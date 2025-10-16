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
class SdOrderDetailTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("주문서 상세 조회 성공(soId=1)")
    void getOrderDetail_success() throws Exception {
        mockMvc.perform(get("/api/business/sd/orders/{soId}", 1L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("주문서 상세 정보를 조회했습니다."))
                .andExpect(jsonPath("$.data.order.soId").value(1))
                .andExpect(jsonPath("$.data.order.soNumber").value("SO-2024-001"))
                .andExpect(jsonPath("$.data.order.statusCode").value("IN_PRODUCTION"))
                .andExpect(jsonPath("$.data.order.totalAmount").value(15500000))
                .andExpect(jsonPath("$.data.customer.customerName").exists())
                .andExpect(jsonPath("$.data.customer.manager.managerName").exists())
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items.length()").value(2));
    }

    @Test
    @DisplayName("주문서 상세 조회 실패 - 범위 밖(soId=51)")
    void getOrderDetail_notFound() throws Exception {
        mockMvc.perform(get("/api/business/sd/orders/{soId}", 51L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("주문 정보를 찾을 수 없습니다: soId=51"));
    }
}
