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
class SdAnalyticsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("매출 분석 통계 조회 성공(날짜 기반, 6개월 이내)")
    void getSalesAnalytics_success_dateRange() throws Exception {
        mockMvc.perform(get("/api/business/sd/analytics/sales")
                        .servletPath("/api")
                        .queryParam("start", "2025-01-01")
                        .queryParam("end", "2025-05-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("매출 통계 데이터를 조회했습니다."))
                .andExpect(jsonPath("$.data.period.start").value("2025-01-01"))
                .andExpect(jsonPath("$.data.period.end").value("2025-05-31"))
                .andExpect(jsonPath("$.data.trend").isArray())
                .andExpect(jsonPath("$.data.trend.length()").isNumber())
                .andExpect(jsonPath("$.data.productShare").isArray())
                .andExpect(jsonPath("$.data.productShare.length()").value(10))
                .andExpect(jsonPath("$.data.topCustomers").isArray())
                .andExpect(jsonPath("$.data.topCustomers.length()").value(10));
    }

    @Test
    @DisplayName("매출 분석 통계 조회 실패 - 범위 초과(>6개월)")
    void getSalesAnalytics_rangeTooLarge_400_dateRange() throws Exception {
        mockMvc.perform(get("/api/business/sd/analytics/sales")
                        .servletPath("/api")
                        .queryParam("start", "2025-01-01")
                        .queryParam("end", "2025-08-15")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("조회 기간은 최대 6개월까지만 가능합니다."));
    }

    @Test
    @DisplayName("매출 분석 통계 조회 실패 - 서버 오류(모킹, 날짜 기반)")
    void getSalesAnalytics_serverError_500() throws Exception {
        mockMvc.perform(get("/api/business/sd/analytics/sales")
                        .servletPath("/api")
                        .queryParam("start", "5000-01-01")
                        .queryParam("end", "5000-01-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 알 수 없는 오류가 발생했습니다."));
    }
}
