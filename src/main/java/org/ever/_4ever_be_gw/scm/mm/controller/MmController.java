package org.ever._4ever_be_gw.scm.mm.controller;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.common.exception.RemoteApiException;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.scm.mm.dto.PurchaseOrderRejectRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.PurchaseRequisitionCreateRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.PurchaseRequisitionRejectRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.StockPurchaseRequestDto;
import org.ever._4ever_be_gw.scmpp.dto.mm.supplier.SupplierCreateRequestDto;
import org.ever._4ever_be_gw.scm.mm.service.MmService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/scm-pp/mm")
@RequiredArgsConstructor
public class MmController {

    private final WebClientProvider webClientProvider;
    private final MmService mmService;

    // 공급업체 목록 조회
    @GetMapping("/supplier")
    public ResponseEntity<Object> getSupplierList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(defaultValue = "ALL") String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/mm/supplier")
                        .queryParam("statusCode", statusCode)
                        .queryParam("category", category)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 공급업체 상세 조회
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<Object> getSupplierDetail(@PathVariable String supplierId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/mm/supplier/{supplierId}", supplierId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 공급업체 등록 (SAGA)
    @PostMapping("/supplier")
    public Mono<ResponseEntity<ApiResponse<CreateAuthUserResultDto>>> createSupplier(
        @RequestBody SupplierCreateRequestDto requestDto
    ) {
        return mmService.createSupplier(requestDto)
            .map(remoteResponse -> {
                var status = org.springframework.http.HttpStatus.resolve(remoteResponse.getStatus());
                if (status == null) {
                    status = org.springframework.http.HttpStatus.OK;
                }
                String message = remoteResponse.getMessage() != null
                    ? remoteResponse.getMessage()
                    : "공급사 등록이 완료되었습니다.";

                return ResponseEntity.status(status)
                    .body(ApiResponse.success(remoteResponse.getData(), message, status));
            })
            .onErrorResume(RemoteApiException.class, ex -> {
                var status = ex.getStatus() != null ? ex.getStatus() : org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
                ApiResponse<CreateAuthUserResultDto> fail = ApiResponse.fail(
                    ex.getMessage() != null ? ex.getMessage() : "공급사 등록 중 오류가 발생했습니다.",
                    status,
                    ex.getErrors()
                );
                return Mono.just(ResponseEntity.status(status).body(fail));
            })
            .onErrorResume(error -> {
                ApiResponse<CreateAuthUserResultDto> fail = ApiResponse.fail(
                    "공급사 등록 중 오류가 발생했습니다.",
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    error.getMessage()
                );
                return Mono.just(ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(fail));
            });
    }

    // 재고성 구매요청 생성
    @PostMapping("/stock-purchase-requisitions")
    public ResponseEntity<Object> createStockPurchaseRequest(@RequestBody StockPurchaseRequestDto requestDto) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/mm/stock-purchase-requisitions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 구매요청서 목록 조회
    @GetMapping("/purchase-requisitions")
    public ResponseEntity<Object> getPurchaseRequisitionList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/mm/purchase-requisitions")
                        .queryParam("statusCode", statusCode)
                        .queryParam("type", type)
                        .queryParam("keyword", keyword)
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 구매요청서 상세 조회
    @GetMapping("/purchase-requisitions/{purchaseRequisitionId}")
    public ResponseEntity<Object> getPurchaseRequisitionDetail(@PathVariable String purchaseRequisitionId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/mm/purchase-requisitions/{purchaseRequisitionId}", purchaseRequisitionId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 구매요청서 생성
    @PostMapping("/purchase-requisitions")
    public ResponseEntity<Object> createPurchaseRequisition(@RequestBody PurchaseRequisitionCreateRequestDto requestDto) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/mm/purchase-requisitions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 구매요청서 승인
    @PostMapping("/purchase-requisitions/{purchaseRequisitionId}/approve")
    public ResponseEntity<Object> approvePurchaseRequisition(@PathVariable String purchaseRequisitionId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .patch()
                .uri("/scm-pp/mm/purchase-requisitions/{purchaseRequisitionId}/approve", purchaseRequisitionId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 구매요청서 반려
    @PostMapping("/purchase-requisitions/{purchaseRequisitionId}/reject")
    public ResponseEntity<Object> rejectPurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @RequestBody PurchaseRequisitionRejectRequestDto requestDto) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .patch()
                .uri("/scm-pp/mm/purchase-requisitions/{purchaseRequisitionId}/reject", purchaseRequisitionId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 발주서 목록 조회
    @GetMapping("/purchase-orders")
    public ResponseEntity<Object> getPurchaseOrderList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/mm/purchase-orders")
                        .queryParam("statusCode", statusCode)
                        .queryParam("type", type)
                        .queryParam("keyword", keyword)
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 발주서 상세 조회
    @GetMapping("/purchase-orders/{purchaseOrderId}")
    public ResponseEntity<Object> getPurchaseOrderDetail(@PathVariable String purchaseOrderId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/mm/purchase-orders/{purchaseOrderId}", purchaseOrderId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 발주서 승인
    @PostMapping("/purchase-orders/{purchaseOrderId}/approve")
    public ResponseEntity<Object> approvePurchaseOrder(@PathVariable String purchaseOrderId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/mm/purchase-orders/{purchaseOrderId}/approve", purchaseOrderId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 발주서 반려
    @PostMapping("/purchase-orders/{purchaseOrderId}/reject")
    public ResponseEntity<Object> rejectPurchaseOrder(
            @PathVariable String purchaseOrderId,
            @RequestBody PurchaseOrderRejectRequestDto requestDto) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/mm/purchase-orders/{purchaseOrderId}/reject", purchaseOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // MM 통계 조회
    @GetMapping("/statistics")
    public ResponseEntity<Object> getMMStatistics() {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/mm/statistics")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 구매요청서 상태 토글
    @GetMapping("/purchase_requisition/status/toggle")
    public ResponseEntity<Object> getPurchaseRequisitionStatusToggle() {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/mm/purchase_requisition/status/toggle")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 발주서 상태 토글
    @GetMapping("/purchase-orders/status/toggle")
    public ResponseEntity<Object> getPurchaseOrderStatusToggle() {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/mm/purchase-orders/status/toggle")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 공급업체 상태 토글
    @GetMapping("/supplier/status/toggle")
    public ResponseEntity<Object> getSupplierStatusToggle() {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/mm/supplier/status/toggle")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 공급업체 카테고리 토글
    @GetMapping("/supplier/category/toggle")
    public ResponseEntity<Object> getSupplierCategoryToggle() {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/mm/supplier/category/toggle")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }
}
