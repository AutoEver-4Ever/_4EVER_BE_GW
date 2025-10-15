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
class SdCustomerDetailTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("고객사 상세 조회 성공(1~10 중 1)")
    void getCustomerDetail_success() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers/{cusId}", 1L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("고객사 상세 정보를 조회했습니다."))
                .andExpect(jsonPath("$.data.customerId").value(1))
                .andExpect(jsonPath("$.data.companyName").value("삼성전자"))
                .andExpect(jsonPath("$.data.ceoName").value("이재용"))
                .andExpect(jsonPath("$.data.contact.phone").value("02-1234-5678"))
                .andExpect(jsonPath("$.data.transaction.totalOrders").value(45));
    }

    @Test
    @DisplayName("고객사 상세 권한 없음 403(모킹)")
    void getCustomerDetail_forbidden() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers/{cusId}", 403001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("해당 고객사를 조회할 권한이 없습니다."));
    }

    @Test
    @DisplayName("고객사 상세 미존재 404")
    void getCustomerDetail_notFound() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers/{cusId}", 999L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("고객사를 찾을 수 없습니다: customerId=999"));
    }

    @Test
    @DisplayName("고객사 상세 서버 오류 500(모킹)")
    void getCustomerDetail_serverError() throws Exception {
        mockMvc.perform(get("/api/business/sd/customers/{cusId}", 500001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 알 수 없는 오류가 발생했습니다."));
    }
}
