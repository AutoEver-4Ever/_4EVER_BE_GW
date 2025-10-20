package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.ever._4ever_be_gw.business.dto.InvoiceUpdateRequestDto;
import org.ever._4ever_be_gw.business.dto.invoice.InvoiceDetailDto;
import org.ever._4ever_be_gw.business.dto.invoice.PurchaseInvoiceItemDto;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.scmpp.dto.PeriodStatDto;
// Use business-layer AP invoice DTOs for detail view
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/business/fcm")
@Tag(name = "재무관리(FCM)", description = "재무 관리 API")
public class FcmController {

	private static final Set<String> ALLOWED_PERIODS = Set.of("week", "month", "quarter", "year");

	// ==================== 재무 관리 통계 ====================

	@GetMapping("/statictics")
	@Operation(
		summary = "FCM 통계 조회",
		description = "기간별 재무 관리 통계를 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "성공",
						content = @Content(mediaType = "application/json",
							examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"재무 통계 데이터를 성공적으로 조회했습니다.\",\n  \"data\": {\n    \"week\": {\n      \"total_sales\": { \"value\": 68500000, \"delta_rate\": 0.082 },\n      \"total_purchases\": { \"value\": 43200000, \"delta_rate\": 0.054 },\n      \"net_profit\": { \"value\": 21000000, \"delta_rate\": 0.097 },\n      \"accounts_receivable\": { \"value\": 12500000, \"delta_rate\": -0.012 }\n    },\n    \"month\": {\n      \"total_sales\": { \"value\": 275000000, \"delta_rate\": 0.125 },\n      \"total_purchases\": { \"value\": 189000000, \"delta_rate\": 0.083 },\n      \"net_profit\": { \"value\": 86000000, \"delta_rate\": 0.153 },\n      \"accounts_receivable\": { \"value\": 25000000, \"delta_rate\": -0.032 }\n    }\n  }\n}"))
				)
		}
	)
	public ResponseEntity<ApiResponse<StatsResponseDto<StatsMetricsDto>>> getStatistics(
		@Parameter(description = "조회 기간 목록(콤마 구분)")
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
			throw new ValidationException(ErrorCode.VALIDATION_FAILED, List.of(Map.of("field", "periods", "reason", "ALLOWED_VALUES: WEEK, MONTH, QUARTER, YEAR")));
		}

		List<String> finalPeriods = requested.stream().filter(ALLOWED_PERIODS::contains).toList();
		StatsResponseDto.StatsResponseDtoBuilder<StatsMetricsDto> builder = StatsResponseDto.<StatsMetricsDto>builder();

		if (finalPeriods.contains("week")) {
			builder.week(buildMetrics(68_500_000L, new BigDecimal("0.082"), 43_200_000L, new BigDecimal("0.054"), 21_000_000L, new BigDecimal("0.097"), 12_500_000L, new BigDecimal("-0.012")));
		}
		if (finalPeriods.contains("month")) {
			builder.month(buildMetrics(275_000_000L, new BigDecimal("0.125"), 189_000_000L, new BigDecimal("0.083"), 86_000_000L, new BigDecimal("0.153"), 25_000_000L, new BigDecimal("-0.032")));
		}
		if (finalPeriods.contains("quarter")) {
			builder.quarter(buildMetrics(812_000_000L, new BigDecimal("0.094"), 596_000_000L, new BigDecimal("0.071"), 248_000_000L, new BigDecimal("0.118"), 74_000_000L, new BigDecimal("-0.021")));
		}
		if (finalPeriods.contains("year")) {
			builder.year(buildMetrics(3_215_000_000L, new BigDecimal("0.068"), 2_425_000_000L, new BigDecimal("0.057"), 978_000_000L, new BigDecimal("0.103"), 315_000_000L, new BigDecimal("-0.018")));
		}

		StatsResponseDto<StatsMetricsDto> data = builder.build();
		return ResponseEntity.ok(ApiResponse.success(data, "재무 통계 데이터를 성공적으로 조회했습니다.", HttpStatus.OK));
	}

	private StatsMetricsDto buildMetrics(
		long totalSales,
		BigDecimal totalSalesChange,
		long totalPurchases,
		BigDecimal totalPurchasesChange,
		long netProfit,
		BigDecimal netProfitChange,
		long accountsReceivable,
		BigDecimal accountsReceivableChange
	) {
		return StatsMetricsDto.builder()
			.put("total_sales", PeriodStatDto.builder().value(totalSales).deltaRate(totalSalesChange).build())
			.put("total_purchases", PeriodStatDto.builder().value(totalPurchases).deltaRate(totalPurchasesChange).build())
			.put("net_profit", PeriodStatDto.builder().value(netProfit).deltaRate(netProfitChange).build())
			.put("accounts_receivable", PeriodStatDto.builder().value(accountsReceivable).deltaRate(accountsReceivableChange).build())
			.build();
	}

	// ==================== 전표 목록 조회 (AP: 매입, AS: 매출) ====================

	@GetMapping("/invoice/ap")
	@Operation(
		summary = "매입 전표 목록 조회",
		description = "매입(AP) 전표 목록을 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "성공",
						content = @Content(mediaType = "application/json",
							examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매입 전표 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"invoiceId\": 1,\n        \"invoiceCode\": \"AP-2024-001\",\n        \"connection\": {\n          \"connectionId\": 1,\n          \"connectionCode\": \"C-001\",\n          \"connectionName\": \"현대자동차\"\n        },\n        \"totalAmount\": 10000000,\n        \"issueDate\": \"2025-10-14\",\n        \"dueDate\": \"2025-11-14\",\n        \"status\": \"UNPAID\",\n        \"referenceCode\": \"PO-2024-001\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 15,\n      \"totalPages\": 2,\n      \"hasNext\": true\n    }\n  }\n}"))
			)
		}
	)
	public ResponseEntity<ApiResponse<Object>> getApinvoices(
		@Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
		@Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
		@Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
		@Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
		@Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
	) {
		validateDateRange(startDate, endDate);
		int p = (page == null || page < 0) ? 0 : page;
		int s = (size == null || size < 1) ? 10 : size;
		Map<String, Object> data = generateinvoiceListMock(p, s, "AP");
		return ResponseEntity.ok(ApiResponse.success(data, "매출 전표 목록 조회에 성공했습니다.", HttpStatus.OK));
	}

    @GetMapping("/invoice/ar")
	@Operation(
		summary = "매출 전표 목록 조회",
		description = "매출(AR) 전표 목록을 조회합니다.",
		responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 전표 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"invoiceId\": 1,\n        \"invoiceCode\": \"AR-2024-001\",\n        \"connection\": {\n          \"connectionId\": 1,\n          \"connectionCode\": \"C-001\",\n          \"connectionName\": \"현대자동차\"\n        },\n        \"totalAmount\": 10000000,\n        \"issueDate\": \"2025-10-14\",\n        \"dueDate\": \"2025-11-14\",\n        \"status\": \"UNPAID\",\n        \"referenceCode\": \"SO-2024-001\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 15,\n      \"totalPages\": 2,\n      \"hasNext\": true\n    }\n  }\n}"))
			)
		}
	)
	public ResponseEntity<ApiResponse<Object>> getAsinvoices(
		@Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
		@Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
		@Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
		@Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
		@Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
	) {
		validateDateRange(startDate, endDate);
		int p = (page == null || page < 0) ? 0 : page;
		int s = (size == null || size < 1) ? 10 : size;
		Map<String, Object> data = generateinvoiceListMock(p, s, "AR");
		return ResponseEntity.ok(ApiResponse.success(data, "매출 전표 목록 조회에 성공했습니다.", HttpStatus.OK));
	}

	// ==================== 전표 상세 조회 ====================
    @GetMapping("/invoice/ap/{invoiceId}")
	@Operation(
		summary = "매입 전표 상세 조회",
		description = "매입(AP) 전표 상세 정보를 조회합니다.",
		responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매입 전표 상세 조회에 성공했습니다.\",\n  \"data\": {\n    \"invoiceId\": 2001,\n    \"invoiceCode\": \"AP2025-001\",\n    \"invoiceType\": \"AP\",\n    \"statusCode\": \"UNPAID\",\n    \"issueDate\": \"2025-01-20\",\n    \"dueDate\": \"2025-02-19\",\n    \"name\": \"대한철강\",\n    \"referenceCode\": \"PO2025-001\",\n    \"items\": [{\n      \"itemId\": 101,\n      \"itemName\": \"강판\",\n      \"quantity\": 500,\n      \"uomName\": \"kg\",\n      \"unitPrice\": 8000,\n      \"totalPrice\": 4000000\n    },{\n      \"itemId\": 201,\n      \"itemName\": \"알루미늄\",\n      \"quantity\": 300,\n      \"uomName\": \"kg\",\n      \"unitPrice\": 3333,\n      \"totalPrice\": 1000000\n    }],\n    \"totalAmount\": 5000000,\n    \"note\": \"1월 생산용 원자재 매입 분\"\n  }\n}"))
			)
		}
	)
	public ResponseEntity<ApiResponse<InvoiceDetailDto>> getApinvoiceDetail(
		@Parameter(description = "전표 ID", example = "1") @PathVariable("invoiceId") Long invoiceId
	) {
		InvoiceDetailDto detail = generatePurchaseInvoiceDetailMock(invoiceId);
		return ResponseEntity.ok(ApiResponse.success(detail, "매입 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK));
	}

    @GetMapping("/invoice/ar/{invoiceId}")
	@Operation(
		summary = "매출 전표 상세 조회",
		description = "매출(AR) 전표 상세 정보를 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "성공",
					content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 전표 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"invoiceId\": 1001,\n    \"invoiceCode\": \"AR2025-001\",\n    \"invoiceType\": \"AR\",\n    \"statusCode\": \"UNPAID\",\n    \"issueDate\": \"2025-10-14\",\n    \"dueDate\": \"2025-11-14\",\n    \"name\": \"삼성전자\",\n    \"referenceCode\": \"SO2025-001\",\n    \"items\": [{\n      \"itemId\": 900001,\n      \"itemName\": \"제품 A\",\n      \"quantity\": 10,\n      \"uomName\": \"EA\",\n      \"unitPrice\": 1000000,\n      \"totalPrice\": 10000000\n    },{\n      \"itemId\": 900011,\n      \"itemName\": \"제품 B\",\n      \"quantity\": 5,\n      \"uomName\": \"EA\",\n      \"unitPrice\": 1000000,\n      \"totalPrice\": 5000000\n    }],\n    \"totalAmount\": 15000000,\n    \"note\": \"도어 패널 100개 납품\"\n  }\n}"))
			)
		}
	)
	public ResponseEntity<ApiResponse<InvoiceDetailDto>> getAsinvoiceDetail(
		@Parameter(description = "전표 ID", example = "1") @PathVariable("invoiceId") Long invoiceId
	) {
		InvoiceDetailDto detail = generateSalesInvoiceDetailMock(invoiceId);
		return ResponseEntity.ok(ApiResponse.success(detail, "매출 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK));
	}

	// ==================== 전표 수정 (데이터만 받고 200) ====================

    @PatchMapping("/invoice/ap/{invoiceId}")
    @Operation(summary = "매입 전표 수정", description = "매입(AP) 전표를 수정합니다. 데이터만 받고 200 반환")
    public ResponseEntity<ApiResponse<Object>> patchApinvoice(
        @Parameter(description = "전표 ID") @PathVariable("invoiceId") Long invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    ) {
        System.out.println("매입 전표 수정 요청 - ID: " + invoiceId + ", 데이터: " + request);
        return ResponseEntity.ok(ApiResponse.success(null, "매입 전표 수정이 완료되었습니다.", HttpStatus.OK));
    }

    @PatchMapping("/invoice/ar/{invoiceId}")
    @Operation(summary = "매출 전표 수정", description = "매출(AR) 전표를 수정합니다. 데이터만 받고 200 반환")
    public ResponseEntity<ApiResponse<Object>> patchAsinvoice(
        @Parameter(description = "전표 ID") @PathVariable("invoiceId") Long invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    ) {
        System.out.println("매출 전표 수정 요청 - ID: " + invoiceId + ", 데이터: " + request);
        return ResponseEntity.ok(ApiResponse.success(null, "매출 전표 수정이 완료되었습니다.", HttpStatus.OK));
    }

    // ==================== 미수 처리 (모의) ====================

    @PostMapping("/invoice/ar/{invoiceId}/receivable/complete")
    @Operation(
        summary = "매출 전표 미수 처리 완료",
        description = "미납/확인요청 상태의 매출(AR) 전표에 대해 미수 처리를 완료합니다. (모의 처리: 항상 성공 반환)",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"미수 처리 완료되었습니다.\",\n  \"data\": null\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> completeReceivable(
        @Parameter(description = "매출 전표 ID") @PathVariable("invoiceId") Long invoiceId
    ) {
        System.out.println("미수 처리 완료 요청(모의) - ID: " + invoiceId);
        return ResponseEntity.ok(ApiResponse.success(null, "미수 처리 완료되었습니다.", HttpStatus.OK));
    }

    // ==================== 매입 전표 미수 처리 요청 (모의) ====================

    @PostMapping("/invoice/ap/receivable/request")
    @Operation(
        summary = "매입 전표 미수 처리 요청",
        description = "매입(AP) 전표에 대해 공급사에 미수 처리 요청을 발송합니다. (모의 처리: 항상 성공 반환)",
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "성공",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"미수 처리 요청을 공급사에 발송했습니다.\",\n  \"data\": null\n}"))
            )
        }
    )
    public ResponseEntity<ApiResponse<Object>> requestApReceivable(
        @Parameter(description = "매입 전표 ID", required = true) @RequestParam("invoiceId") Long invoiceId
    ) {
        System.out.println("매입 전표 미수 처리 요청(모의) - ID: " + invoiceId);
        return ResponseEntity.ok(ApiResponse.success(null, "미수 처리 요청을 공급사에 발송했습니다.", HttpStatus.OK));
    }

	// ==================== 내부 유틸/목 데이터 생성 ====================

	private void validateDateRange(String startDate, String endDate) {
		List<Map<String, String>> errors = new ArrayList<>();
		if (startDate != null) {
			try { LocalDate.parse(startDate); } catch (DateTimeParseException e) {
				errors.add(Map.of("field", "startDate", "reason", "INVALID_DATE"));
			}
		}
		if (endDate != null) {
			try { LocalDate.parse(endDate); } catch (DateTimeParseException e) {
				errors.add(Map.of("field", "endDate", "reason", "INVALID_DATE"));
			}
		}
		if (!errors.isEmpty()) {
			throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
		}
	}

	private Map<String, Object> generateinvoiceListMock(int page, int size, String type) {
		List<Map<String, Object>> all = new ArrayList<>();

		String[] partners = {"현대자동차", "삼성전자", "LG전자", "네이버", "카카오", "SK하이닉스", "한화시스템", "포스코", "CJ대한통운", "두산중공업"};
		String[] statuses = {"UNPAID", "PENDING", "PAID"};

		LocalDate baseIssueDate = LocalDate.parse("2025-01-01");
		for (int i = 0; i < 100; i++) {
			boolean isAp = "AP".equalsIgnoreCase(type);
			long baseId = isAp ? 2000L : 1000L;
			long invoiceId = baseId + i;
			String invoiceCode = String.format("%s-2024-%03d", isAp ? "AP" : "AR", i + 1);
			int partnerIdx = i % partners.length;
			long connectionId = partnerIdx + 1L;
			String connectionCode = String.format("C-%03d", partnerIdx + 1);
			String connectionName = partners[partnerIdx];
			int totalAmount = 5_000_000 + (i % 10) * 500_000;
			LocalDate issueDate = baseIssueDate.plusDays(i);
			LocalDate dueDate = issueDate.plusDays(30);
			String status = statuses[i % statuses.length];
			long referenceBase = isAp ? 1001L : 5001L;
			long referenceId = referenceBase + (i % 50);
			String referenceCode = String.format("%s-2024-%03d", isAp ? "PO" : "SO", (i % 50) + 1);

			all.add(buildinvoiceRow(invoiceId, invoiceCode, connectionId, connectionCode, connectionName, totalAmount, issueDate, dueDate, status, referenceId, referenceCode));
		}

		int totalElements = all.size();
		int pageSize = size <= 0 ? totalElements : size;
		int pageIndex = page < 0 ? 0 : page;
		int from = Math.min(pageIndex * pageSize, totalElements);
		int to = Math.min(from + pageSize, totalElements);
		List<Map<String, Object>> pageContent = all.subList(from, to);

		int totalPages = pageSize == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);
		boolean hasNext = pageIndex + 1 < totalPages;

		PageDto pageInfo = PageDto.builder()
			.number(pageIndex)
			.size(pageSize)
			.totalElements(totalElements)
			.totalPages(totalPages)
			.hasNext(hasNext)
			.build();

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("content", pageContent);
		response.put("page", pageInfo);
		response.put("totalElements", totalElements);
		response.put("totalPages", totalPages);
		response.put("first", pageIndex == 0);
		response.put("last", pageIndex + 1 >= totalPages);
		response.put("numberOfElements", pageContent.size());
		response.put("empty", pageContent.isEmpty());
		return response;
	}

	private Map<String, Object> buildinvoiceRow(
			Long invoiceId,
			String invoiceCode,
			Long connectionId,
			String connectionCode,
			String connectionName,
			int totalAmount,
			LocalDate issueDate,
			LocalDate dueDate,
			String status,
			Long referenceId,
			String referenceCode
		) {
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("invoiceId", invoiceId);
		row.put("invoiceCode", invoiceCode);
		Map<String, Object> connection = new LinkedHashMap<>();
		connection.put("connectionId", connectionId);
		connection.put("connectionCode", connectionCode);
		connection.put("connectionName", connectionName);
		row.put("connection", connection);
		row.put("totalAmount", totalAmount);
		row.put("issueDate", issueDate);
		row.put("dueDate", dueDate);
		row.put("status", status);
		row.put("referenceCode", referenceCode);
		Map<String, Object> reference = new LinkedHashMap<>();
		reference.put("referenceId", referenceId);
		reference.put("referenceCode", referenceCode);
		row.put("reference", reference);
		return row;
	}

    private InvoiceDetailDto generatePurchaseInvoiceDetailMock(Long invoiceId) {
        long resolvedId = invoiceId == null ? 2001L : invoiceId;

        PurchaseInvoiceItemDto item1 = PurchaseInvoiceItemDto.builder()
                .itemName("강판")
                .quantity(500)
                .uomName("kg")
                .unitPrice(8_000L)
                .totalPrice(4_000_000L)
                .build();

        PurchaseInvoiceItemDto item2 = PurchaseInvoiceItemDto.builder()
                .itemName("알루미늄")
                .quantity(300)
                .uomName("kg")
                .unitPrice(3_333L)
                .totalPrice(1_000_000L)
                .build();

        String invoiceCode = String.format("AP2025-%03d", Math.toIntExact(resolvedId % 1000));

        return InvoiceDetailDto.builder()
                .invoiceId(resolvedId)
                .invoiceCode(invoiceCode)
                .invoiceType("AP")
                .statusCode("UNPAID")
                .issueDate(LocalDate.parse("2025-01-20"))
                .dueDate(LocalDate.parse("2025-02-19"))
                .name("대한철강")
                .referenceCode("PO2025-001")
                .totalAmount(item1.getTotalPrice() + item2.getTotalPrice())
                .items(List.of(item1, item2))
                .note("1월 생산용 원자재 매입 분")
                .build();
    }

    private InvoiceDetailDto generateSalesInvoiceDetailMock(Long invoiceId) {
        long resolvedId = invoiceId == null ? 1001L : invoiceId;

        PurchaseInvoiceItemDto item1 = PurchaseInvoiceItemDto.builder()
                .itemName("제품 A")
                .quantity(10)
                .uomName("EA")
                .unitPrice(1_000_000L)
                .totalPrice(10_000_000L)
                .build();

        PurchaseInvoiceItemDto item2 = PurchaseInvoiceItemDto.builder()
                .itemName("제품 B")
                .quantity(5)
                .uomName("EA")
                .unitPrice(1_000_000L)
                .totalPrice(5_000_000L)
                .build();

        String invoiceCode = String.format("AR2025-%03d", Math.toIntExact(resolvedId % 1000));

        return InvoiceDetailDto.builder()
                .invoiceId(resolvedId)
                .invoiceCode(invoiceCode)
                .invoiceType("AR")
                .statusCode("UNPAID")
                .issueDate(LocalDate.parse("2025-10-14"))
                .dueDate(LocalDate.parse("2025-11-14"))
                .name("삼성전자")
                .referenceCode("SO2025-001")
                .totalAmount(item1.getTotalPrice() + item2.getTotalPrice())
                .items(List.of(item1, item2))
                .note("도어 패널 100개 납품")
                .build();
    }
}
