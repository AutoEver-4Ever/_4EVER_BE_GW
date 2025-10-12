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
class SdQuotationListTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("견적 목록 기본 조회 성공(10건 목업, 페이지 메타 포함)")
    void getQuotations_success_default() throws Exception {
        mockMvc.perform(get("/api/business/sd/quotations")
                        .servletPath("/api")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("견적 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].quotationId").exists())
                .andExpect(jsonPath("$.data.items[0].quotationCode").exists())
                .andExpect(jsonPath("$.data.items[0].customerName").exists())
                .andExpect(jsonPath("$.data.page.number").value(1))
                .andExpect(jsonPath("$.data.page.size").value(10))
                .andExpect(jsonPath("$.data.page.totalElements").value(57))
                .andExpect(jsonPath("$.data.page.totalPages").value(6))
                .andExpect(jsonPath("$.data.page.hasNext").value(true));
    }

    @Test
    @DisplayName("견적 목록 검증 실패 422(status 허용값, 기간 순서, size 초과)")
    void getQuotations_validation_422() throws Exception {
        mockMvc.perform(get("/api/business/sd/quotations")
                        .servletPath("/api")
                        .queryParam("status", "BAD")
                        .queryParam("startDate", "2024-02-01")
                        .queryParam("endDate", "2024-01-31")
                        .queryParam("size", "500")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("견적 목록 서버 오류 500(모킹)")
    void getQuotations_serverError_500() throws Exception {
        mockMvc.perform(get("/api/business/sd/quotations")
                        .servletPath("/api")
                        .queryParam("sort", "error")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("견적 목록 조회 처리 중 서버 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("견적 목록 정렬 asc + size 20도 정상")
    void getQuotations_sortAsc_size20_success() throws Exception {
        mockMvc.perform(get("/api/business/sd/quotations")
                        .servletPath("/api")
                        .queryParam("sort", "quotationDate,asc")
                        .queryParam("page", "1")
                        .queryParam("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("견적 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.page.size").value(20));
    }
}

