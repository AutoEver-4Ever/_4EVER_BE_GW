package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.business.dto.QuotationRequestDto;
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
@Tag(name = "SD Statistics", description = "영업관리(SD) 통계 조회 API")
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
}
