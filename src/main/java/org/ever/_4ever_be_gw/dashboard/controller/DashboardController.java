package org.ever._4ever_be_gw.dashboard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.ever._4ever_be_gw.dashboard.service.DashboardHttpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardHttpService dashboardHttpService;

    /**
     * Get comprehensive dashboard statistics
     * Proxies request to Business service
     *
     * Includes: total sales, purchases, profit, and employee count
     * Periods: week, month, quarter, year
     * Delta values are absolute differences (not percentages)
     *
     * Example response:
     * {
     *   "week": {
     *     "total_sales": { "value": 1000000, "delta": 50000 },
     *     "total_purchases": { "value": 600000, "delta": 30000 },
     *     "net_profit": { "value": 400000, "delta": 20000 },
     *     "total_employees": { "value": 123, "delta": 11 }
     *   },
     *   "month": { ... },
     *   "quarter": { ... },
     *   "year": { ... }
     * }
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<DashboardStatisticsResponseDto>> getDashboardStatistics() {
        log.info("Gateway: 종합 대시보드 통계 조회 API 호출");
        return dashboardHttpService.getDashboardStatistics();
    }
}
