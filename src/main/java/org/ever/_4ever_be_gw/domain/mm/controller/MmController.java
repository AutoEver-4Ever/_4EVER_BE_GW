package org.ever._4ever_be_gw.domain.mm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.domain.mm.dto.PeriodMetrics;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.domain.mm.service.MmStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<org.ever._4ever_be_gw.common.response.ApiResponse<Map<String, PeriodMetrics>>> getStatistics(
            @Parameter(name = "periods", description = "조회 기간 목록(콤마 구분). 예: week,month,quarter,year", example = "week,month,quarter,year")
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
            throw new org.ever._4ever_be_gw.common.exception.BusinessException(
                    org.ever._4ever_be_gw.common.exception.ErrorCode.INVALID_PERIODS
            );
        }

        List<String> finalPeriods = requested.stream()
                .filter(ALLOWED_PERIODS::contains)
                .toList();

        Map<String, PeriodMetrics> data = mmStatisticsService.getStatistics(finalPeriods);
        return ResponseEntity.ok(org.ever._4ever_be_gw.common.response.ApiResponse.success(data, "OK", HttpStatus.OK));
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
    public ResponseEntity<org.ever._4ever_be_gw.common.response.ApiResponse<Object>> getPurchaseRequisitions(
            @Parameter(description = "상태 필터 예: PENDING, APPROVED")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "생성일 시작(YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam(name = "createdFrom", required = false) String createdFrom,
            @Parameter(description = "생성일 종료(YYYY-MM-DD)", example = "2024-01-31")
            @RequestParam(name = "createdTo", required = false) String createdTo,
            @Parameter(description = "정렬 필드,정렬방향", example = "createdAt,desc")
            @RequestParam(name = "sort", required = false, defaultValue = "createdAt,desc") String sort,
            @Parameter(description = "페이지 번호(0-base)", example = "0")
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기(최대 200)", example = "20")
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
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
            throw new org.ever._4ever_be_gw.common.exception.ValidationException(
                    org.ever._4ever_be_gw.common.exception.ErrorCode.VALIDATION_FAILED,
                    errors
            );
        }

        // 403 샘플 조건: 과거 특정 기준 이전 조회는 금지 (모킹)
        if (fromDate != null && fromDate.isBefore(java.time.LocalDate.of(2024, 1, 1))) {
            throw new org.ever._4ever_be_gw.common.exception.BusinessException(
                    org.ever._4ever_be_gw.common.exception.ErrorCode.FORBIDDEN_RANGE
            );
        }

        // 성공 응답 (목업)
        java.util.List<Map<String, Object>> content = new java.util.ArrayList<>();
        content.add(new java.util.LinkedHashMap<>() {{
            put("id", 102345L);
            put("prNumber", "100002345");
            put("requesterId", 123L);
            put("requesterName", "홍길동");
            put("departmentId", 12L);
            put("departmentName", "영업1팀");
            put("origin", "MRP");
            put("originRefId", "MRP-2025-10-01-00123");
            put("createdAt", java.time.Instant.parse("2025-10-05T12:30:45Z"));
            put("createdBy", 123L);
            put("itemCount", 2);
            put("hasPreferredVendor", true);
        }});
        content.add(new java.util.LinkedHashMap<>() {{
            put("id", 102346L);
            put("prNumber", "100002346");
            put("requesterId", 124L);
            put("requesterName", "김민수");
            put("departmentId", 12L);
            put("departmentName", "영업1팀");
            put("origin", "MANUAL");
            put("originRefId", null);
            put("createdAt", java.time.Instant.parse("2025-10-05T12:35:02Z"));
            put("createdBy", 124L);
            put("itemCount", 1);
            put("hasPreferredVendor", false);
        }});

        Map<String, Object> pageMeta = new LinkedHashMap<>();
        pageMeta.put("number", page);
        pageMeta.put("size", size);
        pageMeta.put("totalElements", 257);
        pageMeta.put("totalPages", 13);
        pageMeta.put("hasNext", (page + 1) < 13);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", content);
        data.put("page", pageMeta);

        return ResponseEntity.ok(org.ever._4ever_be_gw.common.response.ApiResponse.<Object>success(
                data, "구매요청서 목록입니다.", HttpStatus.OK
        ));
    }
}
