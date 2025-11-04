package org.ever._4ever_be_gw.scm.im.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Tag(name = "재고관리(IM)", description = "재고 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/scm-pp")
public class OrderController {

    private final WebClientProvider webClientProvider;

    //입고 완료 목록 조회
    @GetMapping("/purchase-orders/received")
    public ResponseEntity<Object> getReceivedPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/purchase-orders/received")
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

    //  입고 대기 목록 조회
    @GetMapping("/purchase-orders/receiving")
    public ResponseEntity<Object> getReceivingPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/purchase-orders/receiving")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    //  생산중 목록 조회
    @GetMapping("/sales-orders/production")
    public ResponseEntity<Object> getSalesOrdersInProduction(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/sales-orders/production")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    //  출고 준비 완료 목록 조회
    @GetMapping("/sales-orders/ready-to-ship")
    public ResponseEntity<Object> getReadyToShipSalesOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/sales-orders/ready-to-ship")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    //  출고 준비 완료 상세 조회
    @GetMapping("/sales-orders/ready-to-ship/{salesOrderId}")
    public ResponseEntity<Object> getReadyToShipOrder(@PathVariable String salesOrderId) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri("/scm-pp/sales-orders/ready-to-ship/{salesOrderId}", salesOrderId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    //  생산중 상세 조회
    @GetMapping("/sales-orders/production/{salesOrderId}")
    public ResponseEntity<Object> getProductionOrder(@PathVariable String salesOrderId) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = scmPpWebClient.get()
                .uri("/scm-pp/sales-orders/production/{salesOrderId}", salesOrderId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }


    // 창고 통계 조회
    @GetMapping("/product/item/toggle")
    public ResponseEntity<Object> getItemCategoryProducts() {
        var client = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result = client.get()
                .uri("/scm-pp/product/item/toggle")
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }
}
