package org.ever._4ever_be_gw.domain.mm.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.ever._4ever_be_gw.domain.mm.dto.PeriodMetrics;
import org.ever._4ever_be_gw.domain.mm.dto.PeriodStat;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.domain.mm.service.MmStatisticsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@WebMvcTest(controllers = MmController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.mvc.servlet.path=/api"
})
@Import(MmControllerTest.MockConfig.class)
class MmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MmStatisticsService mmStatisticsService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(mmStatisticsService);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class MockConfig {
        @Bean
        MmStatisticsService mmStatisticsService() {
            return Mockito.mock(MmStatisticsService.class);
        }
    }

    @Test
    @DisplayName("GET /api/scm-pp/mm/statistics 은 기간별 키로 응답한다")
    void getStatistics_returnsPeriodKeyedData() throws Exception {
        // mock service data for all periods
        Map<String, PeriodMetrics> data = new LinkedHashMap<>();
        data.put("week", PeriodMetrics.builder()
                .purchaseRequestCount(new PeriodStat(184L, new BigDecimal("0.0728")))
                .purchaseApprovalPendingCount(new PeriodStat(39L, new BigDecimal("-0.0532")))
                .purchaseOrderAmount(new PeriodStat(1_283_000_000L, new BigDecimal("0.1044")))
                .purchaseOrderApprovalPendingCount(new PeriodStat(22L, new BigDecimal("0.1000")))
                .build());
        data.put("month", PeriodMetrics.builder()
                .purchaseRequestCount(new PeriodStat(736L, new BigDecimal("0.0389")))
                .purchaseApprovalPendingCount(new PeriodStat(161L, new BigDecimal("-0.0417")))
                .purchaseOrderAmount(new PeriodStat(5_214_000_000L, new BigDecimal("0.0361")))
                .purchaseOrderApprovalPendingCount(new PeriodStat(94L, new BigDecimal("0.0652")))
                .build());
        data.put("quarter", PeriodMetrics.builder()
                .purchaseRequestCount(new PeriodStat(2_154L, new BigDecimal("0.0215")))
                .purchaseApprovalPendingCount(new PeriodStat(472L, new BigDecimal("-0.0186")))
                .purchaseOrderAmount(new PeriodStat(15_123_000_000L, new BigDecimal("0.0247")))
                .purchaseOrderApprovalPendingCount(new PeriodStat(281L, new BigDecimal("0.0426")))
                .build());
        data.put("year", PeriodMetrics.builder()
                .purchaseRequestCount(new PeriodStat(8_421L, new BigDecimal("0.0298")))
                .purchaseApprovalPendingCount(new PeriodStat(1_813L, new BigDecimal("-0.0221")))
                .purchaseOrderAmount(new PeriodStat(59_876_000_000L, new BigDecimal("0.0312")))
                .purchaseOrderApprovalPendingCount(new PeriodStat(1_103L, new BigDecimal("0.0185")))
                .build());
        given(mmStatisticsService.getStatistics(anyList())).willReturn(data);

        mockMvc.perform(get("/api/scm-pp/mm/statistics").servletPath("/api")
                        .queryParam("periods", "week,month,quarter,year")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))

                // week
                .andExpect(jsonPath("$.data.week.purchase_request_count.value").value(184))
                .andExpect(jsonPath("$.data.week.purchase_request_count.delta_rate").value(0.0728))
                .andExpect(jsonPath("$.data.week.purchase_approval_pending_count.value").value(39))
                .andExpect(jsonPath("$.data.week.purchase_approval_pending_count.delta_rate").value(-0.0532))
                .andExpect(jsonPath("$.data.week.purchase_order_amount.value").value(1283000000))
                .andExpect(jsonPath("$.data.week.purchase_order_amount.delta_rate").value(0.1044))
                .andExpect(jsonPath("$.data.week.purchase_order_approval_pending_count.value").value(22))
                .andExpect(jsonPath("$.data.week.purchase_order_approval_pending_count.delta_rate").value(0.1000))

                // month
                .andExpect(jsonPath("$.data.month.purchase_request_count.value").value(736))
                .andExpect(jsonPath("$.data.month.purchase_request_count.delta_rate").value(0.0389))
                .andExpect(jsonPath("$.data.month.purchase_approval_pending_count.value").value(161))
                .andExpect(jsonPath("$.data.month.purchase_approval_pending_count.delta_rate").value(-0.0417))
                .andExpect(jsonPath("$.data.month.purchase_order_amount.value").value(5214000000L))
                .andExpect(jsonPath("$.data.month.purchase_order_amount.delta_rate").value(0.0361))
                .andExpect(jsonPath("$.data.month.purchase_order_approval_pending_count.value").value(94))
                .andExpect(jsonPath("$.data.month.purchase_order_approval_pending_count.delta_rate").value(0.0652))

                // quarter
                .andExpect(jsonPath("$.data.quarter.purchase_request_count.value").value(2154))
                .andExpect(jsonPath("$.data.quarter.purchase_request_count.delta_rate").value(0.0215))
                .andExpect(jsonPath("$.data.quarter.purchase_approval_pending_count.value").value(472))
                .andExpect(jsonPath("$.data.quarter.purchase_approval_pending_count.delta_rate").value(-0.0186))
                .andExpect(jsonPath("$.data.quarter.purchase_order_amount.value").value(15123000000L))
                .andExpect(jsonPath("$.data.quarter.purchase_order_amount.delta_rate").value(0.0247))
                .andExpect(jsonPath("$.data.quarter.purchase_order_approval_pending_count.value").value(281))
                .andExpect(jsonPath("$.data.quarter.purchase_order_approval_pending_count.delta_rate").value(0.0426))

                // year
                .andExpect(jsonPath("$.data.year.purchase_request_count.value").value(8421))
                .andExpect(jsonPath("$.data.year.purchase_request_count.delta_rate").value(0.0298))
                .andExpect(jsonPath("$.data.year.purchase_approval_pending_count.value").value(1813))
                .andExpect(jsonPath("$.data.year.purchase_approval_pending_count.delta_rate").value(-0.0221))
                .andExpect(jsonPath("$.data.year.purchase_order_amount.value").value(59876000000L))
                .andExpect(jsonPath("$.data.year.purchase_order_amount.delta_rate").value(0.0312))
                .andExpect(jsonPath("$.data.year.purchase_order_approval_pending_count.value").value(1103))
                .andExpect(jsonPath("$.data.year.purchase_order_approval_pending_count.delta_rate").value(0.0185));
    }

    @Test
    @DisplayName("GET /api/scm-pp/mm/statistics 은 요청한 기간만 포함한다")
    void getStatistics_respectsRequestedPeriods() throws Exception {
        // 테스트 내에서 필요한 데이터만 구성하여 Partial 응답을 준비한다
        Map<String, PeriodMetrics> partial = new LinkedHashMap<>();
        partial.put("week", PeriodMetrics.builder()
                .purchaseRequestCount(new PeriodStat(184L, new BigDecimal("0.0728")))
                .purchaseApprovalPendingCount(new PeriodStat(39L, new BigDecimal("-0.0532")))
                .purchaseOrderAmount(new PeriodStat(1_283_000_000L, new BigDecimal("0.1044")))
                .purchaseOrderApprovalPendingCount(new PeriodStat(22L, new BigDecimal("0.1000")))
                .build());
        partial.put("month", PeriodMetrics.builder()
                .purchaseRequestCount(new PeriodStat(736L, new BigDecimal("0.0389")))
                .purchaseApprovalPendingCount(new PeriodStat(161L, new BigDecimal("-0.0417")))
                .purchaseOrderAmount(new PeriodStat(5_214_000_000L, new BigDecimal("0.0361")))
                .purchaseOrderApprovalPendingCount(new PeriodStat(94L, new BigDecimal("0.0652")))
                .build());
        given(mmStatisticsService.getStatistics(anyList())).willReturn(partial);

        mockMvc.perform(get("/api/scm-pp/mm/statistics").servletPath("/api")
                        .queryParam("periods", "week,month")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.week").exists())
                .andExpect(jsonPath("$.data.month").exists())
                .andExpect(jsonPath("$.data.quarter").doesNotExist())
                .andExpect(jsonPath("$.data.year").doesNotExist());
    }

    @Test
    @DisplayName("기간 파라미터가 유효하지 않으면 400을 반환한다")
    void getStatistics_invalidPeriods_returns400() throws Exception {
        mockMvc.perform(get("/api/scm-pp/mm/statistics").servletPath("/api")
                        .queryParam("periods", "foo,bar")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("요청 파라미터 'periods' 값이 올바르지 않습니다."));
    }

    @Test
    @DisplayName("기간 계산 중 오류가 발생하면 422를 반환한다")
    void getStatistics_calculationError_returns422() throws Exception {
        given(mmStatisticsService.getStatistics(anyList())).willThrow(new BusinessException(ErrorCode.PERIOD_CALCULATION_ERROR, "calc error"));

        mockMvc.perform(get("/api/scm-pp/mm/statistics").servletPath("/api")
                        .queryParam("periods", "week")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value("요청을 처리할 수 없습니다. 기간 계산 중 오류가 발생했습니다."));
    }

    @Test
    @DisplayName("서버 내부 오류가 발생하면 500을 반환한다")
    void getStatistics_serverError_returns500() throws Exception {
        given(mmStatisticsService.getStatistics(anyList())).willThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/scm-pp/mm/statistics").servletPath("/api")
                        .queryParam("periods", "week")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}
