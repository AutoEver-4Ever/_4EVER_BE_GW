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
class SdQuotationConfirmTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("견적 검토 요청 성공(200)")
    void confirmQuotations_success() throws Exception {
        String body = "{\n  \"quotationIds\": [12001, 12002, 12005]\n}";

        mockMvc.perform(post("/api/business/sd/quotations/confirm").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("견적 검토 요청이 정상적으로 처리되었습니다."));
    }

    @Test
    @DisplayName("견적 검토 요청 실패 - 검토 불가 상태 포함(400)")
    void confirmQuotations_invalidState_400() throws Exception {
        String body = "{\n  \"quotationIds\": [12001, 400001]\n}";

        mockMvc.perform(post("/api/business/sd/quotations/confirm").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("요청한 견적 중 검토 요청이 불가능한 상태가 포함되어 있습니다."));
    }

    @Test
    @DisplayName("견적 검토 요청 실패 - 미존재 견적 포함(404)")
    void confirmQuotations_notFound_404() throws Exception {
        String body = "{\n  \"quotationIds\": [12001, 13000]\n}";

        mockMvc.perform(post("/api/business/sd/quotations/confirm").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("존재하지 않는 견적이 포함되어 있습니다."));
    }

    @Test
    @DisplayName("견적 검토 요청 실패 - 서버 오류(500)")
    void confirmQuotations_serverError_500() throws Exception {
        String body = "{\n  \"quotationIds\": [500001]\n}";

        mockMvc.perform(post("/api/business/sd/quotations/confirm").servletPath("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}

