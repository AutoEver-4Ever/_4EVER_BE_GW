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
    @DisplayName("매출 분석 통계 조회 성공(12주 이내)")
    void getSalesAnalytics_success() throws Exception {
        mockMvc.perform(get("/api/business/sd/analytics/sales")
                        .servletPath("/api")
                        .queryParam("startYear", "2025")
                        .queryParam("startWeek", "10")
                        .queryParam("endYear", "2025")
                        .queryParam("endWeek", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("매출 통계 데이터를 조회했습니다."))
                .andExpect(jsonPath("$.data.trend").isArray())
                .andExpect(jsonPath("$.data.trend[0].year").value(2025))
                .andExpect(jsonPath("$.data.trend[0].week").value(10))
                .andExpect(jsonPath("$.data.productShare").isArray())
                .andExpect(jsonPath("$.data.topCustomers").isArray());
    }

    @Test
    @DisplayName("매출 분석 통계 조회 실패 - 범위 초과(>12주)")
    void getSalesAnalytics_rangeTooLarge_400() throws Exception {
        mockMvc.perform(get("/api/business/sd/analytics/sales")
                        .servletPath("/api")
                        .queryParam("startYear", "2024")
                        .queryParam("startWeek", "1")
                        .queryParam("endYear", "2024")
                        .queryParam("endWeek", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("조회 기간은 최대 12주(3개월)까지만 가능합니다."));
    }

    @Test
    @DisplayName("매출 분석 통계 조회 실패 - 서버 오류(모킹)")
    void getSalesAnalytics_serverError_500() throws Exception {
        mockMvc.perform(get("/api/business/sd/analytics/sales")
                        .servletPath("/api")
                        .queryParam("startYear", "5000")
                        .queryParam("startWeek", "1")
                        .queryParam("endYear", "2025")
                        .queryParam("endWeek", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("요청 처리 중 알 수 없는 오류가 발생했습니다."));
    }
}

