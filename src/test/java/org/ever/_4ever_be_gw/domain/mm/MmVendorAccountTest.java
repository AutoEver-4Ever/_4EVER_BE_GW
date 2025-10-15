package org.ever._4ever_be_gw.domain.mm;

import org.ever._4ever_be_gw.scmpp.controller.MmController;
import org.ever._4ever_be_gw.scmpp.service.MmStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MmController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
@Import(MmVendorAccountTest.MockConfig.class)
class MmVendorAccountTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MmStatisticsService mmStatisticsService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(mmStatisticsService);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class MockConfig {
        @Bean
        MmStatisticsService mmStatisticsService() {
            return Mockito.mock(MmStatisticsService.class);
        }
    }

    @Test
    @DisplayName("공급업체 계정 생성 성공")
    void inviteVendorAccount_success() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors/{vendorId}/account", 101L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-ADMIN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("공급업체 계정이 생성되고 초대 이메일이 발송되었습니다."))
                .andExpect(jsonPath("$.data.vendorId").value(101))
                .andExpect(jsonPath("$.data.vendorCode").value("SUP-2025-0001"))
                .andExpect(jsonPath("$.data.managerEmail").value("contact@everp.com"))
                .andExpect(jsonPath("$.data.tempPassword").value("Abc12345!"))
                .andExpect(jsonPath("$.data.invitedAt").value("2025-10-13T10:05:00Z"));
    }

    @Test
    @DisplayName("Authorization 없으면 401")
    void inviteVendorAccount_unauthorized() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors/{vendorId}/account", 101L)
                        .servletPath("/api"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("권한 없는 토큰이면 403")
    void inviteVendorAccount_forbidden() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors/{vendorId}/account", 101L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("계정 생성 권한이 없습니다."));
    }

    @Test
    @DisplayName("존재하지 않는 공급업체 404")
    void inviteVendorAccount_notFound() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors/{vendorId}/account", 9999L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("해당 공급업체를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("이미 계정 발급된 경우 409")
    void inviteVendorAccount_conflict() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors/{vendorId}/account", 999L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-ADMIN"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("이미 계정이 발급된 공급업체입니다."));
    }

    @Test
    @DisplayName("이메일 발송 실패 500")
    void inviteVendorAccount_processingError() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors/{vendorId}/account", 101L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer ADMIN-ERROR"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("초대 이메일 발송 중 오류가 발생했습니다."));
    }
}
