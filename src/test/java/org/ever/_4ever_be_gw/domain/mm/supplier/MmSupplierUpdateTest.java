package org.ever._4ever_be_gw.domain.mm.supplier;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MmController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
@Import(MmSupplierUpdateTest.MockConfig.class)
class MmSupplierUpdateTest {

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

    private String body() {
        return "{\n" +
                "  \"statusCode\": \"ACTIVE\",\n" +
                "  \"supplierInfo\": {\n" +
                "    \"supplierName\": \"대한철강\",\n" +
                "    \"supplierBaseAddress\": \"서울특별시 강남구 테헤란로 123\",\n" +
                "    \"supplierDetailAddress\": \"B동 2층\",\n" +
                "    \"deliveryLeadTime\": 3\n" +
                "  },\n" +
                "  \"materialList\": [\n" +
                "    { \"materialName\": \"철강재\" },\n" +
                "    { \"materialName\": \"스테인리스\" }\n" +
                "  ]\n" +
                "}";
    }

    @Test
    @DisplayName("공급업체 정보 수정 성공")
    void updateVendor_success() throws Exception {
        mockMvc.perform(patch("/api/scm-pp/mm/vendors/{vendorId}", 1L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("공급업체 정보를 수정했습니다."))
                .andExpect(jsonPath("$.data.vendorId").value(1))
                .andExpect(jsonPath("$.data.companyName").value("대한철강"))
                .andExpect(jsonPath("$.data.statusCode").value("ACTIVE"))
                .andExpect(jsonPath("$.data.updatedAt").value("2025-10-13T12:00:00Z"));
    }

    @Test
    @DisplayName("Authorization 없으면 401")
    void updateVendor_unauthorized() throws Exception {
        mockMvc.perform(patch("/api/scm-pp/mm/vendors/{vendorId}", 1L)
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("권한 없는 토큰이면 403")
    void updateVendor_forbidden() throws Exception {
        mockMvc.perform(patch("/api/scm-pp/mm/vendors/{vendorId}", 1L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer user-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("공급업체 수정 권한이 없습니다."));
    }

    @Test
    @DisplayName("담당자 필드 수정 시 422")
    void updateVendor_contactFieldsForbidden() throws Exception {
        String invalidBody = "{\n" +
                "  \"managerInfo\": { \"managerName\": \"홍길동\" }\n" +
                "}";

        mockMvc.perform(patch("/api/scm-pp/mm/vendors/{vendorId}", 1L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 본문 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors[*].reason", hasItem("FIELD_NOT_EDITABLE_BY_ADMIN")));
    }

    @Test
    @DisplayName("미존재 공급업체 404")
    void updateVendor_notFound() throws Exception {
        mockMvc.perform(patch("/api/scm-pp/mm/vendors/{vendorId}", 9999L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("수정할 공급업체를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("처리 오류 500")
    void updateVendor_processingError() throws Exception {
        mockMvc.perform(patch("/api/scm-pp/mm/vendors/{vendorId}", 1L)
                        .servletPath("/api")
                        .header("Authorization", "Bearer ADMIN-ERROR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("공급업체 정보 수정 처리 중 오류가 발생했습니다."));
    }
}
