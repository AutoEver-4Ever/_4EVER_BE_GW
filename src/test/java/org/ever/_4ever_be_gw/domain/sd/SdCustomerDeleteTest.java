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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SdController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
class SdCustomerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("고객사 삭제 성공(200)")
    void deleteCustomer_success() throws Exception {
        mockMvc.perform(delete("/api/business/sd/customers/{customerId}", 1L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("고객사 정보가 삭제되었습니다."));
    }

    @Test
    @DisplayName("고객사 삭제 권한 없음 403(모킹)")
    void deleteCustomer_forbidden_403() throws Exception {
        mockMvc.perform(delete("/api/business/sd/customers/{customerId}", 403001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("해당 고객사를 삭제할 권한이 없습니다."));
    }

    @Test
    @DisplayName("고객사 삭제 미존재 404")
    void deleteCustomer_notFound_404() throws Exception {
        mockMvc.perform(delete("/api/business/sd/customers/{customerId}", 10000L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("고객사를 찾을 수 없습니다: customerId=10000"));
    }

    @Test
    @DisplayName("고객사 삭제 충돌 409(거래 내역 존재)")
    void deleteCustomer_conflict_409() throws Exception {
        mockMvc.perform(delete("/api/business/sd/customers/{customerId}", 409001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("해당 고객사는 거래 내역이 존재하여 삭제할 수 없습니다."));
    }

    @Test
    @DisplayName("고객사 삭제 서버 오류 500(모킹)")
    void deleteCustomer_serverError_500() throws Exception {
        mockMvc.perform(delete("/api/business/sd/customers/{customerId}", 500001L)
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 알 수 없는 오류가 발생했습니다."));
    }
}

