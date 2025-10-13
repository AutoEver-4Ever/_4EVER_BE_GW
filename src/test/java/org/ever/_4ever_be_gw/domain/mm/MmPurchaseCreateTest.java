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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MmController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
@Import(MmPurchaseCreateTest.MockConfig.class)
class MmPurchaseCreateTest {

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
                "  \"requesterId\": 123,\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"itemName\": \"A4 복사용지\",\n" +
                "      \"quantity\": 10,\n" +
                "      \"uomName\": \"BOX\",\n" +
                "      \"expectedUnitPrice\": 15000,\n" +
                "      \"expectedTotalPrice\": 150000,\n" +
                "      \"preferredVendorName\": \"OO물산\",\n" +
                "      \"desiredDeliveryDate\": \"" + java.time.LocalDate.now().plusDays(3) + "\",\n" +
                "      \"purpose\": \"사무실 비품 보강\",\n" +
                "      \"note\": \"급히 필요함\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    @Test
    @DisplayName("비재고성 자재 구매요청 생성 성공(201)")
    void createPurchaseRequisition_success() throws Exception {
        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("비재고성 자재 구매요청서가 생성되었습니다."))
                .andExpect(jsonPath("$.data.prId").exists())
                .andExpect(jsonPath("$.data.prNumber").exists())
                .andExpect(jsonPath("$.data.departmentId").value(12))
                .andExpect(jsonPath("$.data.requesterId").value(123));
    }

    @Test
    @DisplayName("요청 본문 형식 오류 400")
    void createPurchaseRequisition_badRequest_400() throws Exception {
        String invalidJson = "{"; // malformed
        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("요청 본문 형식이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("검증 실패 422 - 수량/과거일자")
    void createPurchaseRequisition_validation_422() throws Exception {
        String body = "{\n" +
                "  \"requesterId\": 123,\n" +
                "  \"items\": [\n" +
                "    { \"itemName\": \"A4 복사용지\", \"quantity\": 0, \"desiredDeliveryDate\": \"" + java.time.LocalDate.now().minusDays(1) + "\" }\n" +
                "  ]\n" +
                "}";

        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 본문 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("인증 실패 401(모킹)")
    void createPurchaseRequisition_unauthorized_401() throws Exception {
        String body = "{\n  \"requesterId\": 401001,\n  \"items\": [ { \"itemName\": \"A\", \"quantity\": 1, \"desiredDeliveryDate\": \"" + java.time.LocalDate.now().plusDays(1) + "\" } ]\n}";

        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("유효한 인증 토큰이 필요합니다."));
    }

    @Test
    @DisplayName("서버 오류 500(모킹)")
    void createPurchaseRequisition_serverError_500() throws Exception {
        String body = "{\n  \"requesterId\": 123,\n  \"items\": [ { \"itemName\": \"ERROR\", \"quantity\": 1, \"desiredDeliveryDate\": \"" + java.time.LocalDate.now().plusDays(1) + "\" } ]\n}";

        mockMvc.perform(post("/api/scm-pp/mm/purchase-requisitions").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 알 수 없는 오류가 발생했습니다."));
    }
}

