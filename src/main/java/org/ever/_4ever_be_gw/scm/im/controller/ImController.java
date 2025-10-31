package org.ever._4ever_be_gw.scm.im.controller;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.scm.im.dto.AddInventoryItemRequest;
import org.ever._4ever_be_gw.scm.im.dto.StockTransferRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseCreateRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseUpdateRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.util.Map;

@RestController
@RequestMapping("/scm-pp/iv")
@RequiredArgsConstructor
public class ImController {

    private final WebClientProvider webClientProvider;

    // 재고 목록 조회 (외부 서버)
    @GetMapping("/inventory-items")
    public ResponseEntity<Object> getInventoryItems(
            @io.swagger.v3.oas.annotations.Parameter(description = "검색 타입: WAREHOUSE_NAME 또는 ITEM_NAME")
            @RequestParam(name = "type", required = false) String type,
            @io.swagger.v3.oas.annotations.Parameter(description = "검색 키워드")
            @RequestParam(name = "keyword", required = false) String keyword,
            @io.swagger.v3.oas.annotations.Parameter(description = "재고 상태: ALL, NORMAL, CAUTION, URGENT")
            @RequestParam(name = "statusCode", required = false, defaultValue = "ALL") String statusCode,
            @io.swagger.v3.oas.annotations.Parameter(description = "페이지 번호")
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @io.swagger.v3.oas.annotations.Parameter(description = "페이지 크기")
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/inventory-items")
                        .queryParam("type", type)
                        .queryParam("keyword", keyword)
                        .queryParam("statusCode", statusCode)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 재고 추가 (외부 서버)
    @PostMapping("/items")
    public ResponseEntity<Object> addInventoryItem(@RequestBody AddInventoryItemRequest request) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.post()
                .uri("/scm-pp/iv/items")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 안전재고 수정 (외부 서버)
    @PatchMapping("/items/{itemId}/safety-stock")
    public ResponseEntity<Object> updateSafetyStock(
            @PathVariable String itemId,
            @RequestParam Integer safetyStock
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/items/{itemId}/safety-stock")
                        .queryParam("safetyStock", safetyStock)
                        .build(itemId))
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 재고 상세 정보 조회 (외부 서버)
    @GetMapping("/items/{itemId}")
    public ResponseEntity<Object> getInventoryItemDetail(@PathVariable String itemId) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri("/scm-pp/iv/items/{itemId}", itemId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 부족 재고 목록 조회 (외부 서버)
    @GetMapping("/shortage")
    public ResponseEntity<Object> getShortageItems(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/shortage")
                        .queryParam("status", status)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 부족 재고 간단 정보 조회 (외부 서버)
    @GetMapping("/shortage/preview")
    public ResponseEntity<Object> getShortageItemsPreview() {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/shortage/preview")
                        .queryParam("page", 0)
                        .queryParam("size", 5)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 재고에 존재하지 않는 자재 품목 목록
    @GetMapping("/items/toggle")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "자재 추가 시 자재 토글 목록 조회"
    )
    public ResponseEntity<Object> getItemToggleList() {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/items/toggle")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 입고 완료 목록 조회 (외부 서버)
    @GetMapping("/purchase-orders/received")
    public ResponseEntity<Object> getReceivedPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/purchase-orders/received")
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

    // 입고 대기 목록 조회 (외부 서버)
    @GetMapping("/purchase-orders/receiving")
    public ResponseEntity<Object> getReceivingPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/purchase-orders/receiving")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 생산중 목록 조회 (외부 서버)
    @GetMapping("/sales-orders/production")
    public ResponseEntity<Object> getSalesOrdersInProduction(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/sales-orders/production")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 출고 준비 완료 목록 조회 (외부 서버)
    @GetMapping("/sales-orders/ready-to-ship")
    public ResponseEntity<Object> getReadyToShipSalesOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/sales-orders/ready-to-ship")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 출고 준비 완료 상세 조회 (외부 서버)
    @GetMapping("/sales-orders/ready-to-ship/{salesOrderId}")
    public ResponseEntity<Object> getReadyToShipOrder(@PathVariable String salesOrderId) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri("/scm-pp/iv/sales-orders/ready-to-ship/{salesOrderId}", salesOrderId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 생산중 상세 조회 (외부 서버)
    @GetMapping("/sales-orders/production/{salesOrderId}")
    public ResponseEntity<Object> getProductionOrder(@PathVariable String salesOrderId) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri("/scm-pp/iv/sales-orders/production/{salesOrderId}", salesOrderId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 재고 이동 목록 조회 (외부 서버)
    @GetMapping("/stock-transfers")
    public ResponseEntity<Object> getStockTransfers() {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/stock-transfers")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 창고간 재고 이동 생성 (외부 서버)
    @PostMapping("/stock-transfers")
    public ResponseEntity<Object> createStockTransfer(
            @RequestBody StockTransferRequestDto request,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {

        String requesterId = principal.getUserId();

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/stock-transfers")
                        .queryParam("requesterId", requesterId)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 창고 목록 조회 (외부 서버)
    @GetMapping("/warehouses")
    public ResponseEntity<Object> getWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/warehouses")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 창고 상세 정보 조회 (외부 서버)
    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<Object> getWarehouseDetail(@PathVariable String warehouseId) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri("/scm-pp/iv/warehouses/{warehouseId}", warehouseId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 창고 추가 생성 (외부 서버)
    @PostMapping("/warehouses")
    public ResponseEntity<Object> createWarehouse(
            @RequestBody WarehouseCreateRequestDto request
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.post()
                .uri("/scm-pp/iv/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 창고 정보 수정 수정 (외부 서버)
    @PutMapping("warehouses/{warehouseId}")
    public ResponseEntity<Object> updateWarehouse(
            @PathVariable String warehouseId,
            @RequestBody WarehouseUpdateRequestDto request
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/warehouses/{warehouseId}")
                        .build(warehouseId))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 창고 드롭다운 목록 조회
    @GetMapping("/warehouses/dropdown")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "창고 드롭다운 목록 조회"
    )
    public ResponseEntity<Object> getWarehouseDropdown(@RequestParam(required = false) String warehouseId) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/warehouses/dropdown")
                        .queryParam("warehouseId", warehouseId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

     // 재고 부족 통계 조회
    @GetMapping("/shortage/count/critical/statistic")
    public ResponseEntity<Object> getShortageStatistic() {
        var client = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = client.get()
                .uri("/scm-pp/iv/shortage/count/critical/statistic")
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // IM 통계 조회
    @GetMapping("/statistic")
    public ResponseEntity<Object> getImStatistic() {
        var client = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = client.get()
                .uri("/scm-pp/iv/statistic")
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 창고 통계 조회
    @GetMapping("/warehouses/statistic")
    public ResponseEntity<Object> getWarehouseStatistic() {
        var client = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = client.get()
                .uri("/scm-pp/iv/warehouses/statistic")
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

}