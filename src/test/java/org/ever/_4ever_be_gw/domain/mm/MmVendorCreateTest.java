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
@Import(MmVendorCreateTest.MockConfig.class)
class MmVendorCreateTest {

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

    private String validBody() {
        return "{\n" +
                "  \"companyName\": \"대한철강\",\n" +
                "  \"category\": \"원자재\",\n" +
                "  \"contactPerson\": \"홍길동\",\n" +
                "  \"contactPhone\": \"02-1234-5678\",\n" +
                "  \"email\": \"contact@koreasteel.com\",\n" +
                "  \"deliveryLeadTime\": 3,\n" +
                "  \"address\": \"서울시 강남구 테헤란로 123\",\n" +
                "  \"materialList\": [\n" +
                "    {\n" +
                "      \"materialName\": \"철강재\",\n" +
                "      \"unit\": \"kg\",\n" +
                "      \"unitPrice\": 1500\n" +
                "    },\n" +
                "    {\n" +
                "      \"materialName\": \"스테인리스\",\n" +
                "      \"unit\": \"kg\",\n" +
                "      \"unitPrice\": 2500\n" +
                "    },\n" +
                "    {\n" +
                "      \"materialName\": \"알루미늄\",\n" +
                "      \"unit\": \"kg\",\n" +
                "      \"unitPrice\": 2200\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Test
    @DisplayName("공급업체 등록 성공")
    void createVendor_success() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors")
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("공급업체가 정상적으로 등록되었습니다."))
                .andExpect(jsonPath("$.data.vendorId").value(101))
                .andExpect(jsonPath("$.data.vendorCode").value("SUP-2025-0001"))
                .andExpect(jsonPath("$.data.companyName").value("대한철강"))
                .andExpect(jsonPath("$.data.contactPerson").value("홍길동"))
                .andExpect(jsonPath("$.data.email").value("contact@koreasteel.com"))
                .andExpect(jsonPath("$.data.createdAt").value("2025-10-13T10:00:00Z"));
    }

    @Test
    @DisplayName("Authorization 없으면 401")
    void createVendor_unauthorized() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors")
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("권한 없는 토큰이면 403")
    void createVendor_forbidden() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors")
                        .servletPath("/api")
                        .header("Authorization", "Bearer user-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("공급업체 등록 권한이 없습니다."));
    }

    @Test
    @DisplayName("검증 실패 422 - 필수값/이메일 형식")
    void createVendor_validationErrors() throws Exception {
        String invalidBody = "{\n" +
                "  \"companyName\": \"\",\n" +
                "  \"email\": \"invalid-email\"\n" +
                "}";

        mockMvc.perform(post("/api/scm-pp/mm/vendors")
                        .servletPath("/api")
                        .header("Authorization", "Bearer token-with-ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors[*].reason", hasItem("필수 입력값입니다.")))
                .andExpect(jsonPath("$.errors[*].reason", hasItem("올바른 이메일 형식이 아닙니다.")));
    }

    @Test
    @DisplayName("처리 중 오류 500(모킹)")
    void createVendor_processingError() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/vendors")
                        .servletPath("/api")
                        .header("Authorization", "Bearer ADMIN-ERROR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("공급업체 등록 처리 중 오류가 발생했습니다."));
    }
}
