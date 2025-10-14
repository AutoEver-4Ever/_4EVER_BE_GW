package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ever._4ever_be_gw.business.dto.StatementUpdateRequestDto;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.ever._4ever_be_gw.common.response.ApiResponse;
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
@Tag(name = "Finance & Cost Management", description = "재무 관리 API")
public class FcmController {

	// ==================== 재무 관리 통계 ====================

	@GetMapping("/statictics")
	@Operation(
		summary = "재무 관리 통계",
		description = "기간별 재무 관리 통계를 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "성공",
					content = @Content(mediaType = "application/json",
						examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 현황 정보를 성공적으로 조회했습니다.\",\n  \"data\": {\n    \"totalSales\": 125000000,\n    \"totalSalesChange\": 12.5,\n    \"totalPurchases\": 85000000,\n    \"totalPurchasesChange\": 8.2,\n    \"netProfit\": 40000000,\n    \"netProfitChange\": 15.3,\n    \"accountsReceivable\": 25000000,\n    \"accountsReceivableChange\": -3.2\n  }\n}"))
			)
		}
	)
	public ResponseEntity<ApiResponse<Object>> getStatistics(
		@Parameter(description = "검색 기간: WEEK, MONTH, QUARTER, YEAR", example = "MONTH")
		@RequestParam(name = "period", required = false, defaultValue = "MONTH") String period
	) {
		List<Map<String, String>> errors = new ArrayList<>();
		if (period != null) {
			var allowed = Set.of("WEEK", "MONTH", "QUARTER", "YEAR");
			if (!allowed.contains(period)) {
				errors.add(Map.of("field", "period", "reason", "ALLOWED_VALUES: WEEK, MONTH, QUARTER, YEAR"));
			}
		}
		if (!errors.isEmpty()) {
			throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
		}

		Map<String, Object> data = new LinkedHashMap<>();
		data.put("totalSales", 125000000);
		data.put("totalSalesChange", 12.5);
		data.put("totalPurchases", 85000000);
		data.put("totalPurchasesChange", 8.2);
		data.put("netProfit", 40000000);
		data.put("netProfitChange", 15.3);
		data.put("accountsReceivable", 25000000);
		data.put("accountsReceivableChange", -3.2);

		return ResponseEntity.ok(ApiResponse.success(data, "매출 현황 정보를 성공적으로 조회했습니다.", HttpStatus.OK));
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
						examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 전표 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"statementId\": 1,\n        \"statementCode\": \"AR-2024-001\",\n        \"connection\": {\n          \"connectionId\": 1,\n          \"connectionCode\": \"C-001\",\n          \"connectionName\": \"현대자동차\"\n        },\n        \"totalAmount\": 10000000,\n        \"issueDate\": \"2025-10-14T19:31:50.123456789\",\n        \"dueDate\": \"2025-11-14T19:31:50.123456789\",\n        \"status\": \"UNPAID\",\n        \"referenceCode\": \"SO-2024-001\",\n        \"reference\": {\n          \"referenceId\": 1,\n          \"referenceCode\": \"SO-2024-001\"\n        }\n      }\n    ],\n    \"pageable\": {\n      \"sort\": {\n        \"sorted\": false,\n        \"unsorted\": true,\n        \"empty\": true\n      },\n      \"offset\": 0,\n      \"pageNumber\": 0,\n      \"pageSize\": 10,\n      \"paged\": true,\n      \"unpaged\": false\n    },\n    \"totalPages\": 2,\n    \"totalElements\": 15,\n    \"last\": true,\n    \"size\": 10,\n    \"number\": 0,\n    \"sort\": {\n      \"sorted\": false,\n      \"unsorted\": true,\n      \"empty\": true\n    },\n    \"numberOfElements\": 2,\n    \"first\": true,\n    \"empty\": false\n  }\n}"))
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
						examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 전표 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"statementId\": 1,\n        \"statementCode\": \"AR-2024-001\",\n        \"connection\": {\n          \"connectionId\": 1,\n          \"connectionCode\": \"C-001\",\n          \"connectionName\": \"현대자동차\"\n        },\n        \"totalAmount\": 10000000,\n        \"issueDate\": \"2025-10-14T19:31:50.123456789\",\n        \"dueDate\": \"2025-11-14T19:31:50.123456789\",\n        \"status\": \"UNPAID\",\n        \"referenceCode\": \"SO-2024-001\",\n        \"reference\": {\n          \"referenceId\": 1,\n          \"referenceCode\": \"PO-2024-001\"\n        }\n      }\n    ]\n  }\n}"))
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
						examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 전표 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"statementId\": 1,\n    \"statementCode\": \"AR-2024-001\",\n    \"connection\": {\n      \"connectionId\": 1,\n      \"connectionCode\": \"C-001\",\n      \"connectionName\": \"현대자동차\"\n    },\n    \"totalAmount\": 10000000,\n    \"issueDate\": \"2025-10-14T19:31:50.123456789\",\n    \"dueDate\": \"2025-11-14T19:31:50.123456789\",\n    \"status\": \"UNPAID\",\n    \"reference\": {\n      \"referenceId\": 1,\n      \"referenceCode\": \"SO-2024-001\"\n    },\n    \"note\": \"도어 패널 100개 납품\",\n    \"items\": [{\n      \"itemName\": \"강판 A급\",\n      \"quantity\": 50,\n      \"unit\": \"매\",\n      \"unitPrice\": 80000,\n      \"totalPrice\": 4000000\n    }]\n  }\n}"))
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
						examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"매출 전표 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"statementId\": 1,\n    \"statementCode\": \"AR-2024-001\",\n    \"connection\": {\n      \"connectionId\": 1,\n      \"connectionCode\": \"C-001\",\n      \"connectionName\": \"현대자동차\"\n    },\n    \"totalAmount\": 10000000,\n    \"issueDate\": \"2025-10-14T19:31:50.123456789\",\n    \"dueDate\": \"2025-11-14T19:31:50.123456789\",\n    \"status\": \"UNPAID\",\n    \"reference\": {\n      \"referenceId\": 1,\n      \"referenceCode\": \"PO-2024-001\"\n    },\n    \"note\": \"도어 패널 100개 납품\",\n    \"items\": [{\n      \"itemName\": \"강판 A급\",\n      \"quantity\": 50,\n      \"unit\": \"매\",\n      \"unitPrice\": 80000,\n      \"totalPrice\": 4000000\n    }]\n  }\n}"))
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
		// Row 1
		content.add(buildStatementRow(1L, type.equals("AP") ? "AP-2024-001" : "AR-2024-001",
				1L, "C-001", "현대자동차", 10_000_000,
				LocalDateTime.parse("2025-10-14T19:31:50.123456789"),
				LocalDateTime.parse("2025-11-14T19:31:50.123456789"),
				"UNPAID",
				type.equals("AP") ? "PO-2024-001" : "SO-2024-001"));
		// Row 2
		content.add(buildStatementRow(2L, type.equals("AP") ? "AP-2024-002" : "AR-2024-002",
				2L, "C-002", "삼성전자", 25_000_000,
				LocalDateTime.parse("2025-10-10T11:00:00.000000000"),
				LocalDateTime.parse("2025-11-10T11:00:00.000000000"),
				"PENDING",
				type.equals("AP") ? "PO-2024-005" : "SO-2024-025"));

		Map<String, Object> pageable = new LinkedHashMap<>();
		Map<String, Object> sort = new LinkedHashMap<>();
		sort.put("sorted", false);
		sort.put("unsorted", true);
		sort.put("empty", true);
		pageable.put("sort", sort);
		pageable.put("offset", page * size);
		pageable.put("pageNumber", page);
		pageable.put("pageSize", size);
		pageable.put("paged", true);
		pageable.put("unpaged", false);

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("content", content);
		response.put("pageable", pageable);
		response.put("totalPages", 2);
		response.put("totalElements", 15);
		response.put("last", true);
		response.put("size", size);
		response.put("number", page);
		response.put("sort", sort);
		response.put("numberOfElements", content.size());
		response.put("first", page == 0);
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
		LocalDateTime issueDate,
		LocalDateTime dueDate,
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
		data.put("issueDate", LocalDateTime.parse("2025-10-14T19:31:50.123456789"));
		data.put("dueDate", LocalDateTime.parse("2025-11-14T19:31:50.123456789"));
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
