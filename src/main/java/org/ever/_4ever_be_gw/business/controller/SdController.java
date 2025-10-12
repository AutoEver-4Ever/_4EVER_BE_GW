package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.business.dto.QuotationRequestDto;
import org.ever._4ever_be_gw.business.dto.QuotationConfirmRequestDto;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.scmpp.dto.PeriodStatDto;
import org.ever._4ever_be_gw.business.dto.SdPeriodMetricsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/business/sd")
@Tag(name = "SD Statistics", description = "영업관리(SD) API")
public class SdController {

    private static final Set<String> ALLOWED_PERIODS = Set.of("week", "month", "quarter", "year");

    @GetMapping("/statistics")
    @Operation(
            summary = "SD 통계 조회",
            description = "주간/월간/분기/연간 영업 통계를 조회합니다. 요청 파라미터가 없으면 모든 기간을 포함합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"OK\",\n  \"data\": {\n    \"week\": {\n      \"sales_amount\":      { \"value\": 152300000, \"delta_rate\": 0.105 },\n      \"new_orders_count\":  { \"value\": 42,        \"delta_rate\": 0.067 }\n    },\n    \"month\": {\n      \"sales_amount\":      { \"value\": 485200000, \"delta_rate\": 0.125 },\n      \"new_orders_count\":  { \"value\": 127,       \"delta_rate\": 0.082 }\n    },\n    \"quarter\": {\n      \"sales_amount\":      { \"value\": 1385200000, \"delta_rate\": 0.047 },\n      \"new_orders_count\":  { \"value\": 392,        \"delta_rate\": 0.031 }\n    },\n    \"year\": {\n      \"sales_amount\":      { \"value\": 5485200000, \"delta_rate\": 0.036 },\n      \"new_orders_count\":  { \"value\": 4217,       \"delta_rate\": 0.028 }\n    }\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 periods",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "invalid_periods", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"요청 파라미터 'periods' 값이 올바르지 않습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, SdPeriodMetricsDto>>> getStatistics(
            @Parameter(name = "periods", description = "조회 기간 목록(콤마 구분)")
            @RequestParam(name = "periods", required = false) String periods
    ) {
        List<String> requested = periods == null || periods.isBlank()
                ? List.of("week", "month", "quarter", "year")
                : Arrays.stream(periods.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        List<String> invalid = requested.stream().filter(p -> !ALLOWED_PERIODS.contains(p)).toList();
        if (periods != null && !periods.isBlank() && (!invalid.isEmpty() || requested.stream().noneMatch(ALLOWED_PERIODS::contains))) {
            throw new BusinessException(ErrorCode.INVALID_PERIODS);
        }

        List<String> finalPeriods = requested.stream().filter(ALLOWED_PERIODS::contains).toList();

        Map<String, SdPeriodMetricsDto> data = new LinkedHashMap<>();
        if (finalPeriods.contains("week")) {
            data.put("week", SdPeriodMetricsDto.builder()
                    .salesAmount(new PeriodStatDto(152_300_000L, new BigDecimal("0.105")))
                    .newOrdersCount(new PeriodStatDto(42L, new BigDecimal("0.067")))
                    .build());
        }
        if (finalPeriods.contains("month")) {
            data.put("month", SdPeriodMetricsDto.builder()
                    .salesAmount(new PeriodStatDto(485_200_000L, new BigDecimal("0.125")))
                    .newOrdersCount(new PeriodStatDto(127L, new BigDecimal("0.082")))
                    .build());
        }
        if (finalPeriods.contains("quarter")) {
            data.put("quarter", SdPeriodMetricsDto.builder()
                    .salesAmount(new PeriodStatDto(1_385_200_000L, new BigDecimal("0.047")))
                    .newOrdersCount(new PeriodStatDto(392L, new BigDecimal("0.031")))
                    .build());
        }
        if (finalPeriods.contains("year")) {
            data.put("year", SdPeriodMetricsDto.builder()
                    .salesAmount(new PeriodStatDto(5_485_200_000L, new BigDecimal("0.036")))
                    .newOrdersCount(new PeriodStatDto(4_217L, new BigDecimal("0.028")))
                    .build());
        }

        return ResponseEntity.ok(ApiResponse.success(data, "OK", HttpStatus.OK));
    }

    @GetMapping("/quotations")
    @Operation(
            summary = "견적 목록 조회",
            description = "견적을 페이지네이션으로 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"견적 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"items\": [\n      {\n        \"quotationId\": 12001,\n        \"quotationCode\": \"Q2024001\",\n        \"customerName\": \"삼성전자\",\n        \"ownerName\": \"김철수\",\n        \"quotationDate\": \"2024-01-15\",\n        \"dueDate\": \"2024-02-15\",\n        \"totalAmount\": 15000000,\n        \"statusCode\": \"PENDING\",\n        \"statusLabel\": \"대기\",\n        \"actions\": [\"view\"]\n      }\n    ],\n    \"page\": { \"number\": 1, \"size\": 10, \"totalElements\": 57, \"totalPages\": 6, \"hasNext\": true }\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"status\", \"reason\": \"ALLOWED_VALUES: PENDING, REVIEW, APPROVED, REJECTED\" },\n    { \"field\": \"startDate/endDate\", \"reason\": \"FROM_AFTER_TO\" },\n    { \"field\": \"size\", \"reason\": \"MAX_200\" }\n  ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"견적 목록 조회 처리 중 서버 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getQuotations(
            @Parameter(description = "시작일(YYYY-MM-DD)")
            @RequestParam(name = "startDate", required = false) String startDate,
            @Parameter(description = "종료일(YYYY-MM-DD)")
            @RequestParam(name = "endDate", required = false) String endDate,
            @Parameter(description = "상태: PENDING, REVIEW, APPROVED, REJECTED")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "검색어(고객명/담당자)")
            @RequestParam(name = "search", required = false) String search,
            @Parameter(description = "정렬 필드,정렬방향")
            @RequestParam(name = "sort", required = false) String sort,
            @Parameter(description = "페이지 번호(1-base)")
            @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기(최대 200)")
            @RequestParam(name = "size", required = false) Integer size
    ) {
        // 422 검증
        List<Map<String, String>> errors = new ArrayList<>();
        java.time.LocalDate from = null;
        java.time.LocalDate to = null;

        if (status != null) {
            var allowed = java.util.Set.of("PENDING", "REVIEW", "APPROVED", "REJECTED");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: PENDING, REVIEW, APPROVED, REJECTED"));
            }
        }
        if (startDate != null) {
            try { from = java.time.LocalDate.parse(startDate); } catch (Exception e) { /* 형식 검증은 생략(요구 사양 기준) */ }
        }
        if (endDate != null) {
            try { to = java.time.LocalDate.parse(endDate); } catch (Exception e) { /* 형식 검증은 생략(요구 사양 기준) */ }
        }
        if (from != null && to != null && from.isAfter(to)) {
            errors.add(Map.of("field", "startDate/endDate", "reason", "FROM_AFTER_TO"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        // 기본값 처리
        String effectiveSort = (sort == null || sort.isBlank()) ? "quotationDate,desc" : sort;
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;

        // 500 모킹 트리거
        if ("error".equalsIgnoreCase(effectiveSort) || "500".equalsIgnoreCase(effectiveSort)) {
            throw new BusinessException(ErrorCode.QUOTATION_LIST_PROCESSING_ERROR);
        }

        // 성공 목업 10건 고정
        List<Map<String, Object>> items = new ArrayList<>();
        String[] customers = {"삼성전자", "LG전자", "현대자동차", "카카오", "네이버", "SK하이닉스", "포스코", "두산중공업", "한화시스템", "CJ대한통운"};
        String[] owners = {"김철수", "이영희", "박민수", "최지훈", "한소라", "정우성", "장나라", "오세훈", "유재석", "아이유"};
        String[] codes = {"PENDING", "REVIEW", "APPROVED", "REJECTED"};
        for (int i = 0; i < 10; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("quotationId", 12001 + i);
            row.put("quotationCode", String.format("Q2024%03d", i + 1));
            row.put("customerName", customers[i % customers.length]);
            row.put("ownerName", owners[i % owners.length]);
            row.put("quotationDate", String.format("2024-01-%02d", 15 + (i % 10)));
            row.put("dueDate", String.format("2024-02-%02d", 10 + (i % 10)));
            row.put("totalAmount", 15_000_000L - (i * 250_000L));
            String statusCode = codes[i % codes.length];
            row.put("statusCode", statusCode);
            String statusLabel = switch (statusCode) {
                case "PENDING" -> "대기";
                case "REVIEW" -> "검토";
                case "APPROVED" -> "승인";
                case "REJECTED" -> "반려";
                default -> "";
            };
            row.put("statusLabel", statusLabel);
            row.put("actions", List.of("view"));
            items.add(row);
        }

        // 페이지 메타 (목업 고정 57건 가정)
        long totalElements = 57L;
        int totalPages = (int) Math.ceil((double) totalElements / Math.max(1, s));
        boolean hasNext = (long) p * s < totalElements;

        Map<String, Object> pageMeta = new LinkedHashMap<>();
        pageMeta.put("number", p);
        pageMeta.put("size", s);
        pageMeta.put("totalElements", totalElements);
        pageMeta.put("totalPages", totalPages);
        pageMeta.put("hasNext", hasNext);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("items", items);
        data.put("page", pageMeta);

        return ResponseEntity.ok(ApiResponse.success(data, "견적 목록 조회에 성공했습니다.", HttpStatus.OK));
    }

    @GetMapping("/quotations/{quotationId}")
    @Operation(
            summary = "견적 상세 조회",
            description = "견적 단건 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"견적 상세 조회에 성공했습니다.\",\n  \"data\": {\n    \"quotationId\": 12001,\n    \"quotationCode\": \"Q2024001\",\n    \"quotationDate\": \"2024-01-15\",\n    \"dueDate\": \"2024-02-15\",\n    \"statusCode\": \"PENDING\",\n    \"statusLabel\": \"대기\",\n    \"customerName\": \"삼성전자\",\n    \"ownerName\": \"김철수\",\n    \"items\": [\n      { \"itemId\": 900001, \"productName\": \"제품 A\", \"quantity\": 10, \"unitPrice\": 500000, \"amount\": 5000000 },\n      { \"itemId\": 900002, \"productName\": \"제품 B\", \"quantity\": 5,  \"unitPrice\": 200000, \"amount\": 1000000 }\n    ],\n    \"totalAmount\": 15000000\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"견적 상세를 조회할 권한이 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "미존재",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 견적을 찾을 수 없습니다: quotationId=12001\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getQuotationDetail(
            @Parameter(description = "견적 ID")
            @org.springframework.web.bind.annotation.PathVariable("quotationId") Long quotationId
    ) {
        // 403 모킹
        if (Long.valueOf(403001L).equals(quotationId)) {
            throw new BusinessException(ErrorCode.QUOTATION_FORBIDDEN);
        }
        // 500 모킹
        if (Long.valueOf(500001L).equals(quotationId)) {
            throw new RuntimeException("boom");
        }
        // 10건 유효 범위: 12001 ~ 12010
        if (quotationId == null || quotationId < 12001L || quotationId > 12010L) {
            throw new BusinessException(ErrorCode.QUOTATION_NOT_FOUND, "quotationId=" + quotationId);
        }

        // 목업 데이터 구성
        String[] customers = {"삼성전자", "LG전자", "현대자동차", "카카오", "네이버", "SK하이닉스", "포스코", "두산중공업", "한화시스템", "CJ대한통운"};
        String[] owners = {"김철수", "이영희", "박민수", "최지훈", "한소라", "정우성", "장나라", "오세훈", "유재석", "아이유"};
        String[] codes = {"PENDING", "REVIEW", "APPROVED", "REJECTED"};

        int idx = (int) ((quotationId - 12001) % 10);
        String quotationCode = String.format("Q2024%03d", (quotationId - 12000));
        String statusCode = codes[idx % codes.length];
        String statusLabel = switch (statusCode) {
            case "PENDING" -> "대기";
            case "REVIEW" -> "검토";
            case "APPROVED" -> "승인";
            case "REJECTED" -> "반려";
            default -> "";
        };

        Map<String, Object> item1 = new LinkedHashMap<>();
        item1.put("itemId", 900001 + idx);
        item1.put("productName", "제품 A");
        item1.put("quantity", 10);
        item1.put("unitPrice", 500_000L);
        item1.put("amount", 5_000_000L);

        Map<String, Object> item2 = new LinkedHashMap<>();
        item2.put("itemId", 900011 + idx);
        item2.put("productName", "제품 B");
        item2.put("quantity", 5);
        item2.put("unitPrice", 200_000L);
        item2.put("amount", 1_000_000L);

        long totalAmount = (Long) item1.get("amount") + (Long) item2.get("amount");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("quotationId", quotationId);
        data.put("quotationCode", quotationCode);
        data.put("quotationDate", "2024-01-15");
        data.put("dueDate", "2024-02-15");
        data.put("statusCode", statusCode);
        data.put("statusLabel", statusLabel);
        data.put("customerName", customers[idx]);
        data.put("ownerName", owners[idx]);
        data.put("items", List.of(item1, item2));
        data.put("totalAmount", totalAmount);

        return ResponseEntity.ok(ApiResponse.success(data, "견적 상세 조회에 성공했습니다.", HttpStatus.OK));
    }

    @PostMapping("/quotations")
    @Operation(
            summary = "신규 견적서 생성",
            description = "요청 양식만 유효하면 200 OK를 반환합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 201,\n  \"success\": true,\n  \"message\": \"신규 견적서 등록이 완료되었습니다.\",\n  \"data\": {\n    \"quotationId\": 12001,\n    \"quotationDate\": \"2025-10-12\",\n    \"dueDate\": \"2025-11-01\",\n    \"totalAmount\": 7000000,\n    \"statusCode\": \"PENDING\",\n    \"statusLabel\": \"대기\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "납기일 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "bad_request", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"요청 납기일은 현재 날짜 이후여야 합니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unprocessable", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"items는 1개 이상이어야 합니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> createQuotation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"dueDate\": \"2025-11-01\",\n  \"items\": [\n    {\n      \"id\": 10001,\n      \"quantity\": 10,\n      \"unitPrice\": 500000\n    },\n    {\n      \"id\": 10002,\n      \"quantity\": 5,\n      \"unitPrice\": 200000\n    }\n  ],\n  \"note\": \"긴급 납품 요청\"\n}"))
            )
            @RequestBody QuotationRequestDto request
    ) {
        java.time.LocalDate today = java.time.LocalDate.now();
        if (request.getDueDate() == null || !request.getDueDate().isAfter(today)) {
            throw new BusinessException(ErrorCode.QUOTATION_DUE_DATE_INVALID);
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.QUOTATION_ITEMS_EMPTY);
        }

        long totalAmount = request.getItems().stream()
                .mapToLong(i -> {
                    long q = i.getQuantity() == null ? 0 : i.getQuantity();
                    long up = i.getUnitPrice() == null ? 0 : i.getUnitPrice();
                    return q * up;
                })
                .sum();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("quotationId", 12001);
        data.put("quotationDate", today.toString());
        data.put("dueDate", request.getDueDate().toString());
        data.put("totalAmount", totalAmount);
        data.put("statusCode", "PENDING");
        data.put("statusLabel", "대기");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "신규 견적서 등록이 완료되었습니다.", HttpStatus.CREATED));
    }

    @PostMapping("/quotations/confirm")
    @Operation(
            summary = "견적 검토 요청",
            description = "선택한 견적들에 대해 검토 요청을 수행합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"견적 검토 요청이 정상적으로 처리되었습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "검토 불가 상태 포함",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "invalid_state", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"요청한 견적 중 검토 요청이 불가능한 상태가 포함되어 있습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "미존재 견적 포함",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"존재하지 않는 견적이 포함되어 있습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> confirmQuotations(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"quotationIds\": [12001, 12002, 12005]\n}"))
            )
            @RequestBody QuotationConfirmRequestDto request
    ) {
        List<Long> ids = request != null ? request.getQuotationIds() : null;

        // 500 모킹 트리거
        if (ids != null && ids.contains(500001L)) {
            throw new RuntimeException("boom");
        }
        // 400: 검토 불가 상태 포함 (모킹용 sentinel) - 우선 처리
        if (ids != null && ids.contains(400001L)) {
            throw new BusinessException(ErrorCode.QUOTATION_CONFIRM_INVALID_STATE);
        }
        // 404: 존재하지 않는 견적 포함 (목업 범위 12001~12010)
        if (ids != null && ids.stream().anyMatch(id -> id < 12001L || id > 12010L)) {
            throw new BusinessException(ErrorCode.QUOTATION_CONFIRM_NOT_FOUND);
        }

        return ResponseEntity.ok(ApiResponse.success(null, "견적 검토 요청이 정상적으로 처리되었습니다.", HttpStatus.OK));
    }
}
