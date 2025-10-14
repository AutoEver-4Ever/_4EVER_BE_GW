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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SdController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
class SdQuotationDetailTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("견적 상세 조회 성공(12001~12050)")
    void getQuotationDetail_success() throws Exception {
        mockMvc.perform(get("/api/business/sd/quotations/{quotationId}", 12001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("견적 상세 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.quotationId").value(12001))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.totalAmount").value(6000000));
    }

    @Test
    @DisplayName("견적 상세 권한 없음 403(모킹)")
    void getQuotationDetail_forbidden() throws Exception {
        mockMvc.perform(get("/api/business/sd/quotations/{quotationId}", 403001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("견적 상세를 조회할 권한이 없습니다."));
    }

    @Test
    @DisplayName("견적 상세 미존재 404(범위 밖)")
    void getQuotationDetail_notFound() throws Exception {
        mockMvc.perform(get("/api/business/sd/quotations/{quotationId}", 12051L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("해당 견적을 찾을 수 없습니다: quotationId=12051"));
    }

    @Test
    @DisplayName("견적 상세 서버 오류 500(모킹)")
    void getQuotationDetail_serverError() throws Exception {
        mockMvc.perform(get("/api/business/sd/quotations/{quotationId}", 500001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}
