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
class SdOrderListTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("주문 목록 기본 조회 성공 - DTO 응답 및 페이지 메타 포함")
    void getOrders_success_default() throws Exception {
        mockMvc.perform(get("/api/business/sd/orders")
                        .servletPath("/api")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("주문 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].soId").exists())
                .andExpect(jsonPath("$.data.content[0].soNumber").exists())
                .andExpect(jsonPath("$.data.content[0].customerName").exists())
                .andExpect(jsonPath("$.data.content[0].manager.managerName").exists())
                .andExpect(jsonPath("$.data.page.number").value(0))
                .andExpect(jsonPath("$.data.page.size").value(10))
                .andExpect(jsonPath("$.data.page.totalElements").value(50))
                .andExpect(jsonPath("$.data.page.totalPages").value(5))
                .andExpect(jsonPath("$.data.page.hasNext").value(true));
    }

    @Test
    @DisplayName("주문 목록 상태 필터(IN_PRODUCTION) 및 키워드 검색(soNumber)")
    void getOrders_filter_and_search() throws Exception {
        mockMvc.perform(get("/api/business/sd/orders")
                        .servletPath("/api")
                        .queryParam("status", "IN_PRODUCTION")
                        .queryParam("keyword", "SO-2024-001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("주문 목록 검증 실패 422(날짜 포맷 오류, size 초과)")
    void getOrders_validation_422() throws Exception {
        mockMvc.perform(get("/api/business/sd/orders")
                        .servletPath("/api")
                        .queryParam("startDate", "2024/01/01")
                        .queryParam("endDate", "2024-01-10")
                        .queryParam("size", "500")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청 파라미터 검증에 실패했습니다."));
    }
}
