package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.business.dto.QuotationRequestDto;
import org.ever._4ever_be_gw.business.dto.QuotationConfirmRequestDto;
import org.ever._4ever_be_gw.business.dto.CustomerCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.CustomerUpdateRequestDto;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.common.util.PageResponseUtils;
import org.ever._4ever_be_gw.scmpp.dto.PeriodStatDto;
import org.ever._4ever_be_gw.business.dto.SdPeriodMetricsDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/business/sd")
@Tag(name = "영업관리(SD)", description = "영업관리(SD) API")
    public class SdController {

    private static final Set<String> ALLOWED_PERIODS = Set.of("week", "month", "quarter", "year");

    

    

    // -------- Statistics (R) --------
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

    // (moved to end)

    @GetMapping("/quotations")
    @Operation(
            summary = "견적 목록 조회",
            description = "견적을 페이지네이션으로 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"견적 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"items\": [\n      {\n        \"quotationId\": 12001,\n        \"quotationCode\": \"Q2024001\",\n        \"customerName\": \"삼성전자\",\n        \"ownerName\": \"김철수\",\n        \"quotationDate\": \"2024-01-15\",\n        \"dueDate\": \"2024-02-15\",\n        \"totalAmount\": 15000000,\n        \"statusCode\": \"PENDING\",\n        \"statusLabel\": \"대기\",\n        \"actions\": [\"view\"]\n      }\n    ],\n    \"page\": { \"number\": 0, \"size\": 10, \"totalElements\": 57, \"totalPages\": 6, \"hasNext\": true }\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"status\", \"reason\": \"ALLOWED_VALUES: PENDING, REVIEW, APPROVED, REJECTED, ALL\" },\n    { \"field\": \"startDate/endDate\", \"reason\": \"FROM_AFTER_TO\" },\n    { \"field\": \"size\", \"reason\": \"MAX_200\" }\n  ]\n}"))
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
            @Parameter(description = "상태: PENDING, REVIEW, APPROVED, REJECTED, ALL")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "검색어(고객명/담당자)")
            @RequestParam(name = "search", required = false) String search,
            @Parameter(description = "정렬 필드,정렬방향")
            @RequestParam(name = "sort", required = false) String sort,
            @Parameter(description = "페이지 번호(0-base)")
            @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기(최대 200)")
            @RequestParam(name = "size", required = false) Integer size
    ) {
        // 422 검증
        List<Map<String, String>> errors = new ArrayList<>();
        java.time.LocalDate from = null;
        java.time.LocalDate to = null;

        if (status != null) {
            var allowed = java.util.Set.of("PENDING", "REVIEW", "APPROVED", "REJECTED", "ALL");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: PENDING, REVIEW, APPROVED, REJECTED, ALL"));
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
        int pageIndex = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 10 : size;

        // 500 모킹 트리거
        if ("error".equalsIgnoreCase(effectiveSort) || "500".equalsIgnoreCase(effectiveSort)) {
            throw new BusinessException(ErrorCode.QUOTATION_LIST_PROCESSING_ERROR);
        }

        // 성공 목업 57건 생성
        List<Map<String, Object>> items = new ArrayList<>();
        String[] customers = {"삼성전자", "LG전자", "현대자동차", "카카오", "네이버", "SK하이닉스", "포스코", "두산중공업", "한화시스템", "CJ대한통운"};
        String[] owners = {"김철수", "이영희", "박민수", "최지훈", "한소라", "정우성", "장나라", "오세훈", "유재석", "아이유"};
        String[] codes = {"PENDING", "REVIEW", "APPROVED", "REJECTED"};
        for (int i = 0; i < 57; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("quotationId", 12001 + i);
            row.put("quotationCode", String.format("Q2024%03d", i + 1));
            row.put("customerName", customers[i % customers.length]);
            row.put("ownerName", owners[i % owners.length]);
            row.put("quotationDate", String.format("2024-01-%02d", 1 + (i % 28)));
            row.put("dueDate", String.format("2024-02-%02d", 1 + (i % 28)));
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

        // 필터 적용: status(ALL은 전체), 날짜 범위, 검색어(customerName/ownerName/quotationCode)
        List<Map<String, Object>> filtered = items;
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            final String st = status.toUpperCase(Locale.ROOT);
            filtered = filtered.stream()
                    .filter(m -> st.equals(String.valueOf(m.get("statusCode"))))
                    .toList();
        }
        if (from != null) {
            final java.time.LocalDate minDate = from;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(String.valueOf(m.get("quotationDate"))).isBefore(minDate))
                    .toList();
        }
        if (to != null) {
            final java.time.LocalDate maxDate = to;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(String.valueOf(m.get("quotationDate"))).isAfter(maxDate))
                    .toList();
        }
        if (search != null && !search.isBlank()) {
            final String kw = search.toLowerCase(Locale.ROOT);
            filtered = filtered.stream()
                    .filter(m -> String.valueOf(m.get("customerName")).toLowerCase(Locale.ROOT).contains(kw)
                              || String.valueOf(m.get("ownerName")).toLowerCase(Locale.ROOT).contains(kw)
                              || String.valueOf(m.get("quotationCode")).toLowerCase(Locale.ROOT).contains(kw))
                    .toList();
        }

        // 정렬 적용: quotationDate|dueDate|totalAmount + asc|desc
        String[] sortParts = effectiveSort.split(",");
        String sortField = sortParts[0].trim();
        String sortDirection = sortParts.length > 1 ? sortParts[1].trim().toLowerCase(Locale.ROOT) : "desc";
        java.util.Comparator<Map<String, Object>> comparator;
        switch (sortField) {
            case "dueDate" -> comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(String.valueOf(m.get("dueDate"))));
            case "totalAmount" -> comparator = java.util.Comparator.comparing(m -> ((Number) m.get("totalAmount")).longValue());
            case "quotationDate" -> comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(String.valueOf(m.get("quotationDate"))));
            default -> comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(String.valueOf(m.get("quotationDate"))));
        }
        if ("desc".equals(sortDirection)) {
            comparator = comparator.reversed();
        }
        filtered = filtered.stream().sorted(comparator).toList();

        // 페이지네이션 적용
        int total = filtered.size();
        int fromIdx = Math.min(pageIndex * s, total);
        int toIdx = Math.min(fromIdx + s, total);
        List<Map<String, Object>> pageItems = filtered.subList(fromIdx, toIdx);

        Map<String, Object> pageMeta = PageResponseUtils.buildPage(pageIndex, s, total);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("items", pageItems);
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

    @PostMapping("/customers")
    @Operation(
            summary = "고객사 등록",
            description = "고객사 정보를 신규 등록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 201,\n  \"success\": true,\n  \"message\": \"고객사가 등록되었습니다.\",\n  \"data\": {\n    \"customerId\": 501,\n    \"customerCode\": \"C-0001\",\n    \"companyName\": \"삼성전자\",\n    \"ceoName\": \"이재용\",\n    \"businessNumber\": \"123-45-67890\",\n    \"statusCode\": \"ACTIVE\",\n    \"statusLabel\": \"활성\",\n    \"contactPhone\": \"02-1234-5678\",\n    \"contactEmail\": \"contact@samsung.com\",\n    \"address\": \"서울시 강남구 테헤란로 123\",\n    \"manager\": { \"name\": \"김철수\", \"mobile\": \"010-1234-5678\", \"email\": \"kim@samsung.com\" },\n    \"totalOrders\": 0,\n    \"totalTransactionAmount\": 0,\n    \"currency\": \"KRW\",\n    \"note\": \"주요 고객사, 정기 거래처\",\n    \"createdAt\": \"2025-10-12T12:34:56Z\",\n    \"updatedAt\": \"2025-10-12T12:34:56Z\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "필수 필드 누락",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "missing_required", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"필수 필드가 누락되었습니다.\",\n  \"errors\": [\n    { \"field\": \"companyName\", \"reason\": \"REQUIRED\" },\n    { \"field\": \"businessNumber\", \"reason\": \"REQUIRED\" },\n    { \"field\": \"ceoName\", \"reason\": \"REQUIRED\" },\n    { \"field\": \"contactPhone/contactEmail\", \"reason\": \"AT_LEAST_ONE_REQUIRED\" }\n  ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "형식 검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"businessNumber\", \"reason\": \"INVALID_FORMAT (###-##-#####)\" },\n    { \"field\": \"contactEmail\", \"reason\": \"INVALID_EMAIL\" },\n    { \"field\": \"contactPhone\", \"reason\": \"INVALID_PHONE\" }\n  ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> createCustomer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"companyName\": \"삼성전자\",\n  \"businessNumber\": \"123-45-67890\",\n  \"ceoName\": \"이재용\",\n  \"contactPhone\": \"02-1234-5678\",\n  \"contactEmail\": \"contact@samsung.com\",\n  \"address\": \"서울시 강남구 테헤란로 123\",\n  \"manager\": {\n    \"name\": \"김철수\",\n    \"mobile\": \"010-1234-5678\",\n    \"email\": \"kim@samsung.com\"\n  },\n  \"note\": \"주요 고객사, 정기 거래처\"\n}"))
            )
            @RequestBody CustomerCreateRequestDto request
    ) {
        // 500 모킹 트리거: 특정 회사명
        if (request != null && request.getCompanyName() != null && request.getCompanyName().equalsIgnoreCase("error")) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }

        // 400 필수값 검증
        java.util.List<java.util.Map<String, String>> missing = new java.util.ArrayList<>();
        if (request == null || request.getCompanyName() == null || request.getCompanyName().isBlank()) {
            missing.add(java.util.Map.of("field", "companyName", "reason", "REQUIRED"));
        }
        if (request == null || request.getBusinessNumber() == null || request.getBusinessNumber().isBlank()) {
            missing.add(java.util.Map.of("field", "businessNumber", "reason", "REQUIRED"));
        }
        if (request == null || request.getCeoName() == null || request.getCeoName().isBlank()) {
            missing.add(java.util.Map.of("field", "ceoName", "reason", "REQUIRED"));
        }
        boolean noPhone = (request == null || request.getContactPhone() == null || request.getContactPhone().isBlank());
        boolean noEmail = (request == null || request.getContactEmail() == null || request.getContactEmail().isBlank());
        if (noPhone && noEmail) {
            missing.add(java.util.Map.of("field", "contactPhone/contactEmail", "reason", "AT_LEAST_ONE_REQUIRED"));
        }
        if (!missing.isEmpty()) {
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(ErrorCode.CUSTOMER_REQUIRED_FIELDS_MISSING, missing);
        }

        // 422 형식 검증
        java.util.List<java.util.Map<String, String>> errors = new java.util.ArrayList<>();
        var bn = request.getBusinessNumber();
        if (bn != null && !bn.matches("\\d{3}-\\d{2}-\\d{5}")) {
            errors.add(java.util.Map.of("field", "businessNumber", "reason", "INVALID_FORMAT (###-##-#####)"));
        }
        var email = request.getContactEmail();
        if (email != null && !email.isBlank() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            errors.add(java.util.Map.of("field", "contactEmail", "reason", "INVALID_EMAIL"));
        }
        var phone = request.getContactPhone();
        if (phone != null && !phone.isBlank() && !phone.matches("^\\d{2,3}-\\d{3,4}-\\d{4}$")) {
            errors.add(java.util.Map.of("field", "contactPhone", "reason", "INVALID_PHONE"));
        }
        if (!errors.isEmpty()) {
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        // 성공 응답 목업
        java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("customerId", 501);
        data.put("customerCode", "C-0001");
        data.put("companyName", request.getCompanyName());
        data.put("ceoName", request.getCeoName());
        data.put("businessNumber", request.getBusinessNumber());
        data.put("statusCode", "ACTIVE");
        data.put("statusLabel", "활성");
        data.put("contactPhone", request.getContactPhone());
        data.put("contactEmail", request.getContactEmail());
        data.put("address", request.getAddress());
        java.util.Map<String, Object> manager = new java.util.LinkedHashMap<>();
        if (request.getManager() != null) {
            manager.put("name", request.getManager().getName());
            manager.put("mobile", request.getManager().getMobile());
            manager.put("email", request.getManager().getEmail());
        } else {
            manager.put("name", null);
            manager.put("mobile", null);
            manager.put("email", null);
        }
        data.put("manager", manager);
        data.put("totalOrders", 0);
        data.put("totalTransactionAmount", 0);
        data.put("currency", "KRW");
        data.put("note", request.getNote());
        java.time.Instant now = java.time.Instant.now();
        data.put("createdAt", now);
        data.put("updatedAt", now);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "고객사가 등록되었습니다.", HttpStatus.CREATED));
    }

    @GetMapping("/customers")
    @Operation(
            summary = "고객사 목록 조회",
            description = "고객사를 페이지네이션으로 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"고객사 목록을 조회했습니다.\",\n  \"data\": {\n    \"customers\": [\n      {\n        \"customerId\": 1,\n        \"customerCode\": \"C-001\",\n        \"companyName\": \"삼성전자\",\n        \"contactPerson\": \"김철수\",\n        \"phone\": \"02-1234-5678\",\n        \"email\": \"kim@samsung.com\",\n        \"address\": \"서울시 강남구 테헤란로 123\",\n        \"transactionAmount\": 1250000000,\n        \"orderCount\": 45,\n        \"lastOrderDate\": \"2024-01-15\",\n        \"status\": \"활성\"\n      }\n    ],\n    \"page\": { \"number\": 0, \"size\": 10, \"totalElements\": 3, \"totalPages\": 1, \"hasNext\": false }\n  }\n}"))
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
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 데이터를 조회할 권한이 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getCustomers(
            @Parameter(description = "상태: ALL, ACTIVE, DEACTIVE")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "검색 키워드")
            @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "페이지 번호(0-base)")
            @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기(최대 200)")
            @RequestParam(name = "size", required = false) Integer size
    ) {
        // 500 모킹 트리거
        if (keyword != null && ("error".equalsIgnoreCase(keyword) || "500".equalsIgnoreCase(keyword))) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }
        // 403 모킹 트리거
        if (keyword != null && (keyword.equalsIgnoreCase("금지") || keyword.equalsIgnoreCase("forbidden"))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_DATA_ACCESS);
        }

        // 422 검증
        List<Map<String, String>> errors = new ArrayList<>();
        if (status != null) {
            var allowed = java.util.Set.of("ALL", "ACTIVE", "DEACTIVE");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: ALL, ACTIVE, DEACTIVE"));
            }
        }
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        int pageIndex = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 10 : size;

        // 목업 데이터 준비(최소 10건)
        List<Map<String, Object>> all = new ArrayList<>();
        String[] companies = {"삼성전자", "LG화학", "현대자동차", "SK하이닉스", "네이버", "카카오", "포스코", "두산중공업", "CJ대한통운", "한화시스템", "아모레퍼시픽", "롯데케미칼"};
        String[] persons = {"김철수", "박영희", "이민호", "최지우", "한소라", "정우성", "장나라", "오세훈", "유재석", "아이유", "신동엽", "강호동"};
        String[] phones = {"02-1234-5678", "02-2345-6789", "031-111-2222", "02-9876-5432"};
        String[] emails = {"contact@corp.com", "sales@corp.com", "info@corp.com"};
        for (int i = 0; i < 12; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("customerId", i + 1);
            row.put("customerCode", String.format("C-%03d", i + 1));
            row.put("companyName", companies[i % companies.length]);
            row.put("contactPerson", persons[i % persons.length]);
            row.put("phone", phones[i % phones.length]);
            row.put("email", (i % 2 == 0) ? (persons[i % persons.length].charAt(0) + "@" + companies[i % companies.length] + ".com") : emails[i % emails.length]);
            row.put("address", (i % 2 == 0) ? "서울시 강남구 테헤란로 123" : "서울시 영등포구 여의도동 456");
            row.put("transactionAmount", 1_250_000_000L - (i * 37_000_000L));
            row.put("orderCount", 45 - (i % 10));
            row.put("lastOrderDate", String.format("2024-01-%02d", 15 - (i % 10)));
            row.put("status", (i % 3 == 0) ? "비활성" : "활성");
            all.add(row);
        }

        // 필터 적용
        List<Map<String, Object>> filtered = all;
        if (status != null && !"ALL".equals(status)) {
            boolean active = status.equals("ACTIVE");
            filtered = filtered.stream().filter(m -> (active ? "활성" : "비활성").equals(m.get("status"))).toList();
        }
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.toLowerCase();
            filtered = filtered.stream().filter(m -> {
                String cname = String.valueOf(m.get("companyName")).toLowerCase();
                String person = String.valueOf(m.get("contactPerson")).toLowerCase();
                return cname.contains(kw) || person.contains(kw);
            }).toList();
        }

        int total = filtered.size();
        int fromIdx = Math.min(pageIndex * s, total);
        int toIdx = Math.min(fromIdx + s, total);
        List<Map<String, Object>> customers = filtered.subList(fromIdx, toIdx);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("customers", customers);
        Map<String, Object> pageMeta2 = PageResponseUtils.buildPage(pageIndex, s, total);
        data.put("page", pageMeta2);

        return ResponseEntity.ok(ApiResponse.success(data, "고객사 목록을 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/customers/{customerId}")
    @Operation(
            summary = "고객사 상세 조회",
            description = "고객사 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"고객사 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"customerId\": 1,\n    \"customerCode\": \"C-001\",\n    \"companyName\": \"삼성전자\",\n    \"businessNumber\": \"123-45-67890\",\n    \"ceo\": \"이재용\",\n    \"establishmentDate\": \"1969-01-13\",\n    \"industry\": \"전자/반도체\",\n    \"creditRating\": \"A등급\",\n    \"employeeCount\": 267937,\n    \"website\": \"www.samsung.com\",\n    \"status\": \"활성\",\n    \"contact\": {\n      \"phone\": \"02-1234-5678\",\n      \"fax\": \"02-1234-5679\",\n      \"email\": \"kim@samsung.com\",\n      \"address\": \"서울시 강남구 테헤란로 123\"\n    },\n    \"manager\": {\n      \"name\": \"김철수\",\n      \"position\": \"구매팀장\",\n      \"department\": \"구매부\",\n      \"mobile\": \"010-1234-5678\",\n      \"directPhone\": \"02-1234-5680\"\n    },\n    \"transaction\": {\n      \"totalOrders\": 45,\n      \"totalAmount\": 1250000000,\n      \"lastOrderDate\": \"2024-01-15\",\n      \"paymentTerm\": \"30일 후 결제\",\n      \"creditLimit\": 5000000000,\n      \"taxType\": \"일반과세\"\n    },\n    \"note\": \"주요 고객사, 정기 거래처\"\n  }\n}"))
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
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 고객사를 조회할 권한이 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "리소스 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"고객사를 찾을 수 없습니다: customerId=999\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getCustomerDetail(
            @Parameter(description = "고객사 ID")
            @org.springframework.web.bind.annotation.PathVariable("customerId") Long customerId
    ) {
        // 403 모킹
        if (Long.valueOf(403001L).equals(customerId)) {
            throw new BusinessException(ErrorCode.CUSTOMER_FORBIDDEN);
        }
        // 500 모킹
        if (Long.valueOf(500001L).equals(customerId)) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }
        // 유효 범위: 1~10
        if (customerId == null || customerId < 1 || customerId > 10) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "customerId=" + customerId);
        }

        // 고객사 상세 목업 (id=1은 예시 값 사용)
        Map<String, Object> data = new LinkedHashMap<>();
        if (customerId == 1L) {
            data.put("customerId", 1);
            data.put("customerCode", "C-001");
            data.put("companyName", "삼성전자");
            data.put("businessNumber", "123-45-67890");
            data.put("ceo", "이재용");
            data.put("establishmentDate", "1969-01-13");
            data.put("industry", "전자/반도체");
            data.put("creditRating", "A등급");
            data.put("employeeCount", 267_937);
            data.put("website", "www.samsung.com");
            data.put("status", "활성");
            Map<String, Object> contact = new LinkedHashMap<>();
            contact.put("phone", "02-1234-5678");
            contact.put("fax", "02-1234-5679");
            contact.put("email", "kim@samsung.com");
            contact.put("address", "서울시 강남구 테헤란로 123");
            data.put("contact", contact);

            Map<String, Object> manager = new LinkedHashMap<>();
            manager.put("name", "김철수");
            manager.put("position", "구매팀장");
            manager.put("department", "구매부");
            manager.put("mobile", "010-1234-5678");
            manager.put("directPhone", "02-1234-5680");
            data.put("manager", manager);

            Map<String, Object> transaction = new LinkedHashMap<>();
            transaction.put("totalOrders", 45);
            transaction.put("totalAmount", 1_250_000_000L);
            transaction.put("lastOrderDate", "2024-01-15");
            transaction.put("paymentTerm", "30일 후 결제");
            transaction.put("creditLimit", 5_000_000_000L);
            transaction.put("taxType", "일반과세");
            data.put("transaction", transaction);

            data.put("note", "주요 고객사, 정기 거래처");
        } else {
            // 기타 id는 패턴화된 더미 데이터
            int idx = customerId.intValue();
            data.put("customerId", customerId);
            data.put("customerCode", String.format("C-%03d", idx));
            String[] companies = {"LG화학", "현대자동차", "SK하이닉스", "네이버", "카카오"};
            data.put("companyName", companies[(idx - 2) % companies.length]);
            data.put("businessNumber", String.format("%03d-%02d-%05d", 100 + idx, 10 + (idx % 50), 10000 + idx));
            data.put("ceo", "홍길동");
            data.put("establishmentDate", "2000-01-01");
            data.put("industry", "제조/IT");
            data.put("creditRating", "B등급");
            data.put("employeeCount", 10_000 + idx);
            data.put("website", "www.example.com");
            data.put("status", (idx % 2 == 0) ? "활성" : "비활성");
            Map<String, Object> contact = new LinkedHashMap<>();
            contact.put("phone", "02-0000-0000");
            contact.put("fax", "02-0000-0001");
            contact.put("email", "info@example.com");
            contact.put("address", "서울시 중구 세종대로 1");
            data.put("contact", contact);

            Map<String, Object> manager = new LinkedHashMap<>();
            manager.put("name", "관리자");
            manager.put("position", "담당");
            manager.put("department", "영업부");
            manager.put("mobile", "010-0000-0000");
            manager.put("directPhone", "02-0000-0002");
            data.put("manager", manager);

            Map<String, Object> transaction = new LinkedHashMap<>();
            transaction.put("totalOrders", 10 + (idx % 20));
            transaction.put("totalAmount", 100_000_000L + (idx * 1_000_000L));
            transaction.put("lastOrderDate", "2024-01-10");
            transaction.put("paymentTerm", "30일 후 결제");
            transaction.put("creditLimit", 1_000_000_000L);
            transaction.put("taxType", "일반과세");
            data.put("transaction", transaction);

            data.put("note", "비고 없음");
        }

        return ResponseEntity.ok(ApiResponse.success(data, "고객사 상세 정보를 조회했습니다.", HttpStatus.OK));
    }

    @org.springframework.web.bind.annotation.PutMapping("/customers/{customerId}")
    @Operation(
            summary = "고객사 정보 수정",
            description = "고객사 기본/연락/담당자 정보를 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"고객사 정보가 수정되었습니다.\",\n  \"data\": {\n    \"customerId\": \"C-001\",\n    \"companyName\": \"삼성전자\",\n    \"ceo\": \"이재용\",\n    \"businessNumber\": \"123-45-67890\",\n    \"status\": \"활성\",\n    \"contact\": {\n      \"phone\": \"02-1234-5678\",\n      \"address\": \"서울시 강남구 테헤란로 123\",\n      \"email\": \"info@samsung.com\"\n    },\n    \"manager\": {\n      \"name\": \"김철수\",\n      \"mobile\": \"010-1234-5678\",\n      \"email\": \"manager@samsung.com\"\n    },\n    \"note\": \"주요 거래처\"\n  }\n}"))
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
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 고객사를 수정할 권한이 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "리소스 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"고객사를 찾을 수 없습니다: customerId=C-999\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"businessNumber\", \"reason\": \"INVALID_FORMAT (###-##-#####)\" },\n    { \"field\": \"contact.phone\", \"reason\": \"INVALID_PHONE_FORMAT\" },\n    { \"field\": \"manager.email\", \"reason\": \"INVALID_EMAIL_FORMAT\" }\n  ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> updateCustomer(
            @org.springframework.web.bind.annotation.PathVariable("customerId") Long customerId,
            @RequestBody CustomerUpdateRequestDto request
    ) {
        // 500 모킹 트리거
        if (request != null && request.getCompanyName() != null && request.getCompanyName().equalsIgnoreCase("ERROR")) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }
        // 403 모킹
        if (Long.valueOf(403001L).equals(customerId)) {
            throw new BusinessException(ErrorCode.CUSTOMER_UPDATE_FORBIDDEN);
        }
        // 404 범위 외
        if (customerId == null || customerId < 1L || customerId > 10L) {
            String code = String.format("C-%03d", customerId == null ? 0 : customerId);
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "customerId=" + code);
        }

        // 422 형식 검증
        java.util.List<java.util.Map<String, String>> errors = new java.util.ArrayList<>();
        if (request != null) {
            if (request.getBusinessNumber() != null && !request.getBusinessNumber().matches("\\d{3}-\\d{2}-\\d{5}")) {
                errors.add(java.util.Map.of("field", "businessNumber", "reason", "INVALID_FORMAT (###-##-#####)"));
            }
            if (request.getContact() != null && request.getContact().getPhone() != null && !request.getContact().getPhone().isBlank()) {
                if (!request.getContact().getPhone().matches("^\\d{2,3}-\\d{3,4}-\\d{4}$")) {
                    errors.add(java.util.Map.of("field", "contact.phone", "reason", "INVALID_PHONE_FORMAT"));
                }
            }
            if (request.getManager() != null && request.getManager().getEmail() != null && !request.getManager().getEmail().isBlank()) {
                if (!request.getManager().getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                    errors.add(java.util.Map.of("field", "manager.email", "reason", "INVALID_EMAIL_FORMAT"));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        String code = String.format("C-%03d", customerId);

        // 성공 응답 목업: 요청 값을 반영하여 반환
        java.util.Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("customerId", code);
        data.put("companyName", request != null ? request.getCompanyName() : null);
        data.put("ceo", request != null ? request.getCeo() : null);
        data.put("businessNumber", request != null ? request.getBusinessNumber() : null);
        data.put("status", request != null ? request.getStatus() : null);

        java.util.Map<String, Object> contact = new java.util.LinkedHashMap<>();
        if (request != null && request.getContact() != null) {
            contact.put("phone", request.getContact().getPhone());
            contact.put("address", request.getContact().getAddress());
            contact.put("email", request.getContact().getEmail());
        }
        data.put("contact", contact);

        java.util.Map<String, Object> manager = new java.util.LinkedHashMap<>();
        if (request != null && request.getManager() != null) {
            manager.put("name", request.getManager().getName());
            manager.put("mobile", request.getManager().getMobile());
            manager.put("email", request.getManager().getEmail());
        }
        data.put("manager", manager);
        data.put("note", request != null ? request.getNote() : null);

        return ResponseEntity.ok(ApiResponse.success(data, "고객사 정보가 수정되었습니다.", HttpStatus.OK));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/customers/{customerId}")
    @Operation(
            summary = "고객사 삭제",
            description = "고객사 정보를 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"고객사 정보가 삭제되었습니다.\"\n}"))
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
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 고객사를 삭제할 권한이 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "리소스 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"고객사를 찾을 수 없습니다: customerId=10000\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "충돌(거래 내역 존재)",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "conflict", value = "{\n  \"status\": 409,\n  \"success\": false,\n  \"message\": \"해당 고객사는 거래 내역이 존재하여 삭제할 수 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> deleteCustomer(
            @org.springframework.web.bind.annotation.PathVariable("customerId") Long customerId
    ) {
        // 500 모킹
        if (Long.valueOf(500001L).equals(customerId)) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }
        // 403 모킹
        if (Long.valueOf(403001L).equals(customerId)) {
            throw new BusinessException(ErrorCode.CUSTOMER_DELETE_FORBIDDEN);
        }
        // 409 모킹
        if (Long.valueOf(409001L).equals(customerId)) {
            throw new BusinessException(ErrorCode.CUSTOMER_DELETE_CONFLICT);
        }
        // 404 범위 외 (1~10 유효)
        if (customerId == null || customerId < 1 || customerId > 10) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "customerId=" + customerId);
        }

        return ResponseEntity.ok(ApiResponse.success(null, "고객사 정보가 삭제되었습니다.", HttpStatus.OK));
    }

    // -------- Sales Orders (R) --------
    @GetMapping("/orders")
    @Operation(
            summary = "주문서 목록 조회",
            description = "견적서 승인에 따라 자동 생성된 주문서 목록을 조회합니다. 기간/상태/키워드(주문번호, 고객사명, 고객명) 필터를 지원합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"주문 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"id\": 1001,\n        \"soNumber\": \"SO-2024-001\",\n        \"customerId\": 301,\n        \"customerName\": \"(주)대한제조\",\n        \"contactName\": \"김영수\",\n        \"orderDate\": \"2024-01-15\",\n        \"deliveryDate\": \"2024-01-25\",\n        \"totalAmount\": 15000000,\n        \"statusCode\": \"PRODUCTION\",\n        \"statusLabel\": \"생산중\",\n        \"actions\": [\"view\"]\n      },\n      {\n        \"id\": 1002,\n        \"soNumber\": \"SO-2024-002\",\n        \"customerId\": 302,\n        \"customerName\": \"(주)테크솔루션\",\n        \"contactName\": \"박민수\",\n        \"orderDate\": \"2024-01-17\",\n        \"deliveryDate\": \"2024-01-30\",\n        \"totalAmount\": 8900000,\n        \"statusCode\": \"DELIVERING\",\n        \"statusLabel\": \"배송중\",\n        \"actions\": [\"view\"]\n      }\n    ],\n    \"page\": { \"number\": 0, \"size\": 10, \"totalElements\": 2, \"totalPages\": 1, \"hasNext\": false }\n  }\n}"))
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
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"주문 목록 조회 권한이 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [ { \"field\": \"startDate\", \"reason\": \"INVALID_DATE_FORMAT(YYYY-MM-DD)\" } ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"주문 목록 조회 중 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getSalesOrders(
            @Parameter(description = "검색 시작일(YYYY-MM-DD)")
            @RequestParam(name = "startDate", required = false) String startDate,
            @Parameter(description = "검색 종료일(YYYY-MM-DD)")
            @RequestParam(name = "endDate", required = false) String endDate,
            @Parameter(description = "주문번호/고객사명/고객명 검색 키워드")
            @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "상태: ALL, MATERIAL_PREPARATION, PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "페이지 번호(0-base)")
            @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기(최대 200)")
            @RequestParam(name = "size", required = false) Integer size,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        // 인증 체크
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }
        String token = authorization.trim().toUpperCase(Locale.ROOT);
        if (token.contains("ERROR")) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }
        // 권한 체크: SD_VIEWER, SALES_MANAGER, ADMIN
        if (!(token.contains("SD_VIEWER") || token.contains("SALES_MANAGER") || token.contains("ADMIN"))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_DATA_ACCESS);
        }

        // 422 검증
        List<Map<String, String>> errors = new ArrayList<>();
        java.time.LocalDate from = null;
        java.time.LocalDate to = null;
        if (startDate != null) {
            try { from = java.time.LocalDate.parse(startDate); } catch (Exception e) {
                errors.add(Map.of("field", "startDate", "reason", "INVALID_DATE_FORMAT(YYYY-MM-DD)"));
            }
        }
        if (endDate != null) {
            try { to = java.time.LocalDate.parse(endDate); } catch (Exception e) {
                errors.add(Map.of("field", "endDate", "reason", "INVALID_DATE_FORMAT(YYYY-MM-DD)"));
            }
        }
        if (from != null && to != null && from.isAfter(to)) {
            errors.add(Map.of("field", "startDate/endDate", "reason", "FROM_AFTER_TO"));
        }
        if (status != null) {
            var allowed = java.util.Set.of("ALL", "MATERIAL_PREPARATION", "PRODUCTION", "READY_FOR_SHIPMENT", "DELIVERING", "DELIVERED");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: ALL, MATERIAL_PREPARATION, PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED"));
            }
        }
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        int pageIndex = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;

        // 목업 데이터 생성
        List<Map<String, Object>> all = new ArrayList<>();
        String[] soNumbers = {"SO-2024-001","SO-2024-002","SO-2024-003","SO-2024-004","SO-2024-005","SO-2024-006","SO-2024-007","SO-2024-008","SO-2024-009","SO-2024-010"};
        String[] customers = {"(주)대한제조","(주)테크솔루션","현대기공","포스코엠텍","세아베스틸","네오머티리얼","스마트팩","그린테크","동방기계","에이치파워"};
        String[] contacts = {"김영수","박민수","이주연","최은정","홍길동","정우성","김하늘","박서준","한소라","장나라"};
        String[] orderDates = {"2024-01-15","2024-01-17","2024-01-18","2024-01-19","2024-01-20","2024-01-21","2024-01-22","2024-01-23","2024-01-24","2024-01-25"};
        String[] deliveryDates = {"2024-01-25","2024-01-30","2024-01-28","2024-01-29","2024-02-01","2024-02-02","2024-02-03","2024-02-04","2024-02-05","2024-02-06"};
        String[] codes = {"MATERIAL_PREPARATION","PRODUCTION","READY_FOR_SHIPMENT","DELIVERING","DELIVERED"};

        for (int i = 0; i < soNumbers.length; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", 1001 + i);
            row.put("soNumber", soNumbers[i]);
            row.put("customerId", 301 + i);
            row.put("customerName", customers[i]);
            row.put("contactName", contacts[i]);
            row.put("orderDate", orderDates[i]);
            row.put("deliveryDate", deliveryDates[i]);
            row.put("totalAmount", 15_000_000L - (i * 610_000L));
            String statusCode = codes[i % codes.length];
            row.put("statusCode", statusCode);
            String statusLabel = switch (statusCode) {
                case "MATERIAL_PREPARATION" -> "자재 준비중";
                case "PRODUCTION" -> "생산중";
                case "READY_FOR_SHIPMENT" -> "출하 준비 완료";
                case "DELIVERING" -> "배송중";
                case "DELIVERED" -> "배송완료";
                default -> "";
            };
            row.put("statusLabel", statusLabel);
            row.put("actions", List.of("view"));
            all.add(row);
        }

        // 필터 적용
        List<Map<String, Object>> filtered = all;
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            final String st = status.toUpperCase(Locale.ROOT);
            filtered = filtered.stream().filter(m -> st.equals(String.valueOf(m.get("statusCode")))).toList();
        }
        if (keyword != null && !keyword.isBlank()) {
            final String kw = keyword.toLowerCase(Locale.ROOT);
            filtered = filtered.stream().filter(m -> {
                String so = String.valueOf(m.get("soNumber")).toLowerCase(Locale.ROOT);
                String cn = String.valueOf(m.get("customerName")).toLowerCase(Locale.ROOT);
                String pn = String.valueOf(m.get("contactName")).toLowerCase(Locale.ROOT);
                return so.contains(kw) || cn.contains(kw) || pn.contains(kw);
            }).toList();
        }
        if (from != null) {
            final java.time.LocalDate min = from;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(String.valueOf(m.get("orderDate"))).isBefore(min))
                    .toList();
        }
        if (to != null) {
            final java.time.LocalDate max = to;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(String.valueOf(m.get("orderDate"))).isAfter(max))
                    .toList();
        }

        // 정렬: orderDate desc, id asc 보조
        filtered = filtered.stream()
                .sorted(java.util.Comparator
                        .comparing((Map<String, Object> m) -> java.time.LocalDate.parse(String.valueOf(m.get("orderDate"))))
                        .reversed()
                        .thenComparing(m -> ((Number) m.get("id")).longValue()))
                .toList();

        int total = filtered.size();
        int fromIdx = Math.min(pageIndex * pageSize, total);
        int toIdx2 = Math.min(fromIdx + pageSize, total);
        List<Map<String, Object>> pageContent = filtered.subList(fromIdx, toIdx2);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", pageContent);
        data.put("page", PageResponseUtils.buildPage(pageIndex, pageSize, total));

        return ResponseEntity.ok(ApiResponse.success(data, "주문 목록 조회에 성공했습니다.", HttpStatus.OK));
    }

    // -------- Analytics (R) - week params (kept for tests) --------
    @GetMapping("/analytics/sales")
    @Operation(
            summary = "매출 분석 통계 조회",
            description = "주차 범위 내 매출 추이, 제품 비중, 상위 고객사를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 통계 데이터를 조회했습니다.\",\n  \"data\": {\n    \"trend\": [ { \"year\": 2025, \"week\": 10, \"sale\": 350000000, \"orderCount\": 120 } ],\n    \"productShare\": [ { \"productCode\": \"P-001\", \"productName\": \"OLED TV\", \"sale\": 1230000000, \"saleShare\": 35.2 } ],\n    \"topCustomers\": [ { \"customerCode\": \"C-001\", \"customerName\": \"삼성전자\", \"sale\": 850000000, \"saleShare\": 24.3 } ]\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "범위 초과",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "range_too_large", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"조회 기간은 최대 12주(3개월)까지만 가능합니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getSalesAnalytics(
            @Parameter(description = "시작 연도") @RequestParam int startYear,
            @Parameter(description = "시작 주차") @RequestParam int startWeek,
            @Parameter(description = "종료 연도") @RequestParam int endYear,
            @Parameter(description = "종료 주차") @RequestParam int endWeek
    ) {
        // 500 모킹 트리거
        if (startYear == 5000 || endYear == 5000) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }

        // 인덱스 계산 (2024:1..52 → 0..51, 2025:1..39 → 52..90)
        int idxStart = toIndex(startYear, startWeek);
        int idxEnd = toIndex(endYear, endWeek);
        if (idxStart > idxEnd) { int t = idxStart; idxStart = idxEnd; idxEnd = t; }

        int weeks = idxEnd - idxStart + 1;
        if (weeks > 12) {
            throw new BusinessException(ErrorCode.ANALYTICS_RANGE_TOO_LARGE);
        }

        // 트렌드 데이터 생성
        java.util.List<Map<String, Object>> trend = new java.util.ArrayList<>();
        for (int i = idxStart; i <= idxEnd; i++) {
            int year = (i < 52) ? 2024 : 2025;
            int week = (i < 52) ? (i + 1) : (i - 52 + 1);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("year", year);
            row.put("week", week);
            row.put("sale", 300_000_000L + ((i - idxStart) * 50_000_000L));
            row.put("orderCount", 100 + ((i - idxStart) * 20));
            trend.add(row);
        }

        // 제품 비중/상위 고객(샘플)
        java.util.List<Map<String, Object>> productShare = new java.util.ArrayList<>();
        productShare.add(new LinkedHashMap<>() {{ put("productCode", "P-001"); put("productName", "OLED TV"); put("sale", 1_230_000_000L); put("saleShare", 35.2); }});
        productShare.add(new LinkedHashMap<>() {{ put("productCode", "P-002"); put("productName", "냉장고"); put("sale", 850_000_000L); put("saleShare", 24.3); }});

        java.util.List<Map<String, Object>> topCustomers = new java.util.ArrayList<>();
        topCustomers.add(new LinkedHashMap<>() {{ put("customerCode", "C-001"); put("customerName", "삼성전자"); put("sale", 850_000_000L); put("saleShare", 24.3); }});
        topCustomers.add(new LinkedHashMap<>() {{ put("customerCode", "C-002"); put("customerName", "LG전자"); put("sale", 500_000_000L); put("saleShare", 14.3); }});

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("trend", trend);
        data.put("productShare", productShare);
        data.put("topCustomers", topCustomers);

        return ResponseEntity.ok(ApiResponse.success(data, "매출 통계 데이터를 조회했습니다.", HttpStatus.OK));
    }

    private static int toIndex(int year, int week) {
        int weeks2024 = 52;
        if (year <= 2024) {
            int w = Math.max(1, Math.min(52, week));
            return w - 1;
        } else { // 2025 기준
            int w = Math.max(1, Math.min(39, week));
            return weeks2024 + (w - 1);
        }
    }
    
}
