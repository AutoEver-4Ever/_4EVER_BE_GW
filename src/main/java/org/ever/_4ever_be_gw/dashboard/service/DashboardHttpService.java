package org.ever._4ever_be_gw.dashboard.service;

import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.springframework.http.ResponseEntity;

public interface DashboardHttpService {
    /**
     * Get comprehensive dashboard statistics from Business service
     *
     * @return Dashboard statistics response
     */
    ResponseEntity<ApiResponse<DashboardStatisticsResponseDto>> getDashboardStatistics();
}
