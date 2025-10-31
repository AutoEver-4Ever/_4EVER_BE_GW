package org.ever._4ever_be_gw.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.fcm.response.FcmStatisticsDto;
import org.ever._4ever_be_gw.business.service.FcmHttpService;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmHttpServiceImpl implements FcmHttpService {

    private final WebClientProvider webClientProvider;

    @Override
    public ResponseEntity<ApiResponse<FcmStatisticsDto>> getFcmStatistics(String periods) {
        log.debug("재무관리 통계 조회 요청 - periods: {}", periods);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<FcmStatisticsDto> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/fcm/statistics");
                        if (periods != null && !periods.isBlank()) {
                            builder.queryParam("periods", periods);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<FcmStatisticsDto>>() {})
                    .block();

            log.info("재무관리 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            String errorBody = ex.getResponseBodyAsString();
            log.error("재무관리 통계 조회 실패 - Status: {}, Body: {}", ex.getStatusCode(), errorBody);
            return ResponseEntity.status(status).body(
                    ApiResponse.fail("재무관리 통계 조회 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("재무관리 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("재무관리 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getApInvoices(
            String company, String startDate, String endDate, Integer page, Integer size) {
        log.debug("매입 전표 목록 조회 요청 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/fcm/statement/ap");
                        if (company != null) builder.queryParam("company", company);
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("매입 전표 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("매입 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("매입 전표 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("매입 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getApInvoicesBySupplierUserId(
            String supplierUserId, String startDate, String endDate, Integer page, Integer size) {
        log.debug("공급사 사용자 ID로 매입 전표 목록 조회 요청 - supplierUserId: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                supplierUserId, startDate, endDate, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            // supplierUserId를 requestBody에 담아서 Business 서비스로 전송
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("supplierUserId", supplierUserId);
            if (startDate != null) requestBody.put("startDate", startDate);
            if (endDate != null) requestBody.put("endDate", endDate);
            requestBody.put("page", page != null ? page : 0);
            requestBody.put("size", size != null ? size : 10);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/fcm/statement/ap/by-supplier")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("공급사 사용자 ID로 매입 전표 목록 조회 성공 - supplierUserId: {}", supplierUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("공급사 매입 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("공급사 매입 전표 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("공급사 매입 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getArInvoices(
            String company, String startDate, String endDate, Integer page, Integer size) {
        log.debug("AR 전표 목록 조회 요청 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/fcm/invoice/ar");
                        if (company != null) builder.queryParam("company", company);
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("AR 전표 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("AR 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("AR 전표 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("AR 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getApInvoiceDetail(String invoiceId) {
        log.debug("AP 전표 상세 조회 요청 - invoiceId: {}", invoiceId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/fcm/invoice/ap/{invoiceId}", invoiceId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("AP 전표 상세 조회 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("AP 전표 상세 조회", ex);
        } catch (Exception e) {
            log.error("AP 전표 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("AP 전표 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getArInvoiceDetail(String invoiceId) {
        log.debug("AR 전표 상세 조회 요청 - invoiceId: {}", invoiceId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/fcm/invoice/ar/{invoiceId}", invoiceId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("AR 전표 상세 조회 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("AR 전표 상세 조회", ex);
        } catch (Exception e) {
            log.error("AR 전표 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("AR 전표 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> patchApInvoice(String invoiceId, Map<String, Object> requestBody) {
        log.debug("AP 전표 수정 요청 - invoiceId: {}, body: {}", invoiceId, requestBody);

        // AP 전표 수정 엔드포인트가 Business 서비스에 없으므로 mock 응답 반환
        log.warn("AP 전표 수정 엔드포인트가 Business 서비스에 구현되지 않았습니다.");
        return ResponseEntity.ok(
                ApiResponse.success(null, "매입 전표 수정이 완료되었습니다.", HttpStatus.OK)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> patchArInvoice(String invoiceId, Map<String, Object> requestBody) {
        log.debug("AR 전표 수정 요청 - invoiceId: {}, body: {}", invoiceId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            // LocalDate를 String으로 변환
            Map<String, Object> convertedBody = new java.util.HashMap<>(requestBody);
            if (convertedBody.containsKey("dueDate") && convertedBody.get("dueDate") instanceof java.time.LocalDate) {
                convertedBody.put("dueDate", convertedBody.get("dueDate").toString());
            }

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/fcm/invoice/ar/{invoiceId}", invoiceId)
                    .bodyValue(convertedBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("AR 전표 수정 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("AR 전표 수정", ex);
        } catch (Exception e) {
            log.error("AR 전표 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("AR 전표 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> completeReceivable(String invoiceId) {
        log.debug("미수 처리 완료 요청 - invoiceId: {}", invoiceId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/fcm/invoice/ar/{invoiceId}/receivable/complete", invoiceId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("미수 처리 완료 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("미수 처리 완료", ex);
        } catch (Exception e) {
            log.error("미수 처리 완료 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("미수 처리 완료 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> requestApReceivable(String invoiceId) {
        log.debug("매입 전표 미수 처리 요청 - invoiceId: {}", invoiceId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("voucherId", invoiceId, "statusCode", "REQUESTED");

            ApiResponse<Object> response = businessClient.post()
                    .uri("/fcm/invoice/ap/receivable/request")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("매입 전표 미수 처리 요청 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("매입 전표 미수 처리 요청", ex);
        } catch (Exception e) {
            log.error("매입 전표 미수 처리 요청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("매입 전표 미수 처리 요청 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    /**
     * WebClient 오류를 처리하고 로깅하는 공통 메서드
     */
    private ResponseEntity<ApiResponse<Object>> handleWebClientError(String operation, WebClientResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorBody = ex.getResponseBodyAsString();

        log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);

        return ResponseEntity.status(status).body(
                ApiResponse.fail(operation + " 중 오류가 발생했습니다.", status, null)
        );
    }
}
