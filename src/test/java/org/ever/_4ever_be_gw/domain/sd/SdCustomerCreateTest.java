package org.ever._4ever_be_gw.domain.sd;

import org.ever._4ever_be_gw.business.controller.SdController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SdController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
class SdCustomerCreateTest {

    @Autowired
    private MockMvc mockMvc;

    private String requestBody() {
        return "{\n" +
                "  \"companyName\": \"삼성전자\",\n" +
                "  \"businessNumber\": \"123-45-67890\",\n" +
                "  \"ceoName\": \"이재용\",\n" +
                "  \"contactPhone\": \"02-1234-5678\",\n" +
                "  \"contactEmail\": \"contact@samsung.com\",\n" +
                "  \"address\": \"서울시 강남구 테헤란로 123\",\n" +
                "  \"manager\": {\n" +
                "    \"name\": \"김철수\",\n" +
                "    \"mobile\": \"010-1234-5678\",\n" +
                "    \"email\": \"kim@samsung.com\"\n" +
                "  },\n" +
                "  \"note\": \"주요 고객사, 정기 거래처\"\n" +
                "}";
    }

    @Test
    @DisplayName("고객사 등록 성공(201)")
    void createCustomer_success() throws Exception {
        mockMvc.perform(post("/api/business/sd/customers").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("고객사가 등록되었습니다."))
                .andExpect(jsonPath("$.data.customerId").value(501))
                .andExpect(jsonPath("$.data.customerCode").value("C-0001"))
                .andExpect(jsonPath("$.data.companyName").value("삼성전자"))
                .andExpect(jsonPath("$.data.statusCode").value("ACTIVE"))
                .andExpect(jsonPath("$.data.statusLabel").value("활성"))
                .andExpect(jsonPath("$.data.totalOrders").value(0))
                .andExpect(jsonPath("$.data.totalTransactionAmount").value(0))
                .andExpect(jsonPath("$.data.currency").value("KRW"));
    }

    @Test
    @DisplayName("고객사 등록 실패 - 필수 필드 누락(400)")
    void createCustomer_missingRequired_400() throws Exception {
        String body = "{\n  \"companyName\": \"\",\n  \"businessNumber\": \"\",\n  \"ceoName\": \"\",\n  \"address\": \"서울시 강남구\"\n}";

        mockMvc.perform(post("/api/business/sd/customers").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("필수 필드가 누락되었습니다."))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("고객사 등록 실패 - 형식 검증(422)")
    void createCustomer_validation_422() throws Exception {
        String body = "{\n" +
                "  \"companyName\": \"삼성전자\",\n" +
                "  \"businessNumber\": \"1234567890\",\n" +
                "  \"ceoName\": \"이재용\",\n" +
                "  \"contactPhone\": \"02-12-5678\",\n" +
                "  \"contactEmail\": \"contact#samsung.com\",\n" +
                "  \"address\": \"서울시 강남구 테헤란로 123\"\n" +
                "}";

        mockMvc.perform(post("/api/business/sd/customers").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("고객사 등록 실패 - 서버 오류(500)")
    void createCustomer_serverError_500() throws Exception {
        String body = "{\n" +
                "  \"companyName\": \"ERROR\",\n" +
                "  \"businessNumber\": \"123-45-67890\",\n" +
                "  \"ceoName\": \"이재용\"\n" +
                "}";

        mockMvc.perform(post("/api/business/sd/customers").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 알 수 없는 오류가 발생했습니다."));
    }
}

