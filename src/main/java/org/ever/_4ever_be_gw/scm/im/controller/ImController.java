package org.ever._4ever_be_gw.scm.im.controller;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.scm.im.dto.AddInventoryItemRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String statusCode,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
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
    @PutMapping("/items/{itemId}/safety-stock")
    public ResponseEntity<Object> updateSafetyStock(
            @PathVariable String itemId,
            @RequestParam Integer safetyStock
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.put()
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

    // 입고 대기/완료 목록 조회 (외부 서버)
    @GetMapping("/purchase-orders")
    public ResponseEntity<Object> getPurchaseOrders(
            @RequestParam(required = false, defaultValue = "입고 대기") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/purchase-orders")
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

    // 생산중/출고준비완료/배송중 목록 조회 (외부 서버)
    @GetMapping("/sales-orders/production")
    public ResponseEntity<Object> getSalesOrdersInProduction(
            @RequestParam(required = false, defaultValue = "생산중") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/sales-orders/production")
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

    // 재고 이동 목록 조회 (외부 서버)
    @GetMapping("/stock-transfers")
    public ResponseEntity<Object> getStockTransfers() {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/iv/stock-transfers")
                        .queryParam("page", 0)
                        .queryParam("size", 5)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
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
}