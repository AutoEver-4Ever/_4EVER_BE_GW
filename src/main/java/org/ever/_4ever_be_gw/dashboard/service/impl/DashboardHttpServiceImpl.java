package org.ever._4ever_be_gw.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.ever._4ever_be_gw.dashboard.service.DashboardHttpService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardHttpServiceImpl implements DashboardHttpService {

    private final WebClientProvider webClientProvider;

    @Override
    public ResponseEntity<ApiResponse<DashboardStatisticsResponseDto>> getDashboardStatistics() {
        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<?> response = businessClient.get()
                    .uri("/dashboard/statistics")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<?>>() {})
                    .block();

            log.info("종합 대시보드 통계 조회 성공");
            @SuppressWarnings("unchecked")
            ApiResponse<DashboardStatisticsResponseDto> result = (ApiResponse<DashboardStatisticsResponseDto>) response;
            return ResponseEntity.ok(result);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("종합 대시보드 통계 조회", ex);
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> handleWebClientError(String operation, WebClientResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorBody = ex.getResponseBodyAsString();
        log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);
        return ResponseEntity.status(status).body(
                ApiResponse.fail(operation + " 중 오류가 발생했습니다.", status, null)
        );
    }
}
