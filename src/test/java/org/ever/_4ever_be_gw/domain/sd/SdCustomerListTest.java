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
class SdCustomerListTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("고객사 목록 기본 조회 성공")
    void getCustomers_success_default() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers")
                        .servletPath("/api")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("고객사 목록을 조회했습니다."))
                .andExpect(jsonPath("$.data.customers").isArray())
                .andExpect(jsonPath("$.data.page.number").value(0))
                .andExpect(jsonPath("$.data.page.size").value(10));
    }

    @Test
    @DisplayName("고객사 목록 상태 필터 ACTIVE")
    void getCustomers_filter_status_active() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers")
                        .servletPath("/api")
                        .queryParam("status", "ACTIVE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.customers[0].status").value("활성"));
    }

    @Test
    @DisplayName("고객사 목록 키워드 검색")
    void getCustomers_keyword() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers")
                        .servletPath("/api")
                        .queryParam("keyword", "삼성")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.customers").isArray());
    }

    @Test
    @DisplayName("고객사 목록 검증 실패 422")
    void getCustomers_validation_422() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers")
                        .servletPath("/api")
                        .queryParam("status", "BAD")
                        .queryParam("page", "0")
                        .queryParam("size", "500")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."));
    }

    @Test
    @DisplayName("고객사 목록 권한 없음 403(모킹)")
    void getCustomers_forbidden_403() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers")
                        .servletPath("/api")
                        .queryParam("keyword", "금지")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("해당 데이터를 조회할 권한이 없습니다."));
    }

    @Test
    @DisplayName("고객사 목록 서버 오류 500(모킹)")
    void getCustomers_serverError_500() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers")
                        .servletPath("/api")
                        .queryParam("keyword", "error")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 알 수 없는 오류가 발생했습니다."));
    }
}
