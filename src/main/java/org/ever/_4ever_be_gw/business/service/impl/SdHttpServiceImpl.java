package org.ever._4ever_be_gw.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.service.SdHttpService;
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
public class SdHttpServiceImpl implements SdHttpService {

    private final WebClientProvider webClientProvider;

    // ==================== Statistics ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getDashboardStatistics() {
        log.debug("대시보드 통계 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/sd/dashboard/statistics")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("대시보드 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 통계 조회", ex);
        } catch (Exception e) {
            log.error("대시보드 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getSalesAnalytics(String startDate, String endDate) {
        log.debug("매출 분석 통계 조회 요청 - startDate: {}, endDate: {}", startDate, endDate);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/sd/analytics/sales");
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("매출 분석 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("매출 분석 통계 조회", ex);
        } catch (Exception e) {
            log.error("매출 분석 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("매출 분석 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Customers ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getCustomerList(
            String status, String type, String search, Integer page, Integer size) {
        log.debug("고객사 목록 조회 요청 - status: {}, type: {}, search: {}, page: {}, size: {}",
                status, type, search, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/sd/customers");
                        if (status != null) builder.queryParam("status", status);
                        if (type != null) builder.queryParam("type", type);
                        if (search != null) builder.queryParam("search", search);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 목록 조회", ex);
        } catch (Exception e) {
            log.error("고객사 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createCustomer(Map<String, Object> requestBody) {
        log.debug("고객사 등록 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/customers")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 등록 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 등록", ex);
        } catch (Exception e) {
            log.error("고객사 등록 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getCustomerDetail(String customerId) {
        log.debug("고객사 상세 조회 요청 - customerId: {}", customerId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/sd/customers/{customerId}", customerId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 상세 조회 성공 - customerId: {}", customerId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 상세 조회", ex);
        } catch (Exception e) {
            log.error("고객사 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateCustomer(String customerId, Map<String, Object> requestBody) {
        log.debug("고객사 정보 수정 요청 - customerId: {}, body: {}", customerId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/sd/customers/{customerId}", customerId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 정보 수정 성공 - customerId: {}", customerId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 정보 수정", ex);
        } catch (Exception e) {
            log.error("고객사 정보 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 정보 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> deleteCustomer(String customerId) {
        log.debug("고객사 삭제 요청 - customerId: {}", customerId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.delete()
                    .uri("/sd/customers/{customerId}", customerId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 삭제 성공 - customerId: {}", customerId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 삭제", ex);
        } catch (Exception e) {
            log.error("고객사 삭제 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Orders ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getOrderList(
            String startDate, String endDate, String search, String type, String status, Integer page, Integer size) {
        log.debug("주문 목록 조회 요청 - startDate: {}, endDate: {}, search: {}, type: {}, status: {}, page: {}, size: {}",
                startDate, endDate, search, type, status, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/sd/orders");
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        if (search != null) builder.queryParam("search", search);
                        if (type != null) builder.queryParam("type", type);
                        if (status != null) builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("주문 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("주문 목록 조회", ex);
        } catch (Exception e) {
            log.error("주문 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("주문 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getOrderDetail(String salesOrderId) {
        log.debug("주문서 상세 조회 요청 - salesOrderId: {}", salesOrderId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/sd/orders/{salesOrderId}", salesOrderId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("주문서 상세 조회 성공 - salesOrderId: {}", salesOrderId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("주문서 상세 조회", ex);
        } catch (Exception e) {
            log.error("주문서 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("주문서 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Quotations ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getQuotationList(
            String startDate, String endDate, String status, String type, String search, String sort, Integer page, Integer size) {
        log.debug("견적 목록 조회 요청 - startDate: {}, endDate: {}, status: {}, type: {}, search: {}, sort: {}, page: {}, size: {}",
                startDate, endDate, status, type, search, sort, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/sd/quotations");
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        if (status != null) builder.queryParam("status", status);
                        if (type != null) builder.queryParam("type", type);
                        if (search != null) builder.queryParam("search", search);
                        if (sort != null) builder.queryParam("sort", sort);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적 목록 조회", ex);
        } catch (Exception e) {
            log.error("견적 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getQuotationDetail(String quotationId) {
        log.debug("견적 상세 조회 요청 - quotationId: {}", quotationId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/sd/quotations/{quotationId}", quotationId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적 상세 조회 성공 - quotationId: {}", quotationId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적 상세 조회", ex);
        } catch (Exception e) {
            log.error("견적 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createQuotation(Map<String, Object> requestBody) {
        log.debug("견적서 생성 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            // TODO: JWT 구현 후 토큰에서 userId 추출
            // 임시로 하드코딩된 userId 사용 (Business DB 실제 목 데이터)
            Map<String, Object> enrichedBody = new LinkedHashMap<>(requestBody);
            if (!enrichedBody.containsKey("userId")) {
                enrichedBody.put("userId", "customer1"); // 임시: Business DB의 실제 customer_user ID
                log.debug("임시 userId 추가: {}", enrichedBody.get("userId"));
            }

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations")
                    .bodyValue(enrichedBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적서 생성 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적서 생성", ex);
        } catch (Exception e) {
            log.error("견적서 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적서 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> approveQuotation(String quotationId, Map<String, Object> requestBody) {
        log.debug("견적서 승인 및 주문 생성 요청 - quotationId: {}, body: {}", quotationId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            // TODO: JWT 구현 후 토큰에서 employeeId 추출
            // 임시로 하드코딩된 employeeId 사용 (Business DB 실제 목 데이터)
            Map<String, Object> enrichedBody = new LinkedHashMap<>();
            if (requestBody != null) {
                enrichedBody.putAll(requestBody);
            }
            if (!enrichedBody.containsKey("employeeId")) {
                enrichedBody.put("employeeId", "019a293e-163d-7f6f-9689-16381fba05a7"); // 임시: Business DB의 실제 employee ID (internel1, EMP-001)
                log.info("임시 employeeId 추가: {}", enrichedBody.get("employeeId"));
            }

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations/{quotationId}/approve-order", quotationId)
                    .bodyValue(enrichedBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적서 승인 및 주문 생성 성공 - quotationId: {}", quotationId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적서 승인 및 주문 생성", ex);
        } catch (Exception e) {
            log.error("견적서 승인 및 주문 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적서 승인 및 주문 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> confirmQuotation(Map<String, Object> requestBody) {
        log.debug("견적서 검토 확정 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations/confirm")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적서 검토 확정 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적서 검토 확정", ex);
        } catch (Exception e) {
            log.error("견적서 검토 확정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적서 검토 확정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> rejectQuotation(String quotationId, Map<String, Object> requestBody) {
        log.debug("견적서 거부 요청 - quotationId: {}, body: {}", quotationId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations/{quotationId}/rejected", quotationId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적서 거부 성공 - quotationId: {}", quotationId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적서 거부", ex);
        } catch (Exception e) {
            log.error("견적서 거부 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적서 거부 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> checkInventory(Map<String, Object> requestBody) {
        log.debug("재고 확인 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations/inventory/check")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("재고 확인 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("재고 확인", ex);
        } catch (Exception e) {
            log.error("재고 확인 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("재고 확인 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
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
