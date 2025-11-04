package org.ever._4ever_be_gw.scm.pp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.scm.pp.dto.BomCreateRequestDto;
import org.ever._4ever_be_gw.scm.pp.dto.MrpRunConvertRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "생산관리(PP)", description = "생산 관리 API")
@RestController
@RequestMapping("/scm-pp/pp")
@RequiredArgsConstructor
public class PpController {

    private final WebClientProvider webClientProvider;

    // BOM 생성
    @PostMapping("/boms")
    public ResponseEntity<Object> createBom(@RequestBody BomCreateRequestDto requestDto) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/pp/boms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // BOM 목록 조회
    @GetMapping("/boms")
    public ResponseEntity<Object> getBomList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/boms")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // BOM 상세 조회
    @GetMapping("/boms/{bomId}")
    public ResponseEntity<Object> getBomDetail(@PathVariable String bomId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/boms/{bomId}", bomId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // BOM 수정
    @PatchMapping("/boms/{bomId}")
    public ResponseEntity<Object> updateBom(@PathVariable String bomId, @RequestBody BomCreateRequestDto requestDto) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .patch()
                .uri("/scm-pp/pp/boms/{bomId}", bomId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // MES 목록 조회
    @GetMapping("/mes")
    public ResponseEntity<Object> getMesList(
            @RequestParam(required = false) String quotationId,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/mes")
                        .queryParam("quotationId", quotationId)
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

    // MES 상세 조회
    @GetMapping("/mes/{mesId}")
    public ResponseEntity<Object> getMesDetail(@PathVariable String mesId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/mes/{mesId}", mesId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // MES 시작
    @PutMapping("/mes/{mesId}/start")
    public ResponseEntity<Object> startMes(@PathVariable String mesId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mes/{mesId}/start", mesId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // 공정 시작
    @PutMapping("/mes/{mesId}/operations/{operationId}/start")
    public ResponseEntity<Object> startOperation(
            @PathVariable String mesId,
            @PathVariable String operationId,
            @RequestParam(required = false) String managerId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/mes/{mesId}/operations/{operationId}/start")
                        .queryParam("managerId", managerId)
                        .build(mesId, operationId))
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // 공정 완료
    @PutMapping("/mes/{mesId}/operations/{operationId}/complete")
    public ResponseEntity<Object> completeOperation(
            @PathVariable String mesId,
            @PathVariable String operationId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mes/{mesId}/operations/{operationId}/complete", mesId, operationId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // MES 완료
    @PutMapping("/mes/{mesId}/complete")
    public ResponseEntity<Object> completeMes(@PathVariable String mesId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mes/{mesId}/complete", mesId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // MRP → MRP_RUN 계획주문 전환
    @PostMapping("/mrp/convert")
    public ResponseEntity<Object> convertToMrpRun(@RequestBody MrpRunConvertRequestDto requestDto) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/pp/mrp/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // MRP 계획주문 목록 조회
    @GetMapping("/mrp/runs")
    public ResponseEntity<Object> getMrpRunList(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/mrp/runs")
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

    // MRP 계획주문 승인
    @PutMapping("/mrp/runs/{mrpRunId}/approve")
    public ResponseEntity<Object> approveMrpRun(@PathVariable String mrpRunId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mrp/runs/{mrpRunId}/approve", mrpRunId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // MRP 계획주문 거부
    @PutMapping("/mrp/runs/{mrpRunId}/reject")
    public ResponseEntity<Object> rejectMrpRun(@PathVariable String mrpRunId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mrp/runs/{mrpRunId}/reject", mrpRunId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // MRP 계획주문 입고 처리
    @PutMapping("/mrp/runs/{mrpRunId}/receive")
    public ResponseEntity<Object> receiveMrpRun(@PathVariable String mrpRunId) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mrp/runs/{mrpRunId}/receive", mrpRunId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
        return ResponseEntity.ok(result);
    }

    // 견적 목록 조회 (그룹핑)
    @GetMapping("/quotations")
    public ResponseEntity<Object> getQuotationList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/quotations")
                        .queryParam("statusCode", statusCode)
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

    // 견적 시뮬레이션
    @PostMapping("/quotations/simulate")
    public ResponseEntity<Object> simulateQuotations(
            @RequestBody Map<String, Object> requestDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/quotations/simulate")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // MPS 프리뷰 생성
    @PostMapping("/quotations/preview")
    public ResponseEntity<Object> previewMps(@RequestBody List<String> quotationIds) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/pp/quotations/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(quotationIds)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // 견적 확정
    @PostMapping("/quotations/confirm")
    public ResponseEntity<Object> confirmQuotations(@RequestBody Map<String, Object> requestDto) {
        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/pp/quotations/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    // MPS 조회 (주차별)
    @GetMapping("/quotations/mps")
    public ResponseEntity<Object> getMps(
            @RequestParam String bomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {

        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/quotations/mps")
                        .queryParam("bomId", bomId)
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

    // MRP 조회 (자재 조달 계획)
    @GetMapping("/quotations/mrp")
    public ResponseEntity<Object> getMrp(
            @RequestParam(required = false) String bomId,
            @RequestParam(required = false) String quotationId,
            @RequestParam(required = false, defaultValue = "ALL") String availableStatusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Object result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/quotations/mrp")
                        .queryParam("bomId", bomId)
                        .queryParam("quotationId", quotationId)
                        .queryParam("availableStatusCode", availableStatusCode)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }
}
