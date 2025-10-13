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
class SdStatisticsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("SD 통계 전체 조회 성공")
    void getStatistics_all_success() throws Exception {
        mockMvc.perform(get("/api/business/sd/statistics")
                        .servletPath("/api")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                // week
                .andExpect(jsonPath("$.data.week.sales_amount.value").value(152300000))
                .andExpect(jsonPath("$.data.week.sales_amount.delta_rate").value(0.105))
                .andExpect(jsonPath("$.data.week.new_orders_count.value").value(42))
                .andExpect(jsonPath("$.data.week.new_orders_count.delta_rate").value(0.067))
                // month
                .andExpect(jsonPath("$.data.month.sales_amount.value").value(485200000))
                .andExpect(jsonPath("$.data.month.new_orders_count.value").value(127))
                // quarter key exists
                .andExpect(jsonPath("$.data.quarter").exists())
                // year key exists
                .andExpect(jsonPath("$.data.year").exists());
    }

    @Test
    @DisplayName("SD 통계 선택 기간만 포함")
    void getStatistics_selectedPeriods_success() throws Exception {
        mockMvc.perform(get("/api/business/sd/statistics")
                        .servletPath("/api")
                        .queryParam("periods", "week,month")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.week").exists())
                .andExpect(jsonPath("$.data.month").exists())
                .andExpect(jsonPath("$.data.quarter").doesNotExist())
                .andExpect(jsonPath("$.data.year").doesNotExist());
    }

    @Test
    @DisplayName("SD 통계 잘못된 periods는 400 반환")
    void getStatistics_invalidPeriods_400() throws Exception {
        mockMvc.perform(get("/api/business/sd/statistics")
                        .servletPath("/api")
                        .queryParam("periods", "foo,bar")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("요청 파라미터 'periods' 값이 올바르지 않습니다."));
    }
}

