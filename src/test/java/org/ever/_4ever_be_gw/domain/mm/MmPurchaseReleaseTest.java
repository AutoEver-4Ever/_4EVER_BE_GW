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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MmController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
@Import(MmPurchaseReleaseTest.MockConfig.class)
class MmPurchaseReleaseTest {

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
    @DisplayName("구매요청 승인 성공")
    void releasePurchaseRequisition_success() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions/{prId}/release", 102345L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-PR_APPROVER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("구매요청서가 승인되었습니다."))
                .andExpect(jsonPath("$.data.id").value(102345))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.approvedBy").value(777))
                .andExpect(jsonPath("$.data.approvedByName").value("김관리자"));
    }

    @Test
    @DisplayName("Authorization 헤더 없으면 401")
    void releasePurchaseRequisition_unauthorized() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions/{prId}/release", 102345L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("승인 권한 없는 토큰이면 403")
    void releasePurchaseRequisition_forbidden() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions/{prId}/release", 102345L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer basic-user-token")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("승인 권한이 없습니다. (required role: PR_APPROVER|PURCHASING_MANAGER|ADMIN)"));
    }

    @Test
    @DisplayName("승인 불가 상태면 422")
    void releasePurchaseRequisition_invalidTransition() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions/{prId}/release", 102347L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-ADMIN-role")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("해당 상태에서는 승인할 수 없습니다."))
                .andExpect(jsonPath("$.errors[*].field", hasItem("status")))
                .andExpect(jsonPath("$.errors[*].reason", hasItem("INVALID_TRANSITION: DRAFT/REJECTED/VOID/APPROVED → APPROVED 불가")));
    }

    @Test
    @DisplayName("존재하지 않는 구매요청이면 404")
    void releasePurchaseRequisition_notFound() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions/{prId}/release", 999999L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-PR_APPROVER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("해당 구매요청서를 찾을 수 없습니다: prId=999999"));
    }

    @Test
    @DisplayName("처리 중 오류 발생 시 500")
    void releasePurchaseRequisition_processingError() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions/{prId}/release", 102399L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-PR_APPROVER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 오류가 발생했습니다."));
    }
}

