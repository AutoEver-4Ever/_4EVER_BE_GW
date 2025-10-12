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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SdController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
class SdCustomerUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    private String bodySuccess() {
        return "{\n" +
                "  \"companyName\": \"삼성전자\",\n" +
                "  \"ceo\": \"이재용\",\n" +
                "  \"businessNumber\": \"123-45-67890\",\n" +
                "  \"status\": \"활성\",\n" +
                "  \"contact\": { \"phone\": \"02-1234-5678\", \"address\": \"서울시 강남구 테헤란로 123\", \"email\": \"info@samsung.com\" },\n" +
                "  \"manager\": { \"name\": \"김철수\", \"mobile\": \"010-1234-5678\", \"email\": \"manager@samsung.com\" },\n" +
                "  \"note\": \"주요 거래처\"\n" +
                "}";
    }

    @Test
    @DisplayName("고객사 정보 수정 성공(200)")
    void updateCustomer_success() throws Exception {
        mockMvc.perform(put("/api/business/sd/customers/{customerId}", 1L)
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodySuccess())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("고객사 정보가 수정되었습니다."))
                .andExpect(jsonPath("$.data.customerId").value("C-001"))
                .andExpect(jsonPath("$.data.businessNumber").value("123-45-67890"))
                .andExpect(jsonPath("$.data.status").value("활성"));
    }

    @Test
    @DisplayName("고객사 정보 수정 권한 없음 403(모킹)")
    void updateCustomer_forbidden_403() throws Exception {
        mockMvc.perform(put("/api/business/sd/customers/{customerId}", 403001L)
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodySuccess())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("해당 고객사를 수정할 권한이 없습니다."));
    }

    @Test
    @DisplayName("고객사 정보 수정 미존재 404")
    void updateCustomer_notFound_404() throws Exception {
        mockMvc.perform(put("/api/business/sd/customers/{customerId}", 999L)
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodySuccess())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("고객사를 찾을 수 없습니다: customerId=C-999"));
    }

    @Test
    @DisplayName("고객사 정보 수정 검증 실패 422")
    void updateCustomer_validation_422() throws Exception {
        String bad = "{\n" +
                "  \"companyName\": \"삼성전자\",\n" +
                "  \"businessNumber\": \"1234567890\",\n" +
                "  \"contact\": { \"phone\": \"02-12-5678\" },\n" +
                "  \"manager\": { \"email\": \"invalid\" }\n" +
                "}";

        mockMvc.perform(put("/api/business/sd/customers/{customerId}", 1L)
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bad)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("고객사 정보 수정 서버 오류 500(모킹)")
    void updateCustomer_serverError_500() throws Exception {
        String err = "{ \"companyName\": \"ERROR\" }";
        mockMvc.perform(put("/api/business/sd/customers/{customerId}", 1L)
                        .servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(err)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 알 수 없는 오류가 발생했습니다."));
    }
}

