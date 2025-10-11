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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(org.ever._4ever_be_gw.common.response.ApiResponse.fail("요청 파라미터 'periods' 값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST));
        }

        List<String> finalPeriods = requested.stream()
                .filter(ALLOWED_PERIODS::contains)
                .toList();

        try {
            Map<String, PeriodMetrics> data = mmStatisticsService.getStatistics(finalPeriods);
            return ResponseEntity.ok(org.ever._4ever_be_gw.common.response.ApiResponse.success(data, "OK", HttpStatus.OK));
        } catch (BusinessException e) {
            // 공통 예외는 전역 예외 처리기로 위임하여 일관된 응답을 반환
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(org.ever._4ever_be_gw.common.response.ApiResponse.fail("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
