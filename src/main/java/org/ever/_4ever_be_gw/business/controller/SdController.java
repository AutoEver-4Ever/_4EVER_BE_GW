package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.business.dto.customer.*;
import org.ever._4ever_be_gw.business.constants.QuotationStatus;
import org.ever._4ever_be_gw.business.dto.quotation.*;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.common.util.UuidV7;
import org.ever._4ever_be_gw.scmpp.dto.PeriodStatDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.business.dto.sd.InventoryCheckItemDto;
import org.ever._4ever_be_gw.business.dto.sd.InventoryCheckItemRequestDto;
import org.ever._4ever_be_gw.business.dto.sd.InventoryCheckRequestDto;
import org.ever._4ever_be_gw.business.dto.sd.InventoryCheckResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // 견적 목업 저장소 (목록/상세 공유)
    private static final List<QuotationListItemDto> MOCK_QO_LIST = new ArrayList<>();
    private static final Map<String, QuotationDetailDto> MOCK_QO_DETAIL = new LinkedHashMap<>();

    // 고객사 목업 저장소 (목록/상세 공유)
    private static final List<CustomerListItemDto> MOCK_CUS_LIST = new ArrayList<>();
    private static final Map<String, org.ever._4ever_be_gw.business.dto.customer.CustomerDetailDto> MOCK_CUS_DETAIL = new LinkedHashMap<>();

    static {
        String[] customers = {"삼성전자", "LG전자", "현대자동차", "카카오", "네이버", "SK하이닉스", "포스코", "두산중공업", "한화시스템", "CJ대한통운"};
        String[] managers = {"김철수", "이영희", "박민수", "최지훈", "한소라", "정우성", "장나라", "오세훈", "유재석", "아이유"};
        String[] ceoNames = {"이재용", "구광모", "장재경", "김범수", "최수연", "곽노정", "김학동", "박정원", "김동관", "손경식"};
        QuotationStatus[] codes = QuotationStatus.values();

        for (int i = 0; i < 50; i++) {
            String quotationId = UuidV7.string();
            String quotationNumber = "QO-" + quotationId.substring(0, 6);       // uuid의 앞자리 6개
            String quotationDate = String.format("2024-01-%02d", 1 + (i % 28));
            String dueDate = String.format("2024-02-%02d", 1 + (i % 28));
            long totalAmount = 15_000_000L - (i * 250_000L);
            String statusCode = codes[i % codes.length].getCode();

            var listItem = QuotationListItemDto.builder()
                    .quotationId(quotationId)
                    .quotationNumber(quotationNumber)
                    .customerName(customers[i % customers.length])
                    .managerName(managers[i % managers.length])
                    .quotationDate(quotationDate)
                    .dueDate(dueDate)
                    .totalAmount(totalAmount)
                    .statusCode(statusCode)
                    .build();
            MOCK_QO_LIST.add(listItem);

            // 상세 항목(샘플 2개)
            var item1 = QuotationItemDto.builder()
                    .itemId(String.valueOf(900001L + i % 10))
                    .itemName("제품 A")
                    .quantity(10)
                    .uomName("EA")
                    .unitPrice(1_000_000L)
                    .amount(10_000_000L)
                    .build();
            var item2 = QuotationItemDto.builder()
                    .itemId(String.valueOf(900011L + i % 10))
                    .itemName("제품 B")
                    .quantity(5)
                    .uomName("EA")
                    .unitPrice(1_000_000L)
                    .amount(5_000_000L)
                    .build();
            var detail = QuotationDetailDto.builder()
                    .qoId(quotationId)
                    .qoNumber(quotationNumber)
                    .qoDate(java.time.LocalDate.parse("2024-01-15"))
                    .dueDate(java.time.LocalDate.parse("2024-02-15"))
                    .statusCode(statusCode)
                    .customerName(customers[i % customers.length])
                    .ceoName(ceoNames[i % ceoNames.length])
                    .items(java.util.List.of(item1, item2))
                    .totalAmount(item1.getAmount() + item2.getAmount())
                    .build();
            MOCK_QO_DETAIL.put(quotationId, detail);
        }
        // 고객사 목업 데이터 초기화 (목록/상세 연결)
        String[] companies = {"삼성전자", "LG화학", "현대자동차", "SK하이닉스", "네이버", "카카오", "포스코", "두산중공업", "CJ대한통운", "한화시스템", "아모레퍼시픽", "롯데케미칼"};
        String[] persons2 = {"김철수", "박영희", "이민호", "최지우", "한소라", "정우성", "장나라", "오세훈", "유재석", "아이유", "신동엽", "강호동"};
        String[] phones2 = {"02-1234-5678", "02-2345-6789", "031-111-2222", "02-9876-5432"};
        String[] emails2 = {"contact@corp.com", "sales@corp.com", "info@corp.com"};
        for (int i = 0; i < 50; i++) {
            String customerId = UuidV7.string();
            String customerNumber = "CUS-" + customerId.substring(0, 6);
            boolean active = (i % 3) != 0;
            String statusCode2 = active ? "ACTIVE" : "DEACTIVE";

            CustomerManagerDto managerDto = CustomerManagerDto.builder()
                    .managerName(persons2[i % persons2.length])
                    .managerPhone(phones2[i % phones2.length])
                    .managerEmail(emails2[i % emails2.length])
                    .build();

            CustomerListItemDto listItem = CustomerListItemDto.builder()
                    .customerId(customerId)
                    .customerNumber(customerNumber)
                    .customerName(companies[i % companies.length])
                    .manager(managerDto)
                    .address((i % 2 == 0) ? "서울시 강남구 테헤란로 123" : "서울시 영등포구 여의도동 456")
                    .totalTransactionAmount(1_250_000_000L - (long) i * 37_000_000L)
                    .orderCount(45 - (i % 10))
                    .lastOrderDate("2024-01-" + String.format("%02d", (i % 28) + 1))
                    .statusCode(statusCode2)
                    .build();
            MOCK_CUS_LIST.add(listItem);

            org.ever._4ever_be_gw.business.dto.customer.CustomerDetailDto detailDto = org.ever._4ever_be_gw.business.dto.customer.CustomerDetailDto.builder()
                    .customerId(customerId)
                    .customerNumber(customerNumber)
                    .customerName(companies[i % companies.length])
                    .ceoName(persons2[i % persons2.length])
                    .businessNumber(String.format("%03d-%02d-%05d", 100 + (i % 900), 10 + (i % 90), 10000 + (i % 90000)))
                    .statusCode(statusCode2)
                    .customerPhone(phones2[i % phones2.length])
                    .customerEmail(emails2[i % emails2.length])
                    .baseAddress((i % 2 == 0) ? "서울시 강남구 테헤란로 123" : "서울시 영등포구 여의도동 456")
                    .detailAddress((i % 2 == 0) ? "4층" : "12층")
                    .manager(managerDto)
                    .totalOrders(45 - (i % 10))
                    .totalTransactionAmount(1_250_000_000L - (long) i * 37_000_000L)
                    .note("주요 고객사")
                    .build();
            MOCK_CUS_DETAIL.put(customerId, detailDto);
        }
    }

    // SD 통계 조회
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
    public ResponseEntity<ApiResponse<StatsResponseDto<StatsMetricsDto>>> getStatistics(
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

        StatsResponseDto.StatsResponseDtoBuilder<StatsMetricsDto> builder = StatsResponseDto.<StatsMetricsDto>builder();
        if (finalPeriods.contains("week")) {
            builder.week(StatsMetricsDto.builder()
                    .put("sales_amount", new PeriodStatDto(152_300_000L, new BigDecimal("0.105")))
                    .put("new_orders_count", new PeriodStatDto(42L, new BigDecimal("0.067")))
                    .build());
        }
        if (finalPeriods.contains("month")) {
            builder.month(StatsMetricsDto.builder()
                    .put("sales_amount", new PeriodStatDto(485_200_000L, new BigDecimal("0.125")))
                    .put("new_orders_count", new PeriodStatDto(127L, new BigDecimal("0.082")))
                    .build());
        }
        if (finalPeriods.contains("quarter")) {
            builder.quarter(StatsMetricsDto.builder()
                    .put("sales_amount", new PeriodStatDto(1_385_200_000L, new BigDecimal("0.047")))
                    .put("new_orders_count", new PeriodStatDto(392L, new BigDecimal("0.031")))
                    .build());
        }
        if (finalPeriods.contains("year")) {
            builder.year(StatsMetricsDto.builder()
                    .put("sales_amount", new PeriodStatDto(5_485_200_000L, new BigDecimal("0.036")))
                    .put("new_orders_count", new PeriodStatDto(4_217L, new BigDecimal("0.028")))
                    .build());
        }

        StatsResponseDto<StatsMetricsDto> data = builder.build();
        return ResponseEntity.ok(ApiResponse.success(data, "OK", HttpStatus.OK));
    }

    // 견적 품목 재고 확인
    @PostMapping("/quotations/inventory/check")
    @Operation(
            summary = "견적 품목 재고 확인",
            description = "요청한 품목들의 현재 재고를 확인하고 부족 여부를 반환합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"재고 확인을 완료했습니다.\",\n  \"data\": {\n    \"items\": [\n      { \"itemId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\", \"itemName\": \"제품 A\", \"requiredQty\": 10, \"inventoryQty\": 12, \"shortageQty\": 0, \"statusCode\": \"FULFILLED\", \"productionRequired\": false },\n      { \"itemId\": \"018f2c1a-3bfb-7e21-9b3c-1a2b3c4d5e6f\", \"itemName\": \"제품 B\", \"requiredQty\": 5,  \"inventoryQty\": 2,  \"shortageQty\": 3, \"statusCode\": \"SHORTAGE\",  \"productionRequired\": true }\n    ]\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<InventoryCheckResponseDto>> checkInventory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"items\": [\n    { \"itemId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\", \"itemName\": \"제품 A\", \"requiredQty\": 10 },\n    { \"itemId\": \"018f2c1a-3bfb-7e21-9b3c-1a2b3c4d5e6f\", \"itemName\": \"제품 B\", \"requiredQty\": 5 }\n  ]\n}"))
            )
            @RequestBody InventoryCheckRequestDto request
    ) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            errors.add(Map.of("field", "items", "reason", "REQUIRED_MIN_1"));
        } else if (request.getItems().size() > 200) {
            errors.add(Map.of("field", "items", "reason", "MAX_200"));
        } else {
            int idx = 0;
            for (InventoryCheckItemRequestDto it : request.getItems()) {
                idx++;
                if (it == null) {
                    errors.add(Map.of("field", "items[" + (idx - 1) + "]", "reason", "REQUIRED"));
                    continue;
                }
                if (it.getItemId() == null) {
                    errors.add(Map.of("field", "items[" + (idx - 1) + "].itemId", "reason", "REQUIRED"));
                }
                if (it.getItemName() == null || it.getItemName().isBlank()) {
                    errors.add(Map.of("field", "items[" + (idx - 1) + "].itemName", "reason", "REQUIRED"));
                }
                if (it.getRequiredQty() == null || it.getRequiredQty() <= 0) {
                    errors.add(Map.of("field", "items[" + (idx - 1) + "].requiredQty", "reason", ">0"));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        List<InventoryCheckItemDto> results = new ArrayList<>();
        for (InventoryCheckItemRequestDto it : request.getItems()) {
            int required = it.getRequiredQty();
            int baseInv;
            if (it.getItemId() != null && !it.getItemId().isBlank()) {
                try {
                    UUID u = UUID.fromString(it.getItemId());
                    long mixed = u.getMostSignificantBits() ^ u.getLeastSignificantBits();
                    baseInv = (int) (Math.abs(mixed) % 12);
                } catch (IllegalArgumentException e) {
                    baseInv = Math.abs(it.getItemId().hashCode()) % 12;
                }
            } else {
                baseInv = Math.abs((it.getItemName() == null ? 0 : it.getItemName().hashCode()) % 10) + 1;
            }
            int inventory = baseInv;
            int shortage = Math.max(0, required - inventory);
            boolean needProd = shortage > 0;
            String status = needProd ? "SHORTAGE" : "FULFILLED";

            results.add(InventoryCheckItemDto.builder()
                    .itemId(it.getItemId())
                    .itemName(it.getItemName())
                    .requiredQty(required)
                    .inventoryQty(inventory)
                    .shortageQty(shortage)
                    .statusCode(status)
                    .productionRequired(needProd)
                    .build());
        }

        InventoryCheckResponseDto data = InventoryCheckResponseDto.builder()
                .items(results)
                .build();
        return ResponseEntity.ok(ApiResponse.success(data, "재고 확인을 완료했습니다.", HttpStatus.OK));
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"견적 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"items\": [\n      {\n        \"qoId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n        \"qoNumber\": \"QO-2024-001\",\n        \"customerName\": \"삼성전자\",\n        \"managerName\": \"김철수\",\n        \"quotationDate\": \"2024-01-15\",\n        \"dueDate\": \"2024-02-15\",\n        \"totalAmount\": 15000000,\n        \"statusCode\": \"PENDING\"\n      }\n    ],\n    \"page\": { \"number\": 0, \"size\": 10, \"totalElements\": 57, \"totalPages\": 6, \"hasNext\": true }\n  }\n}"))
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
    public ResponseEntity<ApiResponse<QuotationListResponseDto>> getQuotations(
            @Parameter(description = "시작일(YYYY-MM-DD)")
            @RequestParam(name = "startDate", required = false) String startDate,
            @Parameter(description = "종료일(YYYY-MM-DD)")
            @RequestParam(name = "endDate", required = false) String endDate,
            @Parameter(description = "상태: PENDING, REVIEW, APPROVED, REJECTED, ALL")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "검색 타입: qoNumber(견적번호), customerName(고객사명), managerName(담당자명)", example = "qoNumber")
            @RequestParam(name = "type", required = false) String type,
            @Parameter(description = "검색어")
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
        // 검색 타입 검증: search가 있으면 type 필수 및 허용값 확인
        if (search != null && !search.isBlank()) {
            Set<String> allowedTypes = Set.of("qoNumber", "customerName", "managerName");
            if (type == null || type.isBlank()) {
                errors.add(Map.of("field", "type", "reason", "REQUIRED_WHEN_SEARCH"));
            } else if (!allowedTypes.contains(type)) {
                errors.add(Map.of("field", "type", "reason", "ALLOWED_VALUES: qoNumber, customerName, managerName"));
            }
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

        // 고정 목업 데이터 사용
        List<QuotationListItemDto> items = new ArrayList<>(MOCK_QO_LIST);

        // 필터 적용: status(ALL은 전체), 날짜 범위, 검색어(customerName/ownerName/quotationCode)
        List<QuotationListItemDto> filtered = items;
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            final String st = status.toUpperCase(Locale.ROOT);
            filtered = filtered.stream()
                    .filter(m -> st.equals(m.getStatusCode()))
                    .toList();
        }
        if (from != null) {
            final java.time.LocalDate minDate = from;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(m.getQuotationDate()).isBefore(minDate))
                    .toList();
        }
        if (to != null) {
            final java.time.LocalDate maxDate = to;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(m.getQuotationDate()).isAfter(maxDate))
                    .toList();
        }
        if (search != null && !search.isBlank()) {
            final String kw = search.toLowerCase(Locale.ROOT);
            switch (type) {
                case "qoNumber":
                    filtered = filtered.stream()
                            .filter(m -> m.getQuotationNumber().toLowerCase(Locale.ROOT).contains(kw))
                            .toList();
                    break;
                case "customerName":
                    filtered = filtered.stream()
                            .filter(m -> m.getCustomerName().toLowerCase(Locale.ROOT).contains(kw))
                            .toList();
                    break;
                case "managerName":
                    filtered = filtered.stream()
                            .filter(m -> m.getManagerName().toLowerCase(Locale.ROOT).contains(kw))
                            .toList();
                    break;
                default:
                    // 방어적 처리: 허용되지 않은 값은 필터 미적용
                    break;
            }
        }

        // 정렬 적용: quotationDate|dueDate|totalAmount + asc|desc
        String[] sortParts = effectiveSort.split(",");
        String sortField = sortParts[0].trim();
        String sortDirection = sortParts.length > 1 ? sortParts[1].trim().toLowerCase(Locale.ROOT) : "desc";
        java.util.Comparator<QuotationListItemDto> comparator;
        switch (sortField) {
            case "dueDate" -> comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(m.getDueDate()));
            case "totalAmount" -> comparator = java.util.Comparator.comparing(QuotationListItemDto::getTotalAmount);
            case "quotationDate" -> comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(m.getQuotationDate()));
            default -> comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(m.getQuotationDate()));
        }
        if ("desc".equals(sortDirection)) {
            comparator = comparator.reversed();
        }
        filtered = filtered.stream().sorted(comparator).toList();

        // 페이지네이션 적용
        int total = filtered.size();
        int fromIdx = Math.min(pageIndex * s, total);
        int toIdx = Math.min(fromIdx + s, total);
        List<QuotationListItemDto> pageItems = filtered.subList(fromIdx, toIdx);

        // PageDto 구성
        int totalPages = s == 0 ? 0 : (int) Math.ceil((double) total / s);
        boolean hasNext = pageIndex + 1 < totalPages;
        PageDto pageMeta = PageDto.builder()
                .number(pageIndex)
                .size(s)
                .totalElements(total)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .build();

        QuotationListResponseDto data = QuotationListResponseDto.builder()
                .items(pageItems)
                .page(pageMeta)
                .build();

        return ResponseEntity.ok(ApiResponse.success(data, "견적 목록 조회에 성공했습니다.", HttpStatus.OK));
    }

    @GetMapping("/quotations/{qoId}")
    @Operation(
            summary = "견적 상세 조회",
            description = "견적 단건 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"견적 상세 조회에 성공했습니다.\",\n  \"data\": {\n    \"qoId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n    \"qoNumber\": \"QO-2024-001\",\n    \"qoDate\": \"2024-01-15\",\n    \"dueDate\": \"2024-02-15\",\n    \"statusCode\": \"PENDING\",\n    \"customerName\": \"삼성전자\",\n    \"ceoName\": \"이재용\",\n    \"items\": [\n      { \"itemId\": \"900001\", \"itemName\": \"제품 A\", \"quantity\": 10, \"uomName\": \"EA\", \"unitPrice\": 1000000, \"amount\": 10000000 },\n      { \"itemId\": \"900011\", \"itemName\": \"제품 B\", \"quantity\": 5,  \"uomName\": \"EA\", \"unitPrice\": 1000000, \"amount\": 5000000 }\n    ],\n    \"totalAmount\": 15000000\n  }\n}"))
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
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 견적을 찾을 수 없습니다: qoId=12001\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<QuotationDetailDto>> getQuotationDetail(
            @Parameter(description = "견적 ID(qoId)", example = "000019a0-6ce9-eb27-9c2f-95dcc4a9fa8c")
            @org.springframework.web.bind.annotation.PathVariable("qoId") String qoId
    ) {
        QuotationDetailDto detail = MOCK_QO_DETAIL.get(qoId);
        if (detail == null) {
            throw new BusinessException(ErrorCode.QUOTATION_NOT_FOUND, "qoId=" + qoId);
        }
        return ResponseEntity.ok(ApiResponse.success(detail, "견적 상세 조회에 성공했습니다.", HttpStatus.OK));
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 201,\n  \"success\": true,\n  \"message\": \"신규 견적서 등록이 완료되었습니다.\",\n  \"data\": {\n    \"qoId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n    \"qoDate\": \"2025-10-12\",\n    \"dueDate\": \"2025-11-01\",\n    \"totalAmount\": 7000000,\n    \"statusCode\": \"PENDING\"\n  }\n}"))
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
    public ResponseEntity<ApiResponse<QuotationCreateResponseDto>> createQuotation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"dueDate\": \"2025-11-01\",\n  \"items\": [\n    {\n      \"itemId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n      \"quantity\": 10,\n      \"unitPrice\": 500000\n    },\n    {\n      \"itemId\": \"018f2c1a-3bfb-7e21-9b3c-1a2b3c4d5e6f\",\n      \"quantity\": 5,\n      \"unitPrice\": 200000\n    }\n  ],\n  \"note\": \"긴급 납품 요청\"\n}"))
            )
            @RequestBody QuotationRequestDto request
    ) {
        java.time.LocalDate today = java.time.LocalDate.now();

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

        String qoId = UuidV7.string();
        var data = QuotationCreateResponseDto.builder()
                .qoId(qoId)
                .qoDate(today.toString())
                .dueDate("2025-11-01")
                .totalAmount(totalAmount)
                .statusCode("PENDING")
                .build();

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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"견적 검토 요청이 정상적으로 처리되었습니다.\",\n  \"data\": {\n    \"qoId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n    \"statusCode\": \"REVIEW\",\n    \"requestedAt\": \"2025-10-12T12:34:56Z\"\n  }\n}"))
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
    public ResponseEntity<ApiResponse<QuotationConfirmResponseDto>> confirmQuotations(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"qoId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\"\n}"))
            )
            @RequestBody QuotationConfirmRequestDto request
    ) {
        String qoId = request != null ? request.getQoId() : null;
        if (qoId == null || qoId.isBlank()) {
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(ErrorCode.VALIDATION_FAILED,
                    java.util.List.of(java.util.Map.of("field", "qoId", "reason", "REQUIRED")));
        }
        // 500 모킹 트리거
        if ("500001".equals(qoId)) {
            throw new RuntimeException("boom");
        }
        // 400: 검토 불가 상태 포함
        if ("400001".equals(qoId)) {
            throw new BusinessException(ErrorCode.QUOTATION_CONFIRM_INVALID_STATE);
        }
        // 404: 존재하지 않는 견적 (고정 목업 저장소 기준)
        if (!MOCK_QO_DETAIL.containsKey(qoId)) {
            throw new BusinessException(ErrorCode.QUOTATION_CONFIRM_NOT_FOUND);
        }

        var resp = org.ever._4ever_be_gw.business.dto.quotation.QuotationConfirmResponseDto.builder()
                .qoId(qoId)
                .statusCode("REVIEW")
                .requestedAt(java.time.OffsetDateTime.now().toString())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resp, "견적 검토 요청이 정상적으로 처리되었습니다.", HttpStatus.OK));
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 201,\n  \"success\": true,\n  \"message\": \"고객사가 등록되었습니다.\",\n  \"data\": {\n    \"customerId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n    \"customerNumber\": \"C-0001\",\n    \"customerName\": \"삼성전자\",\n    \"ceoName\": \"이재용\",\n    \"businessNumber\": \"123-45-67890\",\n    \"statusCode\": \"ACTIVE\",\n    \"contactPhone\": \"02-1234-5678\",\n    \"contactEmail\": \"contact@samsung.com\",\n    \"zipCode\": \"06236\",\n    \"address\": \"서울시 강남구 테헤란로 123\",\n    \"detailAddress\": \"4층\",\n    \"manager\": { \"managerName\": \"김철수\", \"managerPhone\": \"010-1234-5678\", \"managerEmail\": \"kim@samsung.com\" },\n    \"totalOrders\": 0,\n    \"totalTransactionAmount\": 0,\n    \"note\": \"주요 고객사, 정기 거래처\"\n  }\n}"))
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
    public ResponseEntity<ApiResponse<CustomerCreateResponseDto>> createCustomer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"companyName\": \"삼성전자\",\n  \"businessNumber\": \"123-45-67890\",\n  \"ceoName\": \"이재용\",\n  \"contactPhone\": \"02-1234-5678\",\n  \"contactEmail\": \"contact@samsung.com\",\n  \"zipCode\": \"06236\",\n  \"address\": \"서울시 강남구 테헤란로 123\",\n  \"detailAddress\": \"4층\",\n  \"manager\": {\n    \"name\": \"김철수\",\n    \"mobile\": \"010-1234-5678\",\n    \"email\": \"kim@samsung.com\"\n  },\n  \"note\": \"주요 고객사, 정기 거래처\"\n}"))
            )
            @RequestBody CustomerCreateRequestDto request
    ) {
        // 성공 응답 DTO
        String customerId = UuidV7.string();
        String customerNumber = "CUS-" + customerId.substring(0, 6);
        CustomerManagerDto managerDto = null;
        if (request.getManager() != null) {
            managerDto = CustomerManagerDto.builder()
                    .managerName(request.getManager().getName())
                    .managerPhone(request.getManager().getMobile())
                    .managerEmail(request.getManager().getEmail())
                    .build();
        }

        CustomerCreateResponseDto data = CustomerCreateResponseDto.builder()
                .customerId(customerId)
                .customerNumber(customerNumber)
                .customerName(request.getCompanyName())
                .ceoName(request.getCeoName())
                .businessNumber(request.getBusinessNumber())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .zipCode(request.getZipCode())
                .baseAddress(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .manager(managerDto)
                .note(request.getNote())
                .build();

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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"고객사 목록을 조회했습니다.\",\n  \"data\": {\n    \"customers\": [\n      {\n        \"customerId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n        \"customerNumber\": \"C-001\",\n        \"customerName\": \"삼성전자\",\n        \"statusCode\": \"ACTIVE\",\n        \"address\": \"서울시 강남구 테헤란로 123\",\n        \"manager\": { \"managerName\": \"김철수\", \"managerPhone\": \"010-1234-5678\", \"managerEmail\": \"kim@samsung.com\" },\n        \"totalOrders\": 45,\n        \"totalTransactionAmount\": 1250000000\n      }\n    ],\n    \"page\": { \"number\": 0, \"size\": 10, \"totalElements\": 3, \"totalPages\": 1, \"hasNext\": false }\n  }\n}"))
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
    public ResponseEntity<ApiResponse<CustomerListResponseDto>> getCustomers(
            @Parameter(description = "상태: ALL, ACTIVE, DEACTIVE")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "검색어")
            @RequestParam(name = "search", required = false) String search,
            @Parameter(description = "검색 타입: customerNumber, customerName, managerName", example = "customerName")
            @RequestParam(name = "type", required = false) String type,
            @Parameter(description = "페이지 번호(0-base)")
            @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기(최대 200)")
            @RequestParam(name = "size", required = false) Integer size
    ) {
        int pageIndex = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 10 : size;

        // 정적 목업 목록 사용
        List<CustomerListItemDto> all = new ArrayList<>(MOCK_CUS_LIST);

        // 필터 적용
        List<CustomerListItemDto> filtered = all;
        if (status != null && !"ALL".equals(status)) {
            boolean active = status.equals("ACTIVE");
            filtered = filtered.stream().filter(m -> (active ? "ACTIVE" : "DEACTIVE").equals(m.getStatusCode())).toList();
        }
        if (search != null && !search.isBlank()) {
            String kw = search.toLowerCase();
            switch (type) {
                case "customerNumber" -> filtered = filtered.stream()
                        .filter(m -> (m.getCustomerNumber() != null && m.getCustomerNumber().toLowerCase().contains(kw)))
                        .toList();
                case "customerName" -> filtered = filtered.stream()
                        .filter(m -> (m.getCustomerName() != null && m.getCustomerName().toLowerCase().contains(kw)))
                        .toList();
                case "managerName" -> filtered = filtered.stream()
                        .filter(m -> (m.getManager() != null && m.getManager().getManagerName() != null && m.getManager().getManagerName().toLowerCase().contains(kw)))
                        .toList();
                default -> {
                    // 방어적: 미지정/허용 외 타입은 필터 미적용
                }
            }
        }

        int total = filtered.size();
        int fromIdx = Math.min(pageIndex * s, total);
        int toIdx = Math.min(fromIdx + s, total);
        List<CustomerListItemDto> customers = filtered.subList(fromIdx, toIdx);

        int totalPages = s == 0 ? 0 : (int) Math.ceil((double) total / s);
        boolean hasNext = pageIndex + 1 < totalPages;
        PageDto pageDto = PageDto.builder()
                .number(pageIndex)
                .size(s)
                .totalElements(total)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .build();

        CustomerListResponseDto data = CustomerListResponseDto.builder()
                .customers(customers)
                .page(pageDto)
                .build();

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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"고객사 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"customerId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n    \"customerNumber\": \"C-001\",\n    \"customerName\": \"삼성전자\",\n    \"ceoName\": \"이재용\",\n    \"businessNumber\": \"123-45-67890\",\n    \"statusCode\": \"ACTIVE\",\n    \"contactPhone\": \"02-1234-5678\",\n    \"contactEmail\": \"contact@samsung.com\",\n    \"address\": \"서울시 강남구 테헤란로 123\",\n    \"detailAddress\": \"4층\",\n    \"manager\": { \"managerName\": \"김철수\", \"managerPhone\": \"010-1234-5678\", \"managerEmail\": \"kim@samsung.com\" },\n    \"totalOrders\": 45,\n    \"totalTransactionAmount\": 1250000000,\n    \"note\": \"주요 고객사, 정기 거래처\"\n  }\n}"))
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
    public ResponseEntity<ApiResponse<org.ever._4ever_be_gw.business.dto.customer.CustomerDetailDto>> getCustomerDetail(
            @Parameter(description = "고객사 ID (UUID)", example = "000019a0-a7b0-c667-839c-7d23811a5e5a")
            @org.springframework.web.bind.annotation.PathVariable("customerId") String customerId
    ) {
        if (customerId == null || customerId.isBlank()) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "customerId=" + customerId);
        }
        if ("403001".equals(customerId)) {
            throw new BusinessException(ErrorCode.CUSTOMER_FORBIDDEN);
        }
        if ("500001".equals(customerId)) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }
        var dto = MOCK_CUS_DETAIL.get(customerId);
        if (dto == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "customerId=" + customerId);
        }
        return ResponseEntity.ok(ApiResponse.success(dto, "고객사 상세 정보를 조회했습니다.", HttpStatus.OK));
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"고객사 정보가 수정되었습니다.\",\n  \"data\": {\n    \"customerId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n    \"customerNumber\": \"CUS-018f2c\",\n    \"customerName\": \"삼성전자\",\n    \"ceoName\": \"이재용\",\n    \"businessNumber\": \"123-45-67890\",\n    \"statusCode\": \"ACTIVE\",\n    \"contactPhone\": \"02-1234-5678\",\n    \"contactEmail\": \"info@samsung.com\",\n    \"baseAddress\": \"서울시 강남구 테헤란로 123\",\n    \"detailAddress\": \"4층\",\n    \"manager\": { \"managerName\": \"김철수\", \"managerPhone\": \"010-1234-5678\", \"managerEmail\": \"manager@samsung.com\" },\n    \"note\": \"주요 거래처\"\n  }\n}"))
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
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"businessNumber\", \"reason\": \"INVALID_FORMAT (###-##-#####)\" },\n    { \"field\": \"customerPhone\", \"reason\": \"INVALID_PHONE_FORMAT\" },\n    { \"field\": \"manager.managerEmail\", \"reason\": \"INVALID_EMAIL_FORMAT\" }\n  ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<org.ever._4ever_be_gw.business.dto.customer.CustomerUpdateResponseDto>> updateCustomer(
            @org.springframework.web.bind.annotation.PathVariable("customerId") String customerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"customerName\": \"삼성전자\",\n  \"ceoName\": \"이재용\",\n  \"businessNumber\": \"123-45-67890\",\n  \"customerPhone\": \"02-1234-5678\",\n  \"customerEmail\": \"info@samsung.com\",\n  \"baseAddress\": \"서울시 강남구 테헤란로 123\",\n  \"detailAddress\": \"4층\",\n  \"statusCode\": \"ACTIVE\",\n  \"manager\": {\n    \"managerName\": \"김철수\",\n    \"managerPhone\": \"010-1234-5678\",\n    \"managerEmail\": \"manager@samsung.com\"\n  },\n  \"note\": \"주요 거래처\"\n}"))
            )
            @RequestBody CustomerUpdateRequestDto request
    ) {
        String code = "CUS-" + (customerId != null && customerId.length() >= 6 ? customerId.substring(0, 6) : String.valueOf(customerId));

        var managerDto = (request != null && request.getManager() != null)
                ? org.ever._4ever_be_gw.business.dto.customer.CustomerManagerDto.builder()
                    .managerName(request.getManager().getManagerName())
                    .managerPhone(request.getManager().getManagerPhone())
                    .managerEmail(request.getManager().getManagerEmail())
                    .build()
                : null;

        var data = CustomerUpdateResponseDto.builder()
                .customerId(customerId)
                .customerNumber(code)
                .customerName(request != null ? request.getCustomerName() : null)
                .ceoName(request != null ? request.getCeoName() : null)
                .businessNumber(request != null ? request.getBusinessNumber() : null)
                .statusCode(request != null ? request.getStatusCode() : null)
                .customerPhone(request != null ? request.getCustomerPhone() : null)
                .customerEmail(request != null ? request.getCustomerEmail() : null)
                .baseAddress(request != null ? request.getBaseAddress() : null)
                .detailAddress(request != null ? request.getDetailAddress() : null)
                .manager(managerDto)
                .note(request != null ? request.getNote() : null)
                .build();

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
    public ResponseEntity<ApiResponse<CustomerDeleteResponseDto>> deleteCustomer(
            @org.springframework.web.bind.annotation.PathVariable("customerId") String customerId
    ) {
        var data = CustomerDeleteResponseDto.builder()
                .customerId(customerId)
                .statusCode("INACTIVE")
                .deleted(true)
                .deletedAt(java.time.OffsetDateTime.now().toString())
                .build();

        return ResponseEntity.ok(ApiResponse.success(data, "고객사 정보가 삭제되었습니다.", HttpStatus.OK));
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"주문 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"soId\": 1,\n        \"soNumber\": \"SO-2024-001\",\n        \"customerName\": \"(주)테크솔루션\",\n        \"manager\": { \"managerName\": \"김영수\", \"managerPhone\": \"02-1234-5678\", \"managerEmail\": \"contact@example.com\" },\n        \"orderDate\": \"2024-01-15\",\n        \"deliveryDate\": \"2024-01-25\",\n        \"totalAmount\": 15500000,\n        \"statusCode\": \"IN_PRODUCTION\"\n      },\n      {\n        \"soId\": 2,\n        \"soNumber\": \"SO-2024-002\",\n        \"customerName\": \"(주)대한제조\",\n        \"manager\": { \"managerName\": \"박민수\", \"managerPhone\": \"02-1234-5678\", \"managerEmail\": \"contact@example.com\" },\n        \"orderDate\": \"2024-01-16\",\n        \"deliveryDate\": \"2024-01-27\",\n        \"totalAmount\": 15150000,\n        \"statusCode\": \"DELIVERING\"\n      }\n    ],\n    \"page\": { \"number\": 0, \"size\": 10, \"totalElements\": 50, \"totalPages\": 5, \"hasNext\": true }\n  }\n}"))
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
    public ResponseEntity<ApiResponse<org.ever._4ever_be_gw.business.dto.order.SalesOrderListResponseDto>> getSalesOrders(
            @Parameter(description = "검색 시작일(YYYY-MM-DD)")
            @RequestParam(name = "startDate", required = false) String startDate,
            @Parameter(description = "검색 종료일(YYYY-MM-DD)")
            @RequestParam(name = "endDate", required = false) String endDate,
            @Parameter(description = "검색어")
            @RequestParam(name = "search", required = false) String search,
            @Parameter(description = "검색 타입: soNumber, customerName, managerName", example = "soNumber")
            @RequestParam(name = "type", required = false) String type,
            @Parameter(description = "상태: ALL, MATERIAL_PREPARATION, IN_PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "페이지 번호(0-base)")
            @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기(최대 200)")
            @RequestParam(name = "size", required = false) Integer size
    ) {
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
            var allowed = java.util.Set.of("ALL", "MATERIAL_PREPARATION", "IN_PRODUCTION", "READY_FOR_SHIPMENT", "DELIVERING", "DELIVERED");
            if (!allowed.contains(status)) {
                errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: ALL, MATERIAL_PREPARATION, IN_PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED"));
            }
        }
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (search != null && !search.isBlank()) {
            var allowedTypes = java.util.Set.of("soNumber", "customerName", "managerName");
            if (type == null || type.isBlank() || !allowedTypes.contains(type)) {
                errors.add(Map.of("field", "type", "reason", "ALLOWED_VALUES: soNumber, customerName, managerName"));
            }
        }
        if (!errors.isEmpty()) {
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        int pageIndex = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;

        // 목업 데이터 생성(1~50): 상세 API와 동일한 규칙으로 생성
        java.util.List<org.ever._4ever_be_gw.business.dto.order.SalesOrderListItemDto> all = new ArrayList<>();
        String[] customers = {"(주)테크솔루션","(주)대한제조","현대기공","포스코엠텍","세아베스틸","네오머티리얼","스마트팩","그린테크","동방기계","에이치파워"};
        String[] contacts = {"김영수","박민수","이주연","최은정","홍길동","정우성","김하늘","박서준","한소라","장나라"};
        String[] codes = {"IN_PRODUCTION","DELIVERING","MATERIAL_PREPARATION","READY_FOR_SHIPMENT","DELIVERED"};
        java.time.LocalDate baseOrder = java.time.LocalDate.of(2024, 1, 15);
        for (int i = 0; i < 50; i++) {
            int soIdVal = i + 1;               // 1..50
            int arrIndex = i;                   // 0..49
            java.time.LocalDate od = baseOrder.plusDays(arrIndex % 20);
            java.time.LocalDate dd = od.plusDays(10 + (arrIndex % 5));
            String statusCode = codes[arrIndex % codes.length];

            var manager = org.ever._4ever_be_gw.business.dto.order.ManagerDto.builder()
                    .managerName(contacts[arrIndex % contacts.length])
                    .managerPhone("02-1234-5678")
                    .managerEmail("contact@example.com")
                    .build();

            var item = org.ever._4ever_be_gw.business.dto.order.SalesOrderListItemDto.builder()
                    .soId((long) soIdVal)
                    .soNumber(String.format("SO-2024-%03d", soIdVal))
                    .customerName(customers[arrIndex % customers.length])
                    .manager(manager)
                    .orderDate(od.toString())
                    .deliveryDate(dd.toString())
                    .totalAmount(15_500_000L - (arrIndex * 350_000L))
                    .statusCode(statusCode)
                    .build();

            all.add(item);
        }

        // 필터 적용
        java.util.List<org.ever._4ever_be_gw.business.dto.order.SalesOrderListItemDto> filtered = all;
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            final String st = status.toUpperCase(Locale.ROOT);
            filtered = filtered.stream()
                    .filter(m -> st.equals(String.valueOf(m.getStatusCode())))
                    .toList();
        }
        if (search != null && !search.isBlank()) {
            final String kw = search.toLowerCase(Locale.ROOT);
            switch (type) {
                case "soNumber" -> filtered = filtered.stream()
                        .filter(m -> m.getSoNumber() != null && m.getSoNumber().toLowerCase(Locale.ROOT).contains(kw))
                        .toList();
                case "customerName" -> filtered = filtered.stream()
                        .filter(m -> m.getCustomerName() != null && m.getCustomerName().toLowerCase(Locale.ROOT).contains(kw))
                        .toList();
                case "managerName" -> filtered = filtered.stream()
                        .filter(m -> m.getManager() != null && m.getManager().getManagerName() != null && m.getManager().getManagerName().toLowerCase(Locale.ROOT).contains(kw))
                        .toList();
                default -> {
                }
            }
        }
        if (from != null) {
            final java.time.LocalDate min = from;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(m.getOrderDate()).isBefore(min))
                    .toList();
        }
        if (to != null) {
            final java.time.LocalDate max = to;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(m.getOrderDate()).isAfter(max))
                    .toList();
        }

        // 정렬: orderDate desc, soId asc 보조
        filtered = filtered.stream()
                .sorted(java.util.Comparator
                        .comparing((org.ever._4ever_be_gw.business.dto.order.SalesOrderListItemDto m) -> java.time.LocalDate.parse(m.getOrderDate()))
                        .reversed()
                        .thenComparing(org.ever._4ever_be_gw.business.dto.order.SalesOrderListItemDto::getSoId))
                .toList();

        int total = filtered.size();
        int fromIdx = Math.min(pageIndex * pageSize, total);
        int toIdx2 = Math.min(fromIdx + pageSize, total);
        java.util.List<org.ever._4ever_be_gw.business.dto.order.SalesOrderListItemDto> pageContent = filtered.subList(fromIdx, toIdx2);

        // PageDto 구성 (공통)
        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize);
        boolean hasNext = pageIndex + 1 < totalPages;
        var pageDto = org.ever._4ever_be_gw.common.dto.PageDto.builder()
                .number(pageIndex)
                .size(pageSize)
                .totalElements(total)
                .totalPages(totalPages)
                .hasNext(hasNext)
                .build();

        var data = org.ever._4ever_be_gw.business.dto.order.SalesOrderListResponseDto.builder()
                .content(pageContent)
                .page(pageDto)
                .build();

        return ResponseEntity.ok(ApiResponse.success(data, "주문 목록 조회에 성공했습니다.", HttpStatus.OK));
    }

    // -------- Sales Order Detail (R) --------
    @GetMapping("/orders/{soId}")
    @Operation(
            summary = "주문서 상세 조회",
            description = "주문서 상세 정보를 조회합니다. 주문 정보, 고객 정보, 품목, 총액, 메모를 포함합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"주문서 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"order\": {\n      \"soId\": 1,\n      \"soNumber\": \"SO-2024-001\",\n      \"orderDate\": \"2024-01-15\",\n      \"deliveryDate\": \"2024-01-25\",\n      \"statusCode\": \"IN_PRODUCTION\",\n      \"totalAmount\": 15500000\n    },\n    \"customer\": {\n      \"customerId\": 301,\n      \"customerName\": \"(주)테크솔루션\",\n      \"manager\": { \"managerName\": \"김영수\", \"managerPhone\": \"02-1234-5678\", \"managerEmail\": \"contact@example.com\" }\n    },\n    \"items\": [\n      { \"productName\": \"산업용 모터 5HP\", \"quantity\": 5, \"unit\": \"개\", \"unitPrice\": 850000, \"amount\": 4250000 },\n      { \"productName\": \"제어판넬\", \"quantity\": 2, \"unit\": \"개\", \"unitPrice\": 1200000, \"amount\": 2400000 }\n    ],\n    \"note\": \"긴급 주문 - 우선 처리 요청\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공(예시 업데이트)",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success_updated", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"주문서 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"order\": {\n      \"soId\": 1,\n      \"soNumber\": \"SO-2024-001\",\n      \"orderDate\": \"2024-01-15\",\n      \"deliveryDate\": \"2024-01-25\",\n      \"statusCode\": \"IN_PRODUCTION\",\n      \"totalAmount\": 15500000\n    },\n    \"customer\": {\n      \"customerId\": 301,\n      \"customerName\": \"(주)테크솔루션\",\n      \"manager\": { \"managerName\": \"김영수\", \"managerPhone\": \"02-1234-5678\", \"managerEmail\": \"contact@example.com\" }\n    },\n    \"items\": [\n      { \"productName\": \"산업용 모터 5HP\", \"quantity\": 5, \"unit\": \"개\", \"unitPrice\": 850000, \"amount\": 4250000 },\n      { \"productName\": \"제어판넬\", \"quantity\": 2, \"unit\": \"개\", \"unitPrice\": 1200000, \"amount\": 2400000 }\n    ],\n    \"note\": \"긴급 주문 - 우선 처리 요청\"\n  }\n}"))
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
                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"주문서 상세 조회 권한이 없습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "리소스 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 주문서를 찾을 수 없습니다: soId=1201\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"주문서 상세 조회 중 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<org.ever._4ever_be_gw.business.dto.order.SalesOrderDetailDto>> getSalesOrderDetail(
            @Parameter(description = "주문서 ID")
            @org.springframework.web.bind.annotation.PathVariable("soId") Long soId
    ) {
        // 유효 범위: 1~50
        if (soId == null || soId < 1L || soId > 50L) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "soId=" + soId);
        }

        // 1~50 인덱스 그대로 사용 (로직 매핑 제거)
        int idx = soId.intValue();
        String[] customers = {"(주)테크솔루션","(주)대한제조","현대기공","포스코엠텍","세아베스틸","네오머티리얼","스마트팩","그린테크","동방기계","에이치파워"};
        String[] contacts = {"김영수","박민수","이주연","최은정","홍길동","정우성","김하늘","박서준","한소라","장나라"};

        // 상태코드(상세): IN_PRODUCTION 포함
        String[] codes = {"IN_PRODUCTION","DELIVERING","MATERIAL_PREPARATION","READY_FOR_SHIPMENT","DELIVERED"};
        int arrIndex = (idx - 1);
        java.time.LocalDate od = java.time.LocalDate.of(2024,1,15).plusDays(arrIndex % 20);
        java.time.LocalDate dd = od.plusDays(10 + (arrIndex % 5));
        String code = codes[arrIndex % codes.length];

        // DTO를 이용하여 목업데이터 생성
        var orderDto = org.ever._4ever_be_gw.business.dto.order.OrderSummaryDto.builder()
                .soId(soId)
                .soNumber(String.format("SO-2024-%03d", idx))
                .orderDate(od.toString())
                .deliveryDate(dd.toString())
                .statusCode(code)
                .totalAmount(15_500_000L - (arrIndex * 350_000L))
                .build();

        var managerDto = org.ever._4ever_be_gw.business.dto.order.ManagerDto.builder()
                .managerName(contacts[arrIndex % contacts.length])
                .managerPhone("02-1234-5678")
                .managerEmail("contact@example.com")
                .build();

        var customerDto = org.ever._4ever_be_gw.business.dto.order.CustomerSummaryDto.builder()
                .customerId(300L + idx)
                .customerName(customers[arrIndex % customers.length])
                .manager(managerDto)
                .build();

        java.util.List<org.ever._4ever_be_gw.business.dto.order.OrderItemDto> items = new java.util.ArrayList<>();
        items.add(org.ever._4ever_be_gw.business.dto.order.OrderItemDto.builder()
                .productName("산업용 모터 5HP")
                .quantity(5)
                .uonName("개")
                .unitPrice(850_000L)
                .amount(4_250_000L)
                .build());
        items.add(org.ever._4ever_be_gw.business.dto.order.OrderItemDto.builder()
                .productName("제어판넬")
                .quantity(2)
                .uonName("개")
                .unitPrice(1_200_000L)
                .amount(2_400_000L)
                .build());

        var detail = org.ever._4ever_be_gw.business.dto.order.SalesOrderDetailDto.builder()
                .order(orderDto)
                .customer(customerDto)
                .items(items)
                .note("긴급 주문 - 우선 처리 요청")
                .build();

        return ResponseEntity.ok(ApiResponse.success(detail, "주문서 상세 정보를 조회했습니다.", HttpStatus.OK));
    }

    // -------- Analytics (R) - week params (kept for tests) --------
    @GetMapping("/analytics/sales")
    @Operation(
            summary = "매출 분석 통계 조회",
            description = "start/end(yyyy-mm-dd)으로 날짜 범위를 지정하여 매출 분석을 조회합니다.\n" +
                    "- 날짜 기반: start가 포함된 ISO 주의 월요일부터 end가 포함된 ISO 주의 일요일까지 포함\n" +
                    "- 제한: 최대 6개월 범위\n" +
                    "- 응답 필드\n" +
                    "  * period: { start, end, weekStart, weekEnd, weekCount }\n" +
                    "  * trend: [{ year, month, week, sale, orderCount }] (주차별)\n" +
                    "  * trendScale: { sale: {min,max}, orderCount: {min,max} } (차트 y축 범위)\n" +
                    "  * productShare: 상위 5개 + etc(총 6개) ({ productCode, productName, sale, saleShare })\n" +
                    "  * topCustomers: 10개 고객({ customerCode, customerName, orderCount, sale, active })",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 통계 데이터를 조회했습니다.\",\n  \"data\": {\n    \"period\": { \"start\": \"2025-01-01\", \"end\": \"2025-03-31\", \"weekStart\": \"2024-12-30\", \"weekEnd\": \"2025-04-06\", \"weekCount\": 14 },\n    \"trend\": [ { \"year\": 2025, \"month\": 1, \"week\": 1, \"sale\": 410000000, \"orderCount\": 106 } ],\n    \"trendScale\": { \"sale\": { \"min\": 400000000, \"max\": 540000000 }, \"orderCount\": { \"min\": 100, \"max\": 140 } },\n    \"productShare\": [ { \"productCode\": \"EXT-001\", \"productName\": \"Door Panel\", \"sale\": 1260000000, \"saleShare\": 14.0 } ],\n    \"topCustomers\": [ { \"customerCode\": \"C-001\", \"customerName\": \"현대자동차\", \"orderCount\": 42, \"sale\": 850000000 } ]\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "범위 초과",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "range_too_large", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"조회 기간은 최대 6개월까지만 가능합니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<org.ever._4ever_be_gw.business.dto.analytics.SalesAnalyticsResponseDto>> getSalesAnalytics(
            @Parameter(description = "시작일 (yyyy-mm-dd)") @RequestParam(name = "start", required = false) String startDateStr,
            @Parameter(description = "종료일 (yyyy-mm-dd)") @RequestParam(name = "end", required = false) String endDateStr
    ) {
        // 날짜 기반 요청만 지원: 둘 다 필수
        if (startDateStr != null && !startDateStr.isBlank() && endDateStr != null && !endDateStr.isBlank()) {
            java.time.LocalDate startDate;
            java.time.LocalDate endDate;
            try {
                startDate = java.time.LocalDate.parse(startDateStr);
                endDate = java.time.LocalDate.parse(endDateStr);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.ANALYTICS_INVALID_DATE_FORMAT);
            }

            // 서버 오류(모킹) 트리거: 연도 5000 포함 시
            if (startDate.getYear() == 5000 || endDate.getYear() == 5000) {
                throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
            }

            if (startDate.isAfter(endDate)) {
                throw new BusinessException(ErrorCode.ANALYTICS_START_AFTER_END);
            }

            var wf = java.time.temporal.WeekFields.ISO;
            java.time.LocalDate weekStart = startDate.with(wf.dayOfWeek(), 1);
            java.time.LocalDate weekEnd = endDate.with(wf.dayOfWeek(), 7);

            // 조회 제한: 최대 6개월 (weekEnd가 weekStart.plusMonths(6) 이후면 초과)
            if (weekEnd.isAfter(weekStart.plusMonths(6))) {
                throw new BusinessException(ErrorCode.ANALYTICS_RANGE_TOO_LARGE);
            }

            // 주차별 트렌드 생성
            java.util.List<java.util.Map<String, Object>> trend = new java.util.ArrayList<>();
            long totalSale = 0L;
            int totalOrders = 0;
            long minSale = Long.MAX_VALUE;
            long maxSale = Long.MIN_VALUE;
            int minOrders = Integer.MAX_VALUE;
            int maxOrders = Integer.MIN_VALUE;

            java.time.LocalDate cursor = weekStart;
            int i = 0;
            while (!cursor.isAfter(weekEnd)) {
                int weekBasedYear = cursor.get(wf.weekBasedYear());
                int weekOfYear = cursor.get(wf.weekOfWeekBasedYear());
                java.time.LocalDate ws = cursor.with(wf.dayOfWeek(), 1);

                double base = 400_000_000d;
                double seasonal = 60_000_000d * Math.sin(2 * Math.PI * i / 13.0);
                double step = 15_000_000d * (i % 3);
                double hash = 10_000_000d * ((weekBasedYear * 100 + weekOfYear) % 5);
                long sale = (long) Math.max(250_000_000d, base + seasonal + step + hash);
                sale = (sale / 10_000L) * 10_000L; // 1만 원 단위 절삭

                int orderCount = 100 + 5 * (i % 6) + ((weekBasedYear + weekOfYear) % 7);

                java.util.Map<String, Object> row = new java.util.LinkedHashMap<>();
                row.put("year", weekBasedYear);
                row.put("month", ws.getMonthValue());
                row.put("week", weekOfYear);
                row.put("sale", sale);
                row.put("orderCount", orderCount);
                trend.add(row);

                totalSale += sale;
                totalOrders += orderCount;

                if (sale < minSale) minSale = sale;
                if (sale > maxSale) maxSale = sale;
                if (orderCount < minOrders) minOrders = orderCount;
                if (orderCount > maxOrders) maxOrders = orderCount;

                cursor = cursor.plusWeeks(1);
                i++;
            }

            int weekCount = trend.size();

            // 제품 비중: 상위 5개 + etc(나머지 합산)
            String[] productNames = new String[]{
                "Door Panel", "Front Bumper", "Rear Bumper", "Hood", "Trunk Lid",
                "Front Fender", "Rear Fender", "Side Skirt", "Roof Panel", "Grille"
            };
            int[] productWeights = new int[]{14, 12, 11, 10, 10, 9, 8, 8, 9, 9}; // 합 100
            java.util.List<java.util.Map<String, Object>> productShare = new java.util.ArrayList<>();
            int topN = 5;
            int etcWeight = 0;
            for (int idx = 0; idx < productWeights.length; idx++) {
                if (idx < topN) {
                    java.util.Map<String, Object> p = new java.util.LinkedHashMap<>();
                    p.put("productCode", String.format("EXT-%03d", idx + 1));
                    p.put("productName", productNames[idx]);
                    long ps = Math.round(totalSale * (productWeights[idx] / 100.0));
                    p.put("sale", ps);
                    p.put("saleShare", (double) productWeights[idx]);
                    productShare.add(p);
                } else {
                    etcWeight += productWeights[idx];
                }
            }
            // etc 항목 추가
            java.util.Map<String, Object> etc = new java.util.LinkedHashMap<>();
            etc.put("productCode", "ETC");
            etc.put("productName", "etc");
            long etcSale = Math.round(totalSale * (etcWeight / 100.0));
            etc.put("sale", etcSale);
            etc.put("saleShare", (double) etcWeight);
            productShare.add(etc);

            // 상위 고객 10개 (국내 자동차/부품사)
            String[] customerNames = new String[]{
                "현대자동차", "기아", "한국GM", "르노코리아", "KG모빌리티",
                "현대모비스", "만도", "현대위아", "한온시스템", "SL"
            };
            int[] customerWeights = new int[]{15, 13, 12, 10, 10, 9, 8, 8, 7, 8}; // 합 100
            java.util.List<java.util.Map<String, Object>> topCustomers = new java.util.ArrayList<>();
            for (int idx = 0; idx < 10; idx++) {
                java.util.Map<String, Object> c = new java.util.LinkedHashMap<>();
                c.put("customerCode", String.format("C-%03d", idx + 1));
                c.put("customerName", customerNames[idx]);
                long cs = Math.round(totalSale * (customerWeights[idx] / 100.0));
                int oc = Math.max(1, (int) Math.round((double) totalOrders * (customerWeights[idx] / 100.0)));
                c.put("orderCount", oc);
                c.put("sale", cs);
                // 활성 상태 목업: 짝수 인덱스 활성, 홀수 비활성
                c.put("active", idx % 2 == 0);
                topCustomers.add(c);
            }

            java.util.Map<String, Object> period = new java.util.LinkedHashMap<>();
            period.put("start", startDate.toString());
            period.put("end", endDate.toString());
            period.put("weekStart", weekStart.toString());
            period.put("weekEnd", weekEnd.toString());
            period.put("weekCount", weekCount);

            // y축 범위 계산 (보기 좋은 단위로 보정)
            long saleUnit = 10_000_000L; // 1천만 단위
            long saleMin = (minSale == Long.MAX_VALUE) ? 0 : (minSale / saleUnit) * saleUnit;
            long saleMax = (maxSale == Long.MIN_VALUE) ? 0 : ((maxSale + saleUnit - 1) / saleUnit) * saleUnit;
            int orderUnit = 5;
            int orderMin = (minOrders == Integer.MAX_VALUE) ? 0 : (minOrders / orderUnit) * orderUnit;
            int orderMax = (maxOrders == Integer.MIN_VALUE) ? 0 : ((maxOrders + orderUnit - 1) / orderUnit) * orderUnit;

            java.util.Map<String, Object> trendScale = new java.util.LinkedHashMap<>();
            java.util.Map<String, Object> saleScale = new java.util.LinkedHashMap<>();
            saleScale.put("min", saleMin);
            saleScale.put("max", saleMax);
            java.util.Map<String, Object> orderScale = new java.util.LinkedHashMap<>();
            orderScale.put("min", orderMin);
            orderScale.put("max", orderMax);
            trendScale.put("sale", saleScale);
            trendScale.put("orderCount", orderScale);

            var periodDto = new org.ever._4ever_be_gw.business.dto.analytics.PeriodDto(
                    startDate.toString(), endDate.toString(), weekStart.toString(), weekEnd.toString(), weekCount);
            var trendDto = trend.stream().map(m -> new org.ever._4ever_be_gw.business.dto.analytics.TrendPointDto(
                    (Integer) m.get("year"), (Integer) m.get("month"), (Integer) m.get("week"),
                    ((Number) m.get("sale")).longValue(), (Integer) m.get("orderCount"))).toList();
            var saleScaleDto = new org.ever._4ever_be_gw.business.dto.analytics.ScaleDto(saleMin, saleMax);
            var orderScaleDto = new org.ever._4ever_be_gw.business.dto.analytics.ScaleDto((long) orderMin, (long) orderMax);
            var trendScaleDto = new org.ever._4ever_be_gw.business.dto.analytics.TrendScaleDto(saleScaleDto, orderScaleDto);
            var productShareDto = productShare.stream().map(m -> new org.ever._4ever_be_gw.business.dto.analytics.ProductShareDto(
                    String.valueOf(m.get("productCode")), String.valueOf(m.get("productName")),
                    ((Number) m.get("sale")).longValue(), (Double) m.get("saleShare"))).toList();
            var topCustomersDto = topCustomers.stream().map(m -> new org.ever._4ever_be_gw.business.dto.analytics.TopCustomerDto(
                    String.valueOf(m.get("customerCode")),
                    String.valueOf(m.get("customerName")), (Integer) m.get("orderCount"), ((Number) m.get("sale")).longValue(),
                    (Boolean) m.get("active"))).toList();

            var data = org.ever._4ever_be_gw.business.dto.analytics.SalesAnalyticsResponseDto.builder()
                    .totalSale(totalSale)
                    .totalOrders(totalOrders)
                    .period(periodDto)
                    .trend(trendDto)
                    .trendScale(trendScaleDto)
                    .productShare(productShareDto)
                    .topCustomers(topCustomersDto)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(data, "매출 통계 데이터를 조회했습니다.", HttpStatus.OK));
        }

        // 날짜 파라미터 누락 시 400
        throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE);
    }

    @PostMapping("/quotations/{qoId}/approve-order")
    @Operation(
            summary = "견적 승인 및 주문 전환",
            description = "재고가 충분한 경우 견적서를 승인(APPROVED)하고 주문서를 출고 준비 완료(READY_FOR_SHIPMENT) 상태로 생성합니다. 모킹: 항상 200 성공을 반환합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"견적을 승인하고 주문서로 전환했습니다.\",\n  \"data\": {\n    \"qoId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n    \"soId\": \"018f2c1a-3bfb-7e21-9b3c-1a2b3c4d5e6f\",\n    \"soNumber\": \"SO-2024-123\",\n    \"statusCode\": \"READY_FOR_SHIPMENT\",\n    \"approvedAt\": \"2025-10-12T12:34:56Z\"\n  }\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<org.ever._4ever_be_gw.business.dto.quotation.ApproveQuotationResponseDto>> approveQuotationAndCreateOrder(
            @Parameter(description = "전환 대상 견적 ID (UUID)")
            @PathVariable("qoId") String qoId
    ) {
        String soId = org.ever._4ever_be_gw.common.util.UuidV7.string();
        var resp = org.ever._4ever_be_gw.business.dto.quotation.ApproveQuotationResponseDto.builder()
                .qoId(qoId)
                .soId(soId)
                .soNumber("SO-2024-" + (int)(Math.random()*900+100))
                .statusCode("READY_FOR_SHIPMENT")
                .approvedAt(java.time.OffsetDateTime.now().toString())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resp, "견적을 승인하고 주문서로 전환했습니다.", HttpStatus.OK));
    }
    
}
