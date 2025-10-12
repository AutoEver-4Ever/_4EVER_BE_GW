package org.ever._4ever_be_gw.scmpp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.scmpp.dto.PeriodMetricsDto;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.scmpp.service.MmStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/scm-pp/mm")
@Tag(name = "MM Statistics", description = "MM 통계 조회 API")
public class MmController {

    private static final Set<String> ALLOWED_PERIODS = Set.of("week", "month", "quarter", "year");

    private final MmStatisticsService mmStatisticsService;

    public MmController(MmStatisticsService mmStatisticsService) {
        this.mmStatisticsService = mmStatisticsService;
    }

    @GetMapping("/statistics")
    @Operation(
            summary = "MM 통계 조회",
            description = "주간/월간/분기/연간 통계를 조회합니다. 요청 파라미터가 없으면 모든 기간이 포함됩니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"OK\",\n  \"data\": {\n    \"week\": {\n      \"purchase_request_count\": {\n        \"value\": 184,\n        \"delta_rate\": 0.0728\n      },\n      \"purchase_approval_pending_count\": {\n        \"value\": 39,\n        \"delta_rate\": -0.0532\n      },\n      \"purchase_order_amount\": {\n        \"value\": 1283000000,\n        \"delta_rate\": 0.1044\n      },\n      \"purchase_order_approval_pending_count\": {\n        \"value\": 22,\n        \"delta_rate\": 0.1000\n      }\n    },\n    \"month\": {\n      \"purchase_request_count\": {\n        \"value\": 736,\n        \"delta_rate\": 0.0389\n      },\n      \"purchase_approval_pending_count\": {\n        \"value\": 161,\n        \"delta_rate\": -0.0417\n      },\n      \"purchase_order_amount\": {\n        \"value\": 5214000000,\n        \"delta_rate\": 0.0361\n      },\n      \"purchase_order_approval_pending_count\": {\n        \"value\": 94,\n        \"delta_rate\": 0.0652\n      }\n    },\n    \"quarter\": {\n      \"purchase_request_count\": {\n        \"value\": 2154,\n        \"delta_rate\": 0.0215\n      },\n      \"purchase_approval_pending_count\": {\n        \"value\": 472,\n        \"delta_rate\": -0.0186\n      },\n      \"purchase_order_amount\": {\n        \"value\": 15123000000,\n        \"delta_rate\": 0.0247\n      },\n      \"purchase_order_approval_pending_count\": {\n        \"value\": 281,\n        \"delta_rate\": 0.0426\n      }\n    },\n    \"year\": {\n      \"purchase_request_count\": {\n        \"value\": 8421,\n        \"delta_rate\": 0.0298\n      },\n      \"purchase_approval_pending_count\": {\n        \"value\": 1813,\n        \"delta_rate\": -0.0221\n      },\n      \"purchase_order_amount\": {\n        \"value\": 59876000000,\n        \"delta_rate\": 0.0312\n      },\n      \"purchase_order_approval_pending_count\": {\n        \"value\": 1103,\n        \"delta_rate\": 0.0185\n      }\n    }\n  }\n}" )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 periods",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "invalid_periods", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"요청 파라미터 'periods' 값이 올바르지 않습니다.\",\n  \"errors\": { \"code\": 1007 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "기간 계산 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "period_calc_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청을 처리할 수 없습니다. 기간 계산 중 오류가 발생했습니다.\",\n  \"errors\": { \"code\": 1010 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\",\n  \"errors\": { \"code\": 1005, \"detail\": \"...\" }\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, PeriodMetricsDto>>> getStatistics(
            @Parameter(name = "periods", description = "조회 기간 목록(콤마 구분)")
            @RequestParam(name = "periods", required = false) String periods
    ) {
        // 파라미터 파싱 및 유효성 검증
        List<String> requested = periods == null || periods.isBlank()
                ? List.of("week", "month", "quarter", "year")
                : Arrays.stream(periods.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> invalid = requested.stream()
                .filter(p -> !ALLOWED_PERIODS.contains(p))
                .toList();

        if (periods != null && !periods.isBlank() && (!invalid.isEmpty() || requested.stream().noneMatch(ALLOWED_PERIODS::contains))) {
            throw new BusinessException(ErrorCode.INVALID_PERIODS);
        }

        List<String> finalPeriods = requested.stream()
                .filter(ALLOWED_PERIODS::contains)
                .toList();

        Map<String, PeriodMetricsDto> data = mmStatisticsService.getStatistics(finalPeriods);
        return ResponseEntity.ok(ApiResponse.success(data, "OK", HttpStatus.OK));
    }

    @GetMapping("/purchase-requisitions")
    @Operation(
            summary = "구매요청 목록 조회",
            description = "구매요청서를 페이지네이션으로 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매요청서 목록입니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"id\": 102345,\n        \"prNumber\": \"100002345\",\n        \"requesterId\": 123,\n        \"requesterName\": \"홍길동\",\n        \"departmentId\": 12,\n        \"departmentName\": \"영업1팀\",\n        \"origin\": \"MRP\",\n        \"originRefId\": \"MRP-2025-10-01-00123\",\n        \"createdAt\": \"2025-10-05T12:30:45Z\",\n        \"createdBy\": 123,\n        \"itemCount\": 2,\n        \"hasPreferredVendor\": true\n      },\n      {\n        \"id\": 102346,\n        \"prNumber\": \"100002346\",\n        \"requesterId\": 124,\n        \"requesterName\": \"김민수\",\n        \"departmentId\": 12,\n        \"departmentName\": \"영업1팀\",\n        \"origin\": \"MANUAL\",\n        \"originRefId\": null,\n        \"createdAt\": \"2025-10-05T12:35:02Z\",\n        \"createdBy\": 124,\n        \"itemCount\": 1,\n        \"hasPreferredVendor\": false\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 257,\n      \"totalPages\": 13,\n      \"hasNext\": true\n    }\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 범위의 데이터를 조회할 권한이 없습니다.\",\n  \"errors\": { \"code\": 1009 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"createdFrom\", \"reason\": \"INVALID_DATE\" },\n    { \"field\": \"size\", \"reason\": \"MAX_200\" }\n  ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\",\n  \"errors\": { \"code\": 1005, \"detail\": \"...\" }\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getPurchaseRequisitions(
            @Parameter(description = "상태 필터: PENDING, APPROVED")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "생성일 시작(YYYY-MM-DD)")
            @RequestParam(name = "createdFrom", required = false) String createdFrom,
            @Parameter(description = "생성일 종료(YYYY-MM-DD)")
            @RequestParam(name = "createdTo", required = false) String createdTo,
            @Parameter(description = "정렬 필드,정렬방향")
            @RequestParam(name = "sort", required = false) String sort,
            @Parameter(description = "페이지 번호(0-base)")
            @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기(최대 200)")
            @RequestParam(name = "size", required = false) Integer size
    ) {
        // 422 검증
        List<Map<String, String>> errors = new java.util.ArrayList<>();
        java.time.LocalDate fromDate = null;
        java.time.LocalDate toDate = null;
        if (createdFrom != null) {
            try { fromDate = java.time.LocalDate.parse(createdFrom); } catch (Exception e) {
                errors.add(Map.of("field", "createdFrom", "reason", "INVALID_DATE"));
            }
        }
        if (createdTo != null) {
            try { toDate = java.time.LocalDate.parse(createdTo); } catch (Exception e) {
                errors.add(Map.of("field", "createdTo", "reason", "INVALID_DATE"));
            }
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        // 기본값 처리
        String effectiveSort = (sort == null || sort.isBlank()) ? "createdAt,desc" : sort;
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 20 : size;

        // 403 샘플 조건: 과거 특정 기준 이전 조회는 금지 (모킹)
        if (fromDate != null && fromDate.isBefore(java.time.LocalDate.of(2024, 1, 1))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_RANGE);
        }

        // 성공 응답 (목업) - 10개
        java.util.List<Map<String, Object>> content = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final long id = 102345L + i;
            final long requesterId = 123L + (i % 5);
            final String requesterName = switch (i % 5) {
                case 0 -> "홍길동";
                case 1 -> "김민수";
                case 2 -> "이영희";
                case 3 -> "박철수";
                default -> "최수민";
            };
            final String origin = (i % 2 == 0) ? "MRP" : "MANUAL";
            final String originRef = (i % 2 == 0) ? String.format("MRP-2025-10-01-%05d", 123 + i) : null;
            final int itemCount = 1 + (i % 3);
            final boolean hasPreferred = (i % 2 == 0);
            final java.time.Instant createdAt = java.time.Instant.parse("2025-10-05T12:30:45Z").plusSeconds(60L * i);

            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("id", id);
            row.put("prNumber", String.format("%09d", 100002345 + i));
            row.put("requesterId", requesterId);
            row.put("requesterName", requesterName);
            row.put("departmentId", 12L);
            row.put("departmentName", "영업1팀");
            row.put("origin", origin);
            row.put("originRefId", originRef);
            row.put("createdAt", createdAt);
            row.put("createdBy", requesterId);
            row.put("itemCount", itemCount);
            row.put("hasPreferredVendor", hasPreferred);
            content.add(row);
        }

        Map<String, Object> pageMeta = new LinkedHashMap<>();
        pageMeta.put("number", p);
        pageMeta.put("size", s);
        pageMeta.put("totalElements", 257);
        pageMeta.put("totalPages", 13);
        pageMeta.put("hasNext", (p + 1) < 13);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", content);
        data.put("page", pageMeta);

        return ResponseEntity.ok(ApiResponse.<Object>success(
                data, "구매요청서 목록입니다.", HttpStatus.OK
        ));
    }

    @GetMapping("/purchase-requisitions/{purchaseId}")
    @Operation(
            summary = "구매요청 상세 조회",
            description = "구매요청서 단건 상세를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매요청서 상세입니다.\",\n  \"data\": {\n    \"id\": 1,\n    \"prNumber\": \"PR-2024-001\",\n    \"requesterId\": 123,\n    \"requesterName\": \"김철수\",\n    \"departmentId\": 77,\n    \"departmentName\": \"생산팀\",\n    \"createdAt\": \"2024-01-15T00:00:00Z\",\n    \"desiredDeliveryDate\": \"2024-01-25\",\n    \"status\": \"APPROVED\",\n    \"statusLabel\": \"승인\",\n    \"currency\": \"KRW\",\n    \"items\": [\n      {\n        \"id\": 900001,\n        \"lineNo\": 1,\n        \"itemId\": 40000123,\n        \"itemName\": \"강판\",\n        \"quantity\": 500,\n        \"uomCode\": \"EA\",\n        \"unitPrice\": 5000,\n        \"amount\": 2500000,\n        \"deliveryDate\": \"2024-01-25\",\n        \"note\": null\n      },\n      {\n        \"id\": 900002,\n        \"lineNo\": 2,\n        \"itemId\": 987654321,\n        \"itemName\": \"볼트\",\n        \"quantity\": 100,\n        \"uomCode\": \"EA\",\n        \"unitPrice\": 500,\n        \"amount\": 50000,\n        \"deliveryDate\": \"2024-01-25\",\n        \"note\": null\n      }\n    ],\n    \"totalAmount\": 2550000\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 구매요청서를 조회할 권한이 없습니다.\",\n  \"errors\": { \"code\": 1011 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "리소스 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 구매요청서를 찾을 수 없습니다: purchaseId=11\",\n  \"errors\": { \"code\": 1012 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\",\n  \"errors\": { \"code\": 1005 }\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getPurchaseRequisitionDetail(
            @Parameter(description = "구매요청 ID", example = "1")
            @PathVariable("purchaseId") Long purchaseId
    ) {
        // 모킹된 에러 시나리오
        if (Long.valueOf(403001L).equals(purchaseId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_PURCHASE_ACCESS);
        }
        // 1~10만 유효, 그 외는 404 처리
        if (purchaseId == null || purchaseId < 1 || purchaseId > 10) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "purchaseId=" + purchaseId);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", purchaseId);
        data.put("prNumber", "PR-2024-001");
        data.put("requesterId", 123L);
        data.put("requesterName", "김철수");
        data.put("departmentId", 77L);
        data.put("departmentName", "생산팀");
        data.put("createdAt", java.time.Instant.parse("2024-01-15T00:00:00Z"));
        data.put("desiredDeliveryDate", java.time.LocalDate.parse("2024-01-25"));
        data.put("status", "APPROVED");
        data.put("statusLabel", "승인");
        data.put("currency", "KRW");

        java.util.List<Map<String, Object>> items = new java.util.ArrayList<>();
        items.add(new java.util.LinkedHashMap<>() {{
            put("id", 900001L);
            put("lineNo", 1);
            put("itemId", 40000123L);
            put("itemName", "강판");
            put("quantity", 500);
            put("uomCode", "EA");
            put("unitPrice", 5000);
            put("amount", 2_500_000);
            put("deliveryDate", java.time.LocalDate.parse("2024-01-25"));
            put("note", null);
        }});
        items.add(new java.util.LinkedHashMap<>() {{
            put("id", 900002L);
            put("lineNo", 2);
            put("itemId", 987_654_321L);
            put("itemName", "볼트");
            put("quantity", 100);
            put("uomCode", "EA");
            put("unitPrice", 500);
            put("amount", 50_000);
            put("deliveryDate", java.time.LocalDate.parse("2024-01-25"));
            put("note", null);
        }});
        data.put("items", items);
        data.put("totalAmount", 2_550_000);

        return ResponseEntity.ok(ApiResponse.<Object>success(
                data, "구매요청서 상세입니다.", HttpStatus.OK
        ));
    }

    // ---------------- Purchase Orders List ----------------
    @GetMapping("/purchase-orders")
    @Operation(
            summary = "발주서 목록 조회",
            description = "발주서를 조건에 따라 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"발주서 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"total\": 10,\n    \"page\": 1,\n    \"size\": 10,\n    \"totalPages\": 1,\n    \"hasNext\": false,\n    \"hasPrev\": false,\n    \"orders\": [\n      {\n        \"id\": 1001,\n        \"poNumber\": \"PO-2024-001\",\n        \"supplierName\": \"대한철강\",\n        \"itemsSummary\": \"강판 500kg, 알루미늄 300kg\",\n        \"totalAmount\": 5000000,\n        \"orderDate\": \"2024-01-18\",\n        \"deliveryDate\": \"2024-01-25\",\n        \"priority\": null,\n        \"status\": \"승인됨\"\n      }\n    ]\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{\n  \"status\": 401,\n  \"success\": false,\n  \"message\": \"인증이 필요합니다.\",\n  \"errors\": { \"code\": 1006 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 데이터를 조회할 권한이 없습니다.\",\n  \"errors\": { \"code\": 1013 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [ { \"field\": \"status\", \"reason\": \"ALLOWED_VALUES: APPROVED, PENDING, DELIVERED\" } ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\",\n  \"errors\": { \"code\": 1014 }\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getPurchaseOrders(
            @Parameter(description = "상태 필터: APPROVED,PENDING,DELIVERED")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "주문일 시작(YYYY-MM-DD)")
            @RequestParam(name = "orderDateFrom", required = false) String orderDateFrom,
            @Parameter(description = "주문일 종료(YYYY-MM-DD)")
            @RequestParam(name = "orderDateTo", required = false) String orderDateTo,
            @Parameter(description = "정렬 필드,정렬방향")
            @RequestParam(name = "sort", required = false) String sort,
            @Parameter(description = "페이지 번호(1-base)")
            @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기")
            @RequestParam(name = "size", required = false) Integer size
    ) {
        // 422 검증
        List<Map<String, String>> errors = new java.util.ArrayList<>();
        java.time.LocalDate from = null;
        java.time.LocalDate to = null;
        if (status != null) {
            var allowed = java.util.Set.of("APPROVED", "PENDING", "DELIVERED");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: APPROVED, PENDING, DELIVERED"));
            }
        }
        if (orderDateFrom != null) {
            try { from = java.time.LocalDate.parse(orderDateFrom); } catch (Exception e) {
                errors.add(Map.of("field", "orderDateFrom", "reason", "INVALID_DATE"));
            }
        }
        if (orderDateTo != null) {
            try { to = java.time.LocalDate.parse(orderDateTo); } catch (Exception e) {
                errors.add(Map.of("field", "orderDateTo", "reason", "INVALID_DATE"));
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        // 빈 파라미터 기본값 처리 (Swagger Try-out 시 쿼리 비움)
        String effectiveSort = (sort == null || sort.isBlank()) ? "orderDate,desc" : sort;
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;

        // 403 모킹: 너무 이른 기간 접근 제한
        if (from != null && from.isBefore(java.time.LocalDate.of(2024, 1, 1))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_DATA_ACCESS);
        }

        // 500 모킹 트리거: sort=error,500 등
        if ("error".equalsIgnoreCase(effectiveSort) || "500".equalsIgnoreCase(effectiveSort)) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }

        // 성공: 10개 목업 생성
        java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
        String[][] base = new String[][]{
                {"PO-2024-001","대한철강","강판 500kg, 알루미늄 300kg","2024-01-18","2024-01-25","승인됨"},
                {"PO-2024-002","한국알루미늄","알루미늄 시트 200매","2024-01-17","2024-01-24","대기중"},
                {"PO-2024-003","포스코","고강도 스틸 1톤","2024-01-16","2024-01-23","반려"},
                {"PO-2024-004","효성중공업","볼트 1000개","2024-01-15","2024-01-22","승인됨"},
                {"PO-2024-005","현대제철","스테인리스 파이프 200개","2024-01-14","2024-01-21","대기중"},
                {"PO-2024-006","두산중공업","알루미늄 판재 100매","2024-01-13","2024-01-20","승인됨"},
                {"PO-2024-007","세아베스틸","스틸 코일 3톤","2024-01-12","2024-01-19","반려"},
                {"PO-2024-008","KG동부제철","강철 빔 50개","2024-01-11","2024-01-18","대기중"},
                {"PO-2024-009","동국제강","철판 2톤","2024-01-10","2024-01-17","승인됨"},
                {"PO-2024-010","티엠씨메탈","알루미늄 봉 100개","2024-01-09","2024-01-16","대기중"}
        };
        for (int i = 0; i < 10; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", 1001 + i);
            row.put("poNumber", base[i][0]);
            row.put("supplierName", base[i][1]);
            row.put("itemsSummary", base[i][2]);
            row.put("totalAmount", 5_000_000 - (i * 120_000));
            row.put("orderDate", base[i][3]);
            row.put("deliveryDate", base[i][4]);
            row.put("priority", (i == 2 ? 1 : null));
            row.put("status", base[i][5]);
            list.add(row);
        }

        // 페이지네이션 메타 생성 (1-base)
        int total = list.size();
        int totalPages = (int) Math.ceil((double) total / Math.max(1, s));
        boolean hasNext = p < totalPages;
        boolean hasPrev = p > 1;
        int fromIdx = Math.min((p - 1) * s, total);
        int toIdx = Math.min(fromIdx + s, total);
        java.util.List<Map<String, Object>> pageOrders = list.subList(fromIdx, toIdx);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", total);
        data.put("page", p);
        data.put("size", s);
        data.put("totalPages", totalPages);
        data.put("hasNext", hasNext);
        data.put("hasPrev", hasPrev);
        data.put("orders", pageOrders);

        return ResponseEntity.ok(ApiResponse.<Object>success(data, "발주서 목록 조회에 성공했습니다.", HttpStatus.OK));
    }

    // ---------------- Purchase Order Detail ----------------
    @GetMapping("/purchase-orders/{purchaseId}")
    @Operation(
            summary = "발주서 상세 조회",
            description = "발주서 단건 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"발주서 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"id\": 1,\n    \"poNumber\": \"PO-2024-001\",\n    \"supplierName\": \"대한철강\",\n    \"supplierContact\": \"02-1234-5678\",\n    \"supplierEmail\": \"order@steel.co.kr\",\n    \"orderDate\": \"2024-01-18\",\n    \"deliveryDate\": \"2024-01-25\",\n    \"status\": \"승인됨\",\n    \"totalAmount\": 5000000,\n    \"items\": [\n      { \"itemName\": \"강판\", \"spec\": \"SS400 10mm\", \"quantity\": 500, \"unit\": \"kg\", \"unitPrice\": 8000, \"amount\": 4000000 },\n      { \"itemName\": \"알루미늄\", \"spec\": \"A6061 5mm\", \"quantity\": 300, \"unit\": \"kg\", \"unitPrice\": 3333, \"amount\": 1000000 }\n    ],\n    \"deliveryAddress\": \"경기도 안산시 단원구 공장로 456\",\n    \"requestedDeliveryDate\": \"2024-01-25\",\n    \"specialInstructions\": \"오전 배송 요청\",\n    \"paymentTerms\": \"월말 결제\",\n    \"memo\": \"1월 생산용 원자재 주문\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{\n  \"status\": 401,\n  \"success\": false,\n  \"message\": \"인증이 필요합니다.\",\n  \"errors\": { \"code\": 1006 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 데이터를 조회할 권한이 없습니다.\",\n  \"errors\": { \"code\": 1013 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "리소스 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 발주서를 찾을 수 없습니다: poId=11\",\n  \"errors\": { \"code\": 1015 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\",\n  \"errors\": { \"code\": 1014 }\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getPurchaseOrderDetail(
            @Parameter(description = "발주서 ID", example = "1")
            @PathVariable("purchaseId") Long purchaseId
    ) {
        // 1~10만 존재
        if (purchaseId == null || purchaseId < 1 || purchaseId > 10) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + purchaseId);
        }

        // 목업 데이터 생성
        int idx = (int)((purchaseId - 1) % 10);
        String[] suppliers = {"대한철강","한국알루미늄","포스코","효성중공업","현대제철","두산중공업","세아베스틸","KG동부제철","동국제강","티엠씨메탈"};
        String[] orderDates = {"2024-01-18","2024-01-17","2024-01-16","2024-01-15","2024-01-14","2024-01-13","2024-01-12","2024-01-11","2024-01-10","2024-01-09"};
        String[] deliveryDates = {"2024-01-25","2024-01-24","2024-01-23","2024-01-22","2024-01-21","2024-01-20","2024-01-19","2024-01-18","2024-01-17","2024-01-16"};
        String[] statuses = {"승인됨","대기중","반려","승인됨","대기중","승인됨","반려","대기중","승인됨","대기중"};

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", purchaseId);
        data.put("poNumber", String.format("PO-2024-%03d", 1 + idx));
        data.put("supplierName", suppliers[idx]);
        data.put("supplierContact", "02-1234-5678");
        data.put("supplierEmail", "order@steel.co.kr");
        data.put("orderDate", orderDates[idx]);
        data.put("deliveryDate", deliveryDates[idx]);
        data.put("status", statuses[idx]);

        java.util.List<Map<String, Object>> items = new java.util.ArrayList<>();
        Map<String, Object> item1 = new LinkedHashMap<>();
        item1.put("itemName", "강판");
        item1.put("spec", "SS400 10mm");
        item1.put("quantity", 500);
        item1.put("unit", "kg");
        item1.put("unitPrice", 8000);
        item1.put("amount", 4_000_000);
        items.add(item1);

        Map<String, Object> item2 = new LinkedHashMap<>();
        item2.put("itemName", "알루미늄");
        item2.put("spec", "A6061 5mm");
        item2.put("quantity", 300);
        item2.put("unit", "kg");
        item2.put("unitPrice", 3333);
        item2.put("amount", 1_000_000);
        items.add(item2);

        data.put("items", items);
        data.put("totalAmount", 5_000_000);
        data.put("deliveryAddress", "경기도 안산시 단원구 공장로 456");
        data.put("requestedDeliveryDate", deliveryDates[idx]);
        data.put("specialInstructions", "오전 배송 요청");
        data.put("paymentTerms", "월말 결제");
        data.put("memo", "1월 생산용 원자재 주문");

        return ResponseEntity.ok(ApiResponse.<Object>success(
                data, "발주서 상세 정보 조회에 성공했습니다.", HttpStatus.OK
        ));
    }

    // ---------------- Vendors List ----------------
    @GetMapping("/vendors")
    @Operation(
            summary = "공급업체 목록 조회",
            description = "공급업체 목록을 조건에 따라 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 목록을 조회했습니다.\",\n  \"data\": {\n    \"total\": 10,\n    \"page\": 1,\n    \"size\": 10,\n    \"totalPages\": 1,\n    \"hasNext\": false,\n    \"hasPrev\": false,\n    \"vendors\": [\n      {\n        \"vendorId\": 1,\n        \"companyName\": \"한국철강\",\n        \"contactPhone\": \"02-1234-5678\",\n        \"contactEmail\": \"contact@koreasteel.com\",\n        \"category\": \"원자재\",\n        \"leadTimeDays\": 3,\n        \"leadTimeLabel\": \"3일\",\n        \"statusCode\": \"ACTIVE\",\n        \"statusLabel\": \"활성\",\n        \"actions\": [\"view\"],\n        \"createdAt\": \"2025-10-07T00:00:00Z\",\n        \"updatedAt\": \"2025-10-07T00:00:00Z\"\n      }\n    ]\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{\n  \"status\": 401,\n  \"success\": false,\n  \"message\": \"인증이 필요합니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"공급업체 조회 권한이 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"status\", \"reason\": \"ALLOWED_VALUES: ACTIVE, INACTIVE\" },\n    { \"field\": \"page\", \"reason\": \"MIN_1\" },\n    { \"field\": \"size\", \"reason\": \"MAX_200\" }\n  ]\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getVendors(
            @Parameter(description = "상태 필터: ACTIVE, INACTIVE")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "카테고리 필터", example = "부품")
            @RequestParam(name = "category", required = false) String category,
            @Parameter(description = "페이지(1-base)", example = "1")
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기(최대 200)", example = "10")
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        // 검증
        List<Map<String, String>> errors = new java.util.ArrayList<>();
        if (status != null) {
            var allowed = java.util.Set.of("ACTIVE", "INACTIVE");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: ACTIVE, INACTIVE"));
            }
        }
        if (page != null && page < 1) {
            errors.add(Map.of("field", "page", "reason", "MIN_1"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        // 403 모킹 트리거: category=금지
        if ("금지".equals(category)) {
            throw new BusinessException(ErrorCode.VENDOR_FORBIDDEN);
        }

        // 성공 목업 10개 (기본 5 + 추가 5)
        java.util.List<Map<String, Object>> allVendors = new java.util.ArrayList<>();
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 1);
            put("companyName", "한국철강");
            put("contactPhone", "02-1234-5678");
            put("contactEmail", "contact@koreasteel.com");
            put("category", "원자재");
            put("leadTimeDays", 3);
            put("leadTimeLabel", "3일");
            put("statusCode", "ACTIVE");
            put("statusLabel", "활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));
        }});
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 2);
            put("companyName", "대한전자부품");
            put("contactPhone", "031-987-6543");
            put("contactEmail", "sales@dahanelec.com");
            put("category", "부품");
            put("leadTimeDays", 1);
            put("leadTimeLabel", "1일");
            put("statusCode", "ACTIVE");
            put("statusLabel", "활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-09-15T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-09-20T00:00:00Z"));
        }});
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 3);
            put("companyName", "글로벌화학");
            put("contactPhone", "051-555-0123");
            put("contactEmail", "info@globalchem.co.kr");
            put("category", "원자재");
            put("leadTimeDays", 5);
            put("leadTimeLabel", "5일");
            put("statusCode", "INACTIVE");
            put("statusLabel", "비활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-08-02T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-09-01T00:00:00Z"));
        }});
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 4);
            put("companyName", "한빛소재");
            put("contactPhone", "02-3456-7890");
            put("contactEmail", "info@hanbits.com");
            put("category", "부품");
            put("leadTimeDays", 2);
            put("leadTimeLabel", "2일");
            put("statusCode", "ACTIVE");
            put("statusLabel", "활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-07-10T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-07-20T00:00:00Z"));
        }});
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 5);
            put("companyName", "에이치금속");
            put("contactPhone", "032-222-3333");
            put("contactEmail", "contact@hmetal.co.kr");
            put("category", "원자재");
            put("leadTimeDays", 4);
            put("leadTimeLabel", "4일");
            put("statusCode", "ACTIVE");
            put("statusLabel", "활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-06-01T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-06-05T00:00:00Z"));
        }});
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 6);
            put("companyName", "태성테크");
            put("contactPhone", "02-7777-8888");
            put("contactEmail", "sales@taesung.com");
            put("category", "부품");
            put("leadTimeDays", 7);
            put("leadTimeLabel", "7일");
            put("statusCode", "INACTIVE");
            put("statusLabel", "비활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-05-10T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-05-12T00:00:00Z"));
        }});
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 7);
            put("companyName", "광명산업");
            put("contactPhone", "031-3333-4444");
            put("contactEmail", "contact@kwangmyung.co.kr");
            put("category", "원자재");
            put("leadTimeDays", 6);
            put("leadTimeLabel", "6일");
            put("statusCode", "ACTIVE");
            put("statusLabel", "활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-04-01T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-04-03T00:00:00Z"));
        }});
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 8);
            put("companyName", "한성전자");
            put("contactPhone", "02-2222-1111");
            put("contactEmail", "info@hanseong.com");
            put("category", "부품");
            put("leadTimeDays", 2);
            put("leadTimeLabel", "2일");
            put("statusCode", "ACTIVE");
            put("statusLabel", "활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-03-01T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-03-08T00:00:00Z"));
        }});
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 9);
            put("companyName", "그린케미칼");
            put("contactPhone", "051-777-0000");
            put("contactEmail", "sales@greenchem.co.kr");
            put("category", "원자재");
            put("leadTimeDays", 9);
            put("leadTimeLabel", "9일");
            put("statusCode", "INACTIVE");
            put("statusLabel", "비활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-02-11T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-02-15T00:00:00Z"));
        }});
        allVendors.add(new LinkedHashMap<>() {{
            put("vendorId", 10);
            put("companyName", "아주금속");
            put("contactPhone", "032-101-2020");
            put("contactEmail", "contact@ajumetal.co.kr");
            put("category", "원자재");
            put("leadTimeDays", 10);
            put("leadTimeLabel", "10일");
            put("statusCode", "ACTIVE");
            put("statusLabel", "활성");
            put("actions", java.util.List.of("view"));
            put("createdAt", java.time.Instant.parse("2025-01-20T00:00:00Z"));
            put("updatedAt", java.time.Instant.parse("2025-01-25T00:00:00Z"));
        }});

        // 페이지네이션 슬라이싱 (1-base page)
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int fromIdx = Math.min((p - 1) * s, allVendors.size());
        int toIdx = Math.min(fromIdx + s, allVendors.size());
        java.util.List<Map<String, Object>> vendors = allVendors.subList(fromIdx, toIdx);

        Map<String, Object> data = new LinkedHashMap<>();
        int total = allVendors.size();
        int totalPages = (int) Math.ceil((double) total / Math.max(1, s));
        boolean hasNext = p < totalPages;
        boolean hasPrev = p > 1;
        data.put("total", total);
        data.put("page", p);
        data.put("size", s);
        data.put("totalPages", totalPages);
        data.put("hasNext", hasNext);
        data.put("hasPrev", hasPrev);
        data.put("vendors", vendors);

        return ResponseEntity.ok(ApiResponse.<Object>success(
                data, "공급업체 목록을 조회했습니다.", HttpStatus.OK
        ));
    }

    // ---------------- Vendor Detail ----------------
    @GetMapping("/vendors/{vendorId}")
    @Operation(
            summary = "공급업체 상세 조회",
            description = "공급업체 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"vendorId\": 1,\n    \"companyName\": \"한국철강\",\n    \"contactPhone\": \"02-1234-5678\",\n    \"contactEmail\": \"contact@koreasteel.com\",\n    \"category\": \"원자재\",\n    \"leadTimeDays\": 3,\n    \"leadTimeLabel\": \"3일 소요\",\n    \"statusCode\": \"ACTIVE\",\n    \"statusLabel\": \"활성\",\n    \"materials\": [\"철강재\", \"스테인리스\", \"알루미늄\"],\n    \"createdAt\": \"2025-10-07T00:00:00Z\",\n    \"updatedAt\": \"2025-10-07T00:00:00Z\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{\n  \"status\": 401,\n  \"success\": false,\n  \"message\": \"인증이 필요합니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"공급업체 조회 권한이 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "리소스 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 공급업체를 찾을 수 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"공급업체 조회 처리 중 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getVendorDetail(
            @Parameter(description = "공급업체 ID", example = "1")
            @PathVariable("vendorId") Long vendorId
    ) {
        // 모킹 트리거들
        if (vendorId != null && vendorId == 403001L) {
            throw new BusinessException(ErrorCode.VENDOR_FORBIDDEN);
        }
        if (vendorId != null && vendorId == 500001L) {
            throw new BusinessException(ErrorCode.VENDOR_PROCESSING_ERROR);
        }
        if (vendorId == null || vendorId < 1 || vendorId > 10) {
            throw new BusinessException(ErrorCode.VENDOR_NOT_FOUND);
        }

        int idx = (int)((vendorId - 1) % 10);
        String[] names = {"한국철강","대한전자부품","글로벌화학","한빛소재","에이치금속","태성테크","광명산업","한성전자","그린케미칼","아주금속"};
        String[] categories = {"원자재","부품","원자재","부품","원자재","부품","원자재","부품","원자재","원자재"};
        int[] leadDays = {3,1,5,2,4,7,6,2,9,10};
        String[] statusCode = {"ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE"};
        String[] statusLabel = {"활성","활성","비활성","활성","활성","비활성","활성","활성","비활성","활성"};

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("vendorId", vendorId);
        data.put("companyName", names[idx]);
        data.put("contactPhone", "02-1234-5678");
        data.put("contactEmail", "contact@koreasteel.com");
        data.put("category", categories[idx]);
        data.put("leadTimeDays", leadDays[idx]);
        data.put("leadTimeLabel", leadDays[idx] + "일 소요");
        data.put("statusCode", statusCode[idx]);
        data.put("statusLabel", statusLabel[idx]);
        data.put("materials", java.util.List.of("철강재", "스테인리스", "알루미늄"));
        // 간단한 시계열 생성
        data.put("createdAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));
        data.put("updatedAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));

        return ResponseEntity.ok(ApiResponse.<Object>success(
                data, "공급업체 상세 정보를 조회했습니다.", HttpStatus.OK
        ));
    }
}
