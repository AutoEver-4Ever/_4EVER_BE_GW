package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.business.dto.StatementUpdateRequestDto;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.scmpp.dto.PeriodStatDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/statement/ap")
	@Operation(
		summary = "매입 전표 목록 조회",
		description = "매입(AP) 전표 목록을 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "성공",
						content = @Content(mediaType = "application/json",
							examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매입 전표 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"statementId\": 1,\n        \"statementCode\": \"AP-2024-001\",\n        \"connection\": {\n          \"connectionId\": 1,\n          \"connectionCode\": \"C-001\",\n          \"connectionName\": \"현대자동차\"\n        },\n        \"totalAmount\": 10000000,\n        \"issueDate\": \"2025-10-14\",\n        \"dueDate\": \"2025-11-14\",\n        \"status\": \"UNPAID\",\n        \"referenceCode\": \"PO-2024-001\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 15,\n      \"totalPages\": 2,\n      \"hasNext\": true\n    }\n  }\n}"))
			)
		}
	)
	public ResponseEntity<ApiResponse<Object>> getApStatements(
		@Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
		@Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
		@Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
		@Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
		@Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
	) {
		validateDateRange(startDate, endDate);
		int p = (page == null || page < 0) ? 0 : page;
		int s = (size == null || size < 1) ? 10 : size;
		Map<String, Object> data = generateStatementListMock(p, s, "AP");
		return ResponseEntity.ok(ApiResponse.success(data, "매출 전표 목록 조회에 성공했습니다.", HttpStatus.OK));
	}

	@GetMapping("/statement/as")
	@Operation(
		summary = "매출 전표 목록 조회",
		description = "매출(AS) 전표 목록을 조회합니다.",
		responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 전표 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"statementId\": 1,\n        \"statementCode\": \"AR-2024-001\",\n        \"connection\": {\n          \"connectionId\": 1,\n          \"connectionCode\": \"C-001\",\n          \"connectionName\": \"현대자동차\"\n        },\n        \"totalAmount\": 10000000,\n        \"issueDate\": \"2025-10-14\",\n        \"dueDate\": \"2025-11-14\",\n        \"status\": \"UNPAID\",\n        \"referenceCode\": \"SO-2024-001\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 15,\n      \"totalPages\": 2,\n      \"hasNext\": true\n    }\n  }\n}"))
			)
		}
	)
	public ResponseEntity<ApiResponse<Object>> getAsStatements(
		@Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
		@Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
		@Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
		@Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
		@Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
	) {
		validateDateRange(startDate, endDate);
		int p = (page == null || page < 0) ? 0 : page;
		int s = (size == null || size < 1) ? 10 : size;
		Map<String, Object> data = generateStatementListMock(p, s, "AS");
		return ResponseEntity.ok(ApiResponse.success(data, "매출 전표 목록 조회에 성공했습니다.", HttpStatus.OK));
	}

	// ==================== 전표 상세 조회 ====================

	@GetMapping("/statement/ap/{statementId}")
	@Operation(
		summary = "매입 전표 상세 조회",
		description = "매입(AP) 전표 상세 정보를 조회합니다.",
		responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 전표 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"statementId\": 1,\n    \"statementCode\": \"AR-2024-001\",\n    \"connection\": {\n      \"connectionId\": 1,\n      \"connectionCode\": \"C-001\",\n      \"connectionName\": \"현대자동차\"\n    },\n    \"totalAmount\": 10000000,\n    \"issueDate\": \"2025-10-14\",\n    \"dueDate\": \"2025-11-14\",\n    \"status\": \"UNPAID\",\n    \"reference\": {\n      \"referenceId\": 1,\n      \"referenceCode\": \"SO-2024-001\"\n    },\n    \"note\": \"도어 패널 100개 납품\",\n    \"items\": [{\n      \"itemName\": \"강판 A급\",\n      \"quantity\": 50,\n      \"unit\": \"매\",\n      \"unitPrice\": 80000,\n      \"totalPrice\": 4000000\n    }]\n  }\n}"))
			)
		}
	)
	public ResponseEntity<ApiResponse<Object>> getApStatementDetail(
		@Parameter(description = "전표 ID", example = "1") @PathVariable("statementId") Long statementId
	) {
		Map<String, Object> data = generateStatementDetailMock(statementId, "AP");
		return ResponseEntity.ok(ApiResponse.success(data, "매출 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK));
	}

	@GetMapping("/statement/as/{statementId}")
	@Operation(
		summary = "매출 전표 상세 조회",
		description = "매출(AS) 전표 상세 정보를 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "성공",
					content = @Content(mediaType = "application/json",
						examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 전표 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"statementId\": 1,\n    \"statementCode\": \"AR-2024-001\",\n    \"connection\": {\n      \"connectionId\": 1,\n      \"connectionCode\": \"C-001\",\n      \"connectionName\": \"현대자동차\"\n    },\n    \"totalAmount\": 10000000,\n    \"issueDate\": \"2025-10-14\",\n    \"dueDate\": \"2025-11-14\",\n    \"status\": \"UNPAID\",\n    \"reference\": {\n      \"referenceId\": 1,\n      \"referenceCode\": \"PO-2024-001\"\n    },\n    \"note\": \"도어 패널 100개 납품\",\n    \"items\": [{\n      \"itemName\": \"강판 A급\",\n      \"quantity\": 50,\n      \"unit\": \"매\",\n      \"unitPrice\": 80000,\n      \"totalPrice\": 4000000\n    }]\n  }\n}"))
			)
		}
	)
	public ResponseEntity<ApiResponse<Object>> getAsStatementDetail(
		@Parameter(description = "전표 ID", example = "1") @PathVariable("statementId") Long statementId
	) {
		Map<String, Object> data = generateStatementDetailMock(statementId, "AS");
		return ResponseEntity.ok(ApiResponse.success(data, "매출 전표 상세 정보 조회에 성공했습니다.", HttpStatus.OK));
	}

	// ==================== 전표 수정 (데이터만 받고 200) ====================

    @PatchMapping("/statement/ap/{statementId}")
    @Operation(summary = "매입 전표 수정", description = "매입(AP) 전표를 수정합니다. 데이터만 받고 200 반환")
    public ResponseEntity<ApiResponse<Object>> patchApStatement(
        @Parameter(description = "전표 ID") @PathVariable("statementId") Long statementId,
        @Valid @RequestBody StatementUpdateRequestDto request
    ) {
        System.out.println("매입 전표 수정 요청 - ID: " + statementId + ", 데이터: " + request);
        return ResponseEntity.ok(ApiResponse.success(null, "매입 전표 수정이 완료되었습니다.", HttpStatus.OK));
    }

	@PatchMapping("/statement/as/{statementId}")
	@Operation(summary = "매출 전표 수정", description = "매출(AS) 전표를 수정합니다. 데이터만 받고 200 반환")
    public ResponseEntity<ApiResponse<Object>> patchAsStatement(
        @Parameter(description = "전표 ID") @PathVariable("statementId") Long statementId,
        @Valid @RequestBody StatementUpdateRequestDto request
    ) {
        System.out.println("매출 전표 수정 요청 - ID: " + statementId + ", 데이터: " + request);
        return ResponseEntity.ok(ApiResponse.success(null, "매출 전표 수정이 완료되었습니다.", HttpStatus.OK));
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

	private Map<String, Object> generateStatementListMock(int page, int size, String type) {
		List<Map<String, Object>> content = new ArrayList<>();
			content.add(buildStatementRow(1L, type.equals("AP") ? "AP-2024-001" : "AR-2024-001",
					1L, "C-001", "현대자동차", 10_000_000,
					LocalDate.parse("2025-10-14"),
					LocalDate.parse("2025-11-14"),
					"UNPAID",
					type.equals("AP") ? "PO-2024-001" : "SO-2024-001"));
			content.add(buildStatementRow(2L, type.equals("AP") ? "AP-2024-002" : "AR-2024-002",
					2L, "C-002", "삼성전자", 25_000_000,
					LocalDate.parse("2025-10-10"),
					LocalDate.parse("2025-11-10"),
					"PENDING",
					type.equals("AP") ? "PO-2024-005" : "SO-2024-025"));

		int totalElements = 15;
		int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / size);
		PageDto pageInfo = PageDto.builder()
			.number(page)
			.size(size)
			.totalElements(totalElements)
			.totalPages(totalPages)
			.hasNext(page + 1 < totalPages)
			.build();

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("content", content);
		response.put("page", pageInfo);
		response.put("totalElements", totalElements);
		response.put("totalPages", totalPages);
		response.put("first", page == 0);
		response.put("last", page + 1 >= totalPages);
		response.put("numberOfElements", content.size());
		response.put("empty", content.isEmpty());
		return response;
	}

		private Map<String, Object> buildStatementRow(
			Long statementId,
			String statementCode,
			Long connectionId,
			String connectionCode,
			String connectionName,
			int totalAmount,
			LocalDate issueDate,
			LocalDate dueDate,
			String status,
			String referenceCode
		) {
		Map<String, Object> row = new LinkedHashMap<>();
		row.put("statementId", statementId);
		row.put("statementCode", statementCode);
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
		reference.put("referenceId", statementId);
		reference.put("referenceCode", referenceCode);
		row.put("reference", reference);
		return row;
	}

	private Map<String, Object> generateStatementDetailMock(Long statementId, String type) {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("statementId", statementId);
		data.put("statementCode", type.equals("AP") ? "AP-2024-001" : "AR-2024-001");
		Map<String, Object> connection = new LinkedHashMap<>();
		connection.put("connectionId", 1L);
		connection.put("connectionCode", "C-001");
		connection.put("connectionName", "현대자동차");
		data.put("connection", connection);
		data.put("totalAmount", 10_000_000);
		data.put("issueDate", LocalDate.parse("2025-10-14"));
		data.put("dueDate", LocalDate.parse("2025-11-14"));
		data.put("status", "UNPAID");
		Map<String, Object> reference = new LinkedHashMap<>();
		reference.put("referenceId", 1L);
		reference.put("referenceCode", type.equals("AP") ? "PO-2024-001" : "SO-2024-001");
		data.put("reference", reference);
		data.put("note", "도어 패널 100개 납품");
		List<Map<String, Object>> items = new ArrayList<>();
		items.add(buildItem("강판 A급", 50, "매", 80_000));
		items.add(buildItem("사이드 미러 부품", 100, "개", 60_000));
		data.put("items", items);
		return data;
	}

	private Map<String, Object> buildItem(String itemName, int quantity, String unit, int unitPrice) {
		Map<String, Object> item = new LinkedHashMap<>();
		item.put("itemName", itemName);
		item.put("quantity", quantity);
		item.put("unit", unit);
		item.put("unitPrice", unitPrice);
		item.put("totalPrice", quantity * unitPrice);
		return item;
	}
}
