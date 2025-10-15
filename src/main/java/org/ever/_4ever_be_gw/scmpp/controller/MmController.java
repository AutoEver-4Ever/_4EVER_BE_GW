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
import org.ever._4ever_be_gw.common.util.PageResponseUtils;
import org.ever._4ever_be_gw.scmpp.service.MmStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.ever._4ever_be_gw.scmpp.dto.MmPurchaseRequisitionCreateRequestDto;
import org.ever._4ever_be_gw.scmpp.dto.MmPurchaseRequisitionUpdateRequestDto;
import org.ever._4ever_be_gw.scmpp.dto.MmPurchaseRequisitionRejectRequestDto;
import org.ever._4ever_be_gw.scmpp.dto.MmVendorCreateRequestDto;
import org.ever._4ever_be_gw.scmpp.dto.MmVendorUpdateRequestDto;

@RestController
@RequestMapping("/scm-pp/mm")
@Tag(name = "구매관리(MM)", description = "구매관리(MM) API")
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매요청서 목록입니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"id\": 1,\n        \"prNumber\": \"PR-2025-001\",\n        \"requesterId\": 123,\n        \"requesterName\": \"홍길동\",\n        \"departmentId\": 12,\n        \"departmentName\": \"영업1팀\",\n        \"origin\": \"MRP\",\n        \"originRefId\": \"MRP-2025-10-01-00123\",\n        \"createdAt\": \"2025-10-05T12:30:45Z\",\n        \"desiredDeliveryDate\": \"2025-10-12\",\n        \"createdBy\": 123,\n        \"itemCount\": 2,\n        \"hasPreferredVendor\": true\n      },\n      {\n        \"id\": 2,\n        \"prNumber\": \"PR-2025-002\",\n        \"requesterId\": 124,\n        \"requesterName\": \"김민수\",\n        \"departmentId\": 12,\n        \"departmentName\": \"영업1팀\",\n        \"origin\": \"MANUAL\",\n        \"originRefId\": null,\n        \"createdAt\": \"2025-10-05T12:35:02Z\",\n        \"desiredDeliveryDate\": \"2025-10-12\",\n        \"createdBy\": 124,\n        \"itemCount\": 1,\n        \"hasPreferredVendor\": false\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 50,\n      \"totalPages\": 3,\n      \"hasNext\": true\n    }\n  }\n}"))
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
            @Parameter(description = "요청자명 검색")
            @RequestParam(name = "requesterName", required = false) String requesterName,
            @Parameter(description = "요청 부서 ID 필터")
            @RequestParam(name = "departmentId", required = false) Long departmentId,
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

        // 성공 응답 (목업) - 50개
        java.util.List<Map<String, Object>> content = new java.util.ArrayList<>();
        for (int i = 0; i < 50; i++) {
            final long id = 1L + i; // 상세와 정렬을 맞추기 위해 1~50 사용
            final long requesterId = 123L + (i % 5);
            final String requesterNameVal = switch (i % 5) {
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
            // 변경: PR 번호 포맷을 PR-2025-001, PR-2025-002 ... 로 반환
            row.put("prNumber", String.format("PR-2025-%03d", 1 + i));
            row.put("requesterId", requesterId);
            row.put("requesterName", requesterNameVal);
            long deptId = (i % 2 == 0) ? 12L : 15L;
            String deptName = (deptId == 12L) ? "영업1팀" : "경영지원팀";
            row.put("departmentId", deptId);
            row.put("departmentName", deptName);
            row.put("origin", origin);
            row.put("originRefId", originRef);
            row.put("createdAt", createdAt);
            // 추가: 납기일(desiredDeliveryDate) 반환 (샘플: 요청일 기준 +7일)
            java.time.LocalDate desiredDeliveryDate = createdAt.atZone(java.time.ZoneOffset.UTC).toLocalDate().plusDays(7);
            row.put("desiredDeliveryDate", desiredDeliveryDate);
            row.put("createdBy", requesterId);
            row.put("itemCount", itemCount);
            row.put("hasPreferredVendor", hasPreferred);
            content.add(row);
        }

        // 필터 적용 (requesterName, departmentId)
        java.util.List<Map<String, Object>> filtered = content;
        if (requesterName != null && !requesterName.isBlank()) {
            final String kw = requesterName.toLowerCase();
            filtered = filtered.stream()
                    .filter(m -> String.valueOf(m.get("requesterName")).toLowerCase().contains(kw))
                    .toList();
        }
        if (departmentId != null) {
            final long did = departmentId;
            filtered = filtered.stream()
                    .filter(m -> java.util.Objects.equals(((Number)m.get("departmentId")).longValue(), did))
                    .toList();
        }

        int total = filtered.size();
        int fromIdx = Math.min(p * s, total);
        int toIdx = Math.min(fromIdx + s, total);
        java.util.List<Map<String, Object>> pageContent = filtered.subList(fromIdx, toIdx);

        org.ever._4ever_be_gw.common.dto.PageDto pageDto = org.ever._4ever_be_gw.common.dto.PageDto.builder()
                .number(p)
                .size(s)
                .totalElements(total)
                .totalPages(s == 0 ? 0 : (int) Math.ceil((double) total / s))
                .hasNext(p + 1 < (s == 0 ? 0 : (int) Math.ceil((double) total / s)))
                .build();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", pageContent);
        data.put("page", pageDto);

        return ResponseEntity.ok(ApiResponse.<Object>success(
                data, "구매요청서 목록입니다.", HttpStatus.OK
        ));
    }

    @PostMapping("/purchase-requisitions")
    @Operation(
            summary = "비재고성 자재 구매요청서 생성",
            description = "요청 본문에 포함된 품목들로 비재고성 자재 구매요청서를 생성합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "생성됨",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "created", value = "{\n  \"status\": 201,\n  \"success\": true,\n  \"message\": \"비재고성 자재 구매요청서가 생성되었습니다.\",\n  \"data\": {\n    \"prId\": 202510120001,\n    \"prNumber\": \"PR-NS-2025-00001\",\n    \"departmentId\": 12,\n    \"departmentName\": \"경영지원팀\",\n    \"requesterId\": 123,\n    \"requestDate\": \"2025-10-12\",\n    \"createdAt\": \"2025-10-12T09:30:15Z\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "본문 형식 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "bad_request", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"요청 본문 형식이 올바르지 않습니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{\n  \"status\": 401,\n  \"success\": false,\n  \"message\": \"유효한 인증 토큰이 필요합니다.\"\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 본문 검증에 실패했습니다.\",\n  \"errors\": [ { \"field\": \"items[0].quantity\", \"reason\": \"MUST_BE_POSITIVE\" }, { \"field\": \"items[0].desiredDeliveryDate\", \"reason\": \"PAST_DATE\" } ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\"\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> createPurchaseRequisition(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"requesterId\": 123,\n  \"items\": [ { \"itemName\": \"A4 복사용지\", \"quantity\": 10, \"uomName\": \"BOX\", \"expectedUnitPrice\": 15000, \"expectedTotalPrice\": 150000, \"preferredVendorName\": \"OO물산\", \"desiredDeliveryDate\": \"2025-10-15\", \"purpose\": \"사무실 비품 보강\", \"note\": \"급히 필요함\" } ]\n}"))
            )
            @RequestBody MmPurchaseRequisitionCreateRequestDto request
    ) {
        // 401 모킹: 특정 요청자 ID
        if (request != null && Long.valueOf(401001L).equals(request.getRequesterId())) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }
        // 500 모킹: ERROR 품목명 포함
        if (request != null && request.getItems() != null && request.getItems().stream().anyMatch(i -> "ERROR".equalsIgnoreCase(i.getItemName()))) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }

        // 422 검증
        List<Map<String, String>> errors = new java.util.ArrayList<>();
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            errors.add(Map.of("field", "items", "reason", "REQUIRED"));
        } else {
            for (int i = 0; i < request.getItems().size(); i++) {
                var it = request.getItems().get(i);
                if (it.getQuantity() == null || it.getQuantity() <= 0) {
                    errors.add(Map.of("field", "items[" + i + "].quantity", "reason", "MUST_BE_POSITIVE"));
                }
                if (it.getDesiredDeliveryDate() != null && !it.getDesiredDeliveryDate().isAfter(java.time.LocalDate.now())) {
                    errors.add(Map.of("field", "items[" + i + "].desiredDeliveryDate", "reason", "PAST_DATE"));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.BODY_VALIDATION_FAILED, errors);
        }

        // 성공 응답
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("prId", 202510120001L);
        data.put("prNumber", "PR-NS-2025-00001");
        data.put("departmentId", 12);
        data.put("departmentName", "경영지원팀");
        data.put("requesterId", request.getRequesterId());
        data.put("requestDate", java.time.LocalDate.now().toString());
        data.put("createdAt", java.time.Instant.now());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "비재고성 자재 구매요청서가 생성되었습니다.", HttpStatus.CREATED));
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매요청서 상세입니다.\",\n  \"data\": {\n    \"id\": 1,\n    \"prNumber\": \"PR-2024-001\",\n    \"requesterId\": 123,\n    \"requesterName\": \"김철수\",\n    \"departmentId\": 77,\n    \"departmentName\": \"생산팀\",\n    \"createdAt\": \"2024-01-15T00:00:00Z\",\n    \"desiredDeliveryDate\": \"2024-01-25\",\n    \"status\": \"APPROVED\",\n    \"currency\": \"KRW\",\n    \"items\": [\n      {\n        \"id\": 900001,\n        \"lineNo\": 1,\n        \"itemId\": 40000123,\n        \"itemName\": \"강판\",\n        \"quantity\": 500,\n        \"uomCode\": \"EA\",\n        \"unitPrice\": 5000,\n        \"amount\": 2500000,\n        \"deliveryDate\": \"2024-01-25\",\n        \"note\": null\n      },\n      {\n        \"id\": 900002,\n        \"lineNo\": 2,\n        \"itemId\": 987654321,\n        \"itemName\": \"볼트\",\n        \"quantity\": 100,\n        \"uomCode\": \"EA\",\n        \"unitPrice\": 500,\n        \"amount\": 50000,\n        \"deliveryDate\": \"2024-01-25\",\n        \"note\": null\n      }\n    ],\n    \"totalAmount\": 2550000\n  }\n}"))
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
        // 1~50만 유효, 그 외는 404 처리
        if (purchaseId == null || purchaseId < 1 || purchaseId > 50) {
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
        // statusLabel 제거: statusCode/status만 제공
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

    @PutMapping("/purchase-requisitions/{prId}")
    @Operation(
            summary = "구매요청서 수정",
            description = "비재고성(NON_STOCK)이며 대기(PENDING) 상태인 구매요청서를 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "수정 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매요청서가 수정되었습니다.\",\n  \"data\": {\n    \"id\": 102345,\n    \"prNumber\": \"PR-NS-2025-00001\",\n    \"prType\": \"NON_STOCK\",\n    \"status\": \"PENDING\",\n    \"departmentId\": 12,\n    \"departmentName\": \"경영지원팀\",\n    \"desiredDeliveryDate\": \"2025-10-20\",\n    \"itemCount\": 2,\n    \"totalExpectedAmount\": 318000,\n    \"items\": [\n      {\n        \"id\": 900001,\n        \"lineNo\": 1,\n        \"itemName\": \"A4 복사용지\",\n        \"quantity\": 12,\n        \"uomName\": \"BOX\",\n        \"expectedUnitPrice\": 14000,\n        \"expectedTotalPrice\": 168000,\n        \"preferredVendorName\": \"OO물산\",\n        \"purpose\": \"사무실 비품 보강\",\n        \"note\": \"수량/단가 재산정\"\n      },\n      {\n        \"id\": 900003,\n        \"lineNo\": 3,\n        \"itemName\": \"화이트보드 마커\",\n        \"quantity\": 50,\n        \"uomName\": \"EA\",\n        \"expectedUnitPrice\": 3000,\n        \"expectedTotalPrice\": 150000,\n        \"preferredVendorName\": \"문구나라\",\n        \"purpose\": \"소모품 보충\",\n        \"note\": \"색상 혼합\"\n      }\n    ]\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "구매요청서를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 구매요청서를 찾을 수 없습니다: purchaseId=999999\",\n  \"errors\": { \"code\": 1012 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "수정 불가 상태",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "conflict", value = "{\n  \"status\": 409,\n  \"success\": false,\n  \"message\": \"현재 상태에서는 수정이 허용되지 않습니다. (required: NON_STOCK & PENDING)\",\n  \"errors\": { \"code\": 1035 }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "본문 검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 본문 검증에 실패했습니다.\",\n  \"errors\": [ { \"field\": \"items[0].op\", \"reason\": \"ALLOWED_VALUES: ADD, UPDATE, REMOVE\" } ]\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> updatePurchaseRequisition(
            @Parameter(description = "구매요청 ID", example = "102345")
            @PathVariable("prId") Long prId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"desiredDeliveryDate\": \"2025-10-20\",\n  \"items\": [\n    { \"op\": \"ADD\", \"lineNo\": 3, \"itemName\": \"화이트보드 마커\", \"quantity\": 50, \"uomName\": \"EA\", \"expectedUnitPrice\": 3000, \"preferredVendorName\": \"문구나라\", \"purpose\": \"소모품 보충\", \"note\": \"색상 혼합\" },\n    { \"op\": \"UPDATE\", \"id\": 900001, \"quantity\": 12, \"expectedUnitPrice\": 14000 }\n  ]\n}"))
            )
            @RequestBody MmPurchaseRequisitionUpdateRequestDto request
    ) {
        if (prId == null || prId < 100000L) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "purchaseId=" + prId);
        }
        if (Long.valueOf(102346L).equals(prId) || Long.valueOf(102348L).equals(prId)) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_UPDATE_CONFLICT);
        }
        if (!Long.valueOf(102345L).equals(prId)) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "purchaseId=" + prId);
        }

        java.time.LocalDate baseDesiredDate = java.time.LocalDate.parse("2025-10-15");
        java.time.LocalDate effectiveDesiredDate = (request != null && request.getDesiredDeliveryDate() != null)
                ? request.getDesiredDeliveryDate()
                : baseDesiredDate;

        List<Map<String, String>> errors = new java.util.ArrayList<>();
        if (request != null && request.getDesiredDeliveryDate() != null && !request.getDesiredDeliveryDate().isAfter(java.time.LocalDate.now())) {
            errors.add(Map.of("field", "desiredDeliveryDate", "reason", "PAST_DATE"));
        }

        java.util.List<Map<String, Object>> items = new java.util.ArrayList<>();
        Map<String, Object> baseItem1 = new java.util.LinkedHashMap<>();
        baseItem1.put("id", 900001L);
        baseItem1.put("lineNo", 1);
        baseItem1.put("itemName", "A4 복사용지");
        baseItem1.put("quantity", 10);
        baseItem1.put("uomName", "BOX");
        baseItem1.put("expectedUnitPrice", 15_000L);
        baseItem1.put("expectedTotalPrice", 150_000L);
        baseItem1.put("preferredVendorName", "OO물산");
        baseItem1.put("purpose", "사무실 비품 보강");
        baseItem1.put("note", "긴급 구매");
        items.add(baseItem1);

        Map<String, Object> baseItem2 = new java.util.LinkedHashMap<>();
        baseItem2.put("id", 900002L);
        baseItem2.put("lineNo", 2);
        baseItem2.put("itemName", "화이트보드 세정제");
        baseItem2.put("quantity", 5);
        baseItem2.put("uomName", "EA");
        baseItem2.put("expectedUnitPrice", 12_000L);
        baseItem2.put("expectedTotalPrice", 60_000L);
        baseItem2.put("preferredVendorName", "청소나라");
        baseItem2.put("purpose", "사무실 유지보수");
        baseItem2.put("note", null);
        items.add(baseItem2);

        java.util.Map<Long, Map<String, Object>> itemIndex = items.stream()
                .collect(java.util.stream.Collectors.toMap(m -> ((Number) m.get("id")).longValue(), m -> m, (a, b) -> a, java.util.LinkedHashMap::new));

        if (request != null && request.getItems() != null) {
            for (int i = 0; i < request.getItems().size(); i++) {
                var incoming = request.getItems().get(i);
                String fieldPrefix = "items[" + i + "]";
                String op = incoming.getOp() == null ? null : incoming.getOp().trim().toUpperCase();
                if (op == null || op.isEmpty()) {
                    errors.add(Map.of("field", fieldPrefix + ".op", "reason", "REQUIRED"));
                    continue;
                }
                if (!java.util.Set.of("ADD", "UPDATE", "REMOVE").contains(op)) {
                    errors.add(Map.of("field", fieldPrefix + ".op", "reason", "ALLOWED_VALUES: ADD, UPDATE, REMOVE"));
                    continue;
                }
                if ("ADD".equals(op) && (incoming.getQuantity() == null || incoming.getQuantity() <= 0)) {
                    errors.add(Map.of("field", fieldPrefix + ".quantity", "reason", "MUST_BE_POSITIVE"));
                }
                if (("UPDATE".equals(op) || "REMOVE".equals(op)) && incoming.getId() == null) {
                    errors.add(Map.of("field", fieldPrefix + ".id", "reason", "REQUIRED"));
                } else if (("UPDATE".equals(op) || "REMOVE".equals(op)) && !itemIndex.containsKey(incoming.getId())) {
                    errors.add(Map.of("field", fieldPrefix + ".id", "reason", "NOT_FOUND"));
                }
                if ("UPDATE".equals(op) && incoming.getQuantity() != null && incoming.getQuantity() <= 0) {
                    errors.add(Map.of("field", fieldPrefix + ".quantity", "reason", "MUST_BE_POSITIVE"));
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.BODY_VALIDATION_FAILED, errors);
        }

        long nextItemId = 900003L;
        if (request != null && request.getItems() != null) {
            for (var incoming : request.getItems()) {
                String op = incoming.getOp() == null ? null : incoming.getOp().trim().toUpperCase();
                if (op == null) {
                    continue;
                }
                switch (op) {
                    case "ADD" -> {
                        Map<String, Object> newItem = new java.util.LinkedHashMap<>();
                        newItem.put("id", nextItemId++);
                        newItem.put("lineNo", incoming.getLineNo() != null ? incoming.getLineNo() : items.size() + 1);
                        newItem.put("itemName", incoming.getItemName());
                        newItem.put("quantity", incoming.getQuantity());
                        newItem.put("uomName", incoming.getUomName());
                        newItem.put("expectedUnitPrice", incoming.getExpectedUnitPrice());
                        Long expectedTotal = (incoming.getQuantity() != null && incoming.getExpectedUnitPrice() != null)
                                ? incoming.getQuantity().longValue() * incoming.getExpectedUnitPrice()
                                : null;
                        newItem.put("expectedTotalPrice", expectedTotal);
                        newItem.put("preferredVendorName", incoming.getPreferredVendorName());
                        newItem.put("purpose", incoming.getPurpose());
                        newItem.put("note", incoming.getNote());
                        items.add(newItem);
                    }
                    case "UPDATE" -> {
                        if (incoming.getId() == null) {
                            break;
                        }
                        Map<String, Object> target = itemIndex.get(incoming.getId());
                        if (target == null) {
                            break;
                        }
                        if (incoming.getQuantity() != null) {
                            target.put("quantity", incoming.getQuantity());
                        }
                        if (incoming.getExpectedUnitPrice() != null) {
                            target.put("expectedUnitPrice", incoming.getExpectedUnitPrice());
                        }
                        if (incoming.getItemName() != null) {
                            target.put("itemName", incoming.getItemName());
                        }
                        if (incoming.getUomName() != null) {
                            target.put("uomName", incoming.getUomName());
                        }
                        if (incoming.getPreferredVendorName() != null) {
                            target.put("preferredVendorName", incoming.getPreferredVendorName());
                        }
                        if (incoming.getPurpose() != null) {
                            target.put("purpose", incoming.getPurpose());
                        }
                        if (incoming.getNote() != null) {
                            target.put("note", incoming.getNote());
                        }
                    }
                    case "REMOVE" -> {
                        if (incoming.getId() == null) {
                            break;
                        }
                        long removeId = incoming.getId();
                        items.removeIf(m -> ((Number) m.get("id")).longValue() == removeId);
                    }
                    default -> {
                    }
                }
                itemIndex = items.stream()
                        .collect(java.util.stream.Collectors.toMap(m -> ((Number) m.get("id")).longValue(), m -> m, (a, b) -> a, java.util.LinkedHashMap::new));
            }
        }

        items.forEach(it -> {
            Integer quantity = (Integer) it.get("quantity");
            Long unitPrice = (Long) it.get("expectedUnitPrice");
            if (quantity != null && unitPrice != null) {
                it.put("expectedTotalPrice", quantity.longValue() * unitPrice);
            }
        });

        items.sort(java.util.Comparator.comparing(m -> {
            Object ln = m.get("lineNo");
            return (ln instanceof Integer) ? (Integer) ln : Integer.MAX_VALUE;
        }));

        long totalExpectedAmount = items.stream()
                .map(m -> (Long) m.get("expectedTotalPrice"))
                .filter(java.util.Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", prId);
        data.put("prNumber", "PR-NS-2025-00001");
        data.put("prType", "NON_STOCK");
        data.put("status", "PENDING");
        data.put("departmentId", 12L);
        data.put("departmentName", "경영지원팀");
        data.put("desiredDeliveryDate", effectiveDesiredDate);
        data.put("itemCount", items.size());
        data.put("totalExpectedAmount", totalExpectedAmount);
        data.put("items", items);

        return ResponseEntity.ok(ApiResponse.success(data, "구매요청서가 수정되었습니다.", HttpStatus.OK));
    }

    @PostMapping("/purchase-requisitions/{prId}/release")
    @Operation(
            summary = "구매요청서 승인",
            description = "구매요청서를 승인(Release) 처리합니다. 승인 가능한 역할 토큰이 필요합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "승인 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매요청서가 승인되었습니다.\",\n  \"data\": {\n    \"id\": 102345,\n    \"prNumber\": \"PR-NS-2025-00001\",\n    \"prType\": \"NON_STOCK\",\n    \"status\": \"APPROVED\",\n    \"origin\": \"MRP\",\n    \"originRefId\": \"MRP-2025-10-01-00123\",\n    \"requesterId\": 123,\n    \"requesterName\": \"홍길동\",\n    \"departmentId\": 12,\n    \"departmentName\": \"영업1팀\",\n    \"approvedAt\": \"2025-10-07T09:15:00Z\",\n    \"approvedBy\": 777,\n    \"approvedByName\": \"김관리자\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "승인 권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"승인 권한이 없습니다. (required role: PR_APPROVER|PURCHASING_MANAGER|ADMIN)\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "구매요청서 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 구매요청서를 찾을 수 없습니다: prId=999999\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "승인 불가 상태",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "invalid_transition", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"해당 상태에서는 승인할 수 없습니다.\",\n  \"errors\": [ { \"field\": \"status\", \"reason\": \"INVALID_TRANSITION: DRAFT/REJECTED/VOID/APPROVED → APPROVED 불가\" } ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "처리 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"요청 처리 중 오류가 발생했습니다.\" }"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> releasePurchaseRequisition(
            @Parameter(description = "구매요청 ID", example = "102345")
            @PathVariable("prId") Long prId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        String upperToken = authorization.trim().toUpperCase(Locale.ROOT);
        if (upperToken.contains("ERROR")) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_APPROVAL_PROCESSING_ERROR);
        }

        Set<String> approverRoles = Set.of("PR_APPROVER", "PURCHASING_MANAGER", "ADMIN");
        boolean hasPrivilege = approverRoles.stream().anyMatch(upperToken::contains);
        if (!hasPrivilege) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_APPROVAL_FORBIDDEN);
        }

        if (prId == null || prId < 100000L) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "prId=" + prId);
        }
        if (Long.valueOf(102347L).equals(prId)) {
            List<Map<String, String>> errors = List.of(Map.of(
                    "field", "status",
                    "reason", "INVALID_TRANSITION: DRAFT/REJECTED/VOID/APPROVED → APPROVED 불가"
            ));
            throw new ValidationException(ErrorCode.PURCHASE_REQUEST_INVALID_TRANSITION, errors);
        }
        if (Long.valueOf(102399L).equals(prId)) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_APPROVAL_PROCESSING_ERROR);
        }
        if (!Long.valueOf(102345L).equals(prId)) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "prId=" + prId);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", prId);
        data.put("prNumber", "PR-NS-2025-00001");
        data.put("prType", "NON_STOCK");
        data.put("status", "APPROVED");
        data.put("origin", "MRP");
        data.put("originRefId", "MRP-2025-10-01-00123");
        data.put("requesterId", 123L);
        data.put("requesterName", "홍길동");
        data.put("departmentId", 12L);
        data.put("departmentName", "영업1팀");
        data.put("approvedAt", java.time.Instant.parse("2025-10-07T09:15:00Z"));
        data.put("approvedBy", 777L);
        data.put("approvedByName", "김관리자");

        return ResponseEntity.ok(ApiResponse.success(data, "구매요청서가 승인되었습니다.", HttpStatus.OK));
    }

    @PostMapping("/purchase-requisitions/{prId}/reject")
    @Operation(
            summary = "구매요청서 반려",
            description = "구매요청서를 반려 처리하고 사유를 기록합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "반려 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매요청서가 반려되었습니다.\",\n  \"data\": {\n    \"id\": 102345,\n    \"prNumber\": \"PR-NS-2025-00001\",\n    \"status\": \"REJECTED\",\n    \"origin\": \"MRP\",\n    \"originRefId\": \"MRP-2025-10-01-00123\",\n    \"requesterId\": 123,\n    \"requesterName\": \"홍길동\",\n    \"departmentId\": 12,\n    \"departmentName\": \"영업1팀\",\n    \"rejectedAt\": \"2025-10-07T10:30:00Z\",\n    \"rejectedBy\": 777,\n    \"rejectedByName\": \"김관리자\",\n    \"rejectReason\": \"예산 초과로 반려합니다.\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "반려 권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"해당 문서를 반려할 권한이 없습니다. (required role: PR_APPROVER|PURCHASING_MANAGER|ADMIN)\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "구매요청서 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 구매요청서를 찾을 수 없습니다: prId=999999\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "반려 불가 상태 또는 본문 검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "invalid_transition", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"해당 상태에서는 반려할 수 없습니다.\",\n  \"errors\": [ { \"field\": \"status\", \"reason\": \"INVALID_TRANSITION: DRAFT/APPROVED/REJECTED/VOID → REJECTED 불가\" } ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "처리 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"요청 처리 중 오류가 발생했습니다.\" }"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> rejectPurchaseRequisition(
            @Parameter(description = "구매요청 ID", example = "102345")
            @PathVariable("prId") Long prId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"comment\": \"예산 초과로 반려합니다.\" }"))
            )
            @RequestBody MmPurchaseRequisitionRejectRequestDto request
    ) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        String upperToken = authorization.trim().toUpperCase(Locale.ROOT);
        if (upperToken.contains("ERROR")) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_REJECTION_PROCESSING_ERROR);
        }

        Set<String> approverRoles = Set.of("PR_APPROVER", "PURCHASING_MANAGER", "ADMIN");
        boolean hasPrivilege = approverRoles.stream().anyMatch(upperToken::contains);
        if (!hasPrivilege) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_REJECTION_FORBIDDEN);
        }

        List<Map<String, String>> errors = new java.util.ArrayList<>();
        if (request == null || request.getComment() == null || request.getComment().isBlank()) {
            errors.add(Map.of("field", "comment", "reason", "REQUIRED"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.BODY_VALIDATION_FAILED, errors);
        }

        if (prId == null || prId < 100000L) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "prId=" + prId);
        }
        if (Long.valueOf(102347L).equals(prId)) {
            List<Map<String, String>> transitionErrors = List.of(Map.of(
                    "field", "status",
                    "reason", "INVALID_TRANSITION: DRAFT/APPROVED/REJECTED/VOID → REJECTED 불가"
            ));
            throw new ValidationException(ErrorCode.PURCHASE_REQUEST_REJECTION_INVALID_TRANSITION, transitionErrors);
        }
        if (Long.valueOf(102399L).equals(prId)) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_REJECTION_PROCESSING_ERROR);
        }
        if (!Long.valueOf(102345L).equals(prId)) {
            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "prId=" + prId);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", prId);
        data.put("prNumber", "PR-NS-2025-00001");
        data.put("status", "REJECTED");
        data.put("origin", "MRP");
        data.put("originRefId", "MRP-2025-10-01-00123");
        data.put("requesterId", 123L);
        data.put("requesterName", "홍길동");
        data.put("departmentId", 12L);
        data.put("departmentName", "영업1팀");
        data.put("rejectedAt", java.time.Instant.parse("2025-10-07T10:30:00Z"));
        data.put("rejectedBy", 777L);
        data.put("rejectedByName", "김관리자");
        data.put("rejectReason", request.getComment());

        return ResponseEntity.ok(ApiResponse.success(data, "구매요청서가 반려되었습니다.", HttpStatus.OK));
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"발주서 목록 조회에 성공했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"id\": 1001,\n        \"poNumber\": \"PO-2024-001\",\n        \"supplierName\": \"대한철강\",\n        \"itemsSummary\": \"강판 500kg, 알루미늄 300kg\",\n        \"totalAmount\": 5000000,\n        \"orderDate\": \"2024-01-18\",\n        \"deliveryDate\": \"2024-01-25\",\n        \"status\": \"APPROVED\"\n      }\n    ],\n    \"page\": { \n      \"number\": 0, \n      \"size\": 10, \n      \"totalElements\": 50, \n      \"totalPages\": 5, \n      \"hasNext\": true \n    }\n  }\n}"))
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
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [ { \"field\": \"status\", \"reason\": \"ALLOWED_VALUES: APPROVED, PENDING, REJECTED, DELIVERED, ALL\" } ]\n}"))
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
            @Parameter(description = "상태 필터: PENDING, APPROVED, REJECTED, DELIVERED, ALL(전체)")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "공급업체명 검색")
            @RequestParam(name = "supplierName", required = false) String supplierName,
            @Parameter(description = "발주서 번호 검색")
            @RequestParam(name = "poNumber", required = false) String poNumber,
            @Parameter(description = "주문일 시작(YYYY-MM-DD)")
            @RequestParam(name = "orderDateFrom", required = false) String orderDateFrom,
            @Parameter(description = "주문일 종료(YYYY-MM-DD)")
            @RequestParam(name = "orderDateTo", required = false) String orderDateTo,
            @Parameter(description = "정렬 필드,정렬방향(orderDate|deliveryDate)")
            @RequestParam(name = "sort", required = false) String sort,
            @Parameter(description = "페이지 번호(0-base)")
            @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기(최대 200)")
            @RequestParam(name = "size", required = false) Integer size
    ) {
        List<Map<String, String>> errors = new java.util.ArrayList<>();
        java.time.LocalDate from = null;
        java.time.LocalDate to = null;

        if (status != null) {
            String st = status.toUpperCase(Locale.ROOT);
            if (!"ALL".equals(st)) {
                var allowed = java.util.Set.of("APPROVED", "PENDING", "REJECTED", "DELIVERED");
                if (!allowed.contains(st)) {
                    errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: APPROVED, PENDING, REJECTED, DELIVERED, ALL"));
                }
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
        if (from != null && to != null && from.isAfter(to)) {
            errors.add(Map.of("field", "orderDate", "reason", "FROM_AFTER_TO"));
        }
        if (size != null && size > 200) {
            errors.add(Map.of("field", "size", "reason", "MAX_200"));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
        }

        String effectiveSort = (sort == null || sort.isBlank()) ? "orderDate,desc" : sort;
        String[] sortParts = effectiveSort.split(",");
        String sortField = sortParts[0].trim();
        String sortDirection = sortParts.length > 1 ? sortParts[1].trim().toLowerCase(Locale.ROOT) : "desc";
        if (!java.util.Set.of("orderDate", "deliveryDate").contains(sortField)) {
            sortField = "orderDate";
        }
        if (!sortDirection.equals("asc") && !sortDirection.equals("desc")) {
            sortDirection = "desc";
        }

        int pageIndex = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;

        if (from != null && from.isBefore(java.time.LocalDate.of(2024, 1, 1))) {
            throw new BusinessException(ErrorCode.FORBIDDEN_DATA_ACCESS);
        }
        if (effectiveSort.toLowerCase(Locale.ROOT).contains("error") || effectiveSort.contains("500")) {
            throw new BusinessException(ErrorCode.UNKNOWN_PROCESSING_ERROR);
        }

        java.util.List<Map<String, Object>> all = new java.util.ArrayList<>();
        String[] suppliers = {"대한철강","한국알루미늄","포스코","효성중공업","현대제철","두산중공업","세아베스틸","KG동부제철","동국제강","티엠씨메탈"};
        String[] itemsSummary = {"강판 500kg, 알루미늄 300kg","알루미늄 시트 200매","고강도 스틸 1톤","볼트 1000개","스테인리스 파이프 200개","알루미늄 판재 100매","스틸 코일 3톤","강철 빔 50개","철판 2톤","알루미늄 봉 100개"};
        String[] statusList = {"APPROVED","PENDING","REJECTED","APPROVED","PENDING","DELIVERED","REJECTED","PENDING","APPROVED","DELIVERED"};
        java.time.LocalDate baseDate = java.time.LocalDate.of(2024,1,18);
        for (int i = 0; i < 50; i++) {
            int idx = i % 10;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", 1001 + i);
            row.put("poNumber", String.format("PO-2024-%03d", i + 1));
            row.put("supplierName", suppliers[idx]);
            row.put("itemsSummary", itemsSummary[idx]);
            java.time.LocalDate od = baseDate.minusDays(idx);
            row.put("orderDate", od.toString());
            row.put("deliveryDate", od.plusDays(7).toString());
            row.put("totalAmount", 5_000_000 - (i * 120_000));
            row.put("status", statusList[idx]);
            all.add(row);
        }

        java.util.List<Map<String, Object>> filtered = all;
        if (status != null) {
            String st = status.toUpperCase(Locale.ROOT);
            if (!"ALL".equals(st)) {
                filtered = filtered.stream().filter(m -> st.equals(m.get("status"))).toList();
            }
        }
        if (supplierName != null && !supplierName.isBlank()) {
            String keyword = supplierName.toLowerCase(Locale.ROOT);
            filtered = filtered.stream()
                    .filter(m -> String.valueOf(m.get("supplierName")).toLowerCase(Locale.ROOT).contains(keyword))
                    .toList();
        }
        if (poNumber != null && !poNumber.isBlank()) {
            String keyword = poNumber.toLowerCase(Locale.ROOT);
            filtered = filtered.stream()
                    .filter(m -> String.valueOf(m.get("poNumber")).toLowerCase(Locale.ROOT).contains(keyword))
                    .toList();
        }
        if (from != null) {
            java.time.LocalDate fromDate = from;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(String.valueOf(m.get("orderDate"))).isBefore(fromDate))
                    .toList();
        }
        if (to != null) {
            java.time.LocalDate toDate = to;
            filtered = filtered.stream()
                    .filter(m -> !java.time.LocalDate.parse(String.valueOf(m.get("orderDate"))).isAfter(toDate))
                    .toList();
        }

        java.util.Comparator<Map<String, Object>> comparator;
        if (sortField.equals("deliveryDate")) {
            comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(String.valueOf(m.get("deliveryDate"))));
        } else {
            comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(String.valueOf(m.get("orderDate"))));
        }
        if (sortDirection.equals("desc")) {
            comparator = comparator.reversed();
        }
        filtered = filtered.stream().sorted(comparator).toList();

        int total = filtered.size();
        int fromIdx = Math.min(pageIndex * pageSize, total);
        int toIdx = Math.min(fromIdx + pageSize, total);
        java.util.List<Map<String, Object>> pageContent = filtered.subList(fromIdx, toIdx);

        org.ever._4ever_be_gw.common.dto.PageDto pageDto = org.ever._4ever_be_gw.common.dto.PageDto.builder()
                .number(pageIndex)
                .size(pageSize)
                .totalElements(total)
                .totalPages(pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize))
                .hasNext(pageIndex + 1 < (pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize)))
                .build();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", pageContent);
        data.put("page", pageDto);

        return ResponseEntity.ok(ApiResponse.success(data, "발주서 목록 조회에 성공했습니다.", HttpStatus.OK));
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"발주서 상세 정보 조회에 성공했습니다.\",\n  \"data\": {\n    \"id\": 1,\n    \"poNumber\": \"PO-2024-001\",\n    \"supplierName\": \"대한철강\",\n    \"supplierContact\": \"02-1234-5678\",\n    \"supplierEmail\": \"order@steel.co.kr\",\n    \"orderDate\": \"2024-01-18\",\n    \"deliveryDate\": \"2024-01-25\",\n    \"status\": \"승인됨\",\n    \"totalAmount\": 5000000,\n    \"items\": [\n      { \"itemName\": \"강판\", \"spec\": \"SS400 10mm\", \"quantity\": 500, \"unit\": \"kg\", \"unitPrice\": 8000, \"amount\": 4000000 },\n      { \"itemName\": \"알루미늄\", \"spec\": \"A6061 5mm\", \"quantity\": 300, \"unit\": \"kg\", \"unitPrice\": 3333, \"amount\": 1000000 }\n    ],\n    \"deliveryAddress\": \"경기도 안산시 단원구 공장로 456\",\n    \"requestedDeliveryDate\": \"2024-01-25\",\n    \"specialInstructions\": \"오전 배송 요청\",\n    \"paymentTerms\": \"월말 결제\",\n    \"note\": \"1월 생산용 원자재 주문\"\n  }\n}"))
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
        if (purchaseId == null) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + null);
        }
        boolean poIdRange = purchaseId >= 1001L && purchaseId <= 1050L;
        if (!poIdRange) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + purchaseId);
        }

        // 목업 데이터 생성 (0~49 인덱스, 표시용 데이터는 10개 템플릿을 순환)
        int idxFull = (int)(purchaseId - 1001);
        int idx = idxFull % 10;
        String[] suppliers = {"대한철강","한국알루미늄","포스코","효성중공업","현대제철","두산중공업","세아베스틸","KG동부제철","동국제강","티엠씨메탈"};
        long[] supplierIds = {501,502,503,504,505,506,507,508,509,510};
        String[] supplierCodes = {"SUP001","SUP002","SUP003","SUP004","SUP005","SUP006","SUP007","SUP008","SUP009","SUP010"};
        String[] orderDates = {"2024-01-18","2024-01-17","2024-01-16","2024-01-15","2024-01-14","2024-01-13","2024-01-12","2024-01-11","2024-01-10","2024-01-09"};
        String[] deliveryDates = {"2024-01-25","2024-01-24","2024-01-23","2024-01-22","2024-01-21","2024-01-20","2024-01-19","2024-01-18","2024-01-17","2024-01-16"};
        String[] statusCodes = {"APPROVED","PENDING","REJECTED","APPROVED","PENDING","APPROVED","REJECTED","PENDING","APPROVED","PENDING"};

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", purchaseId);
        data.put("poNumber", String.format("PO-2024-%03d", 1 + idxFull));
        data.put("supplierId", supplierIds[idx]);
        data.put("supplierCode", supplierCodes[idx]);
        data.put("supplierName", suppliers[idx]);
        data.put("supplierContact", "02-1234-5678");
        data.put("supplierEmail", "order@steel.co.kr");
        data.put("orderDate", orderDates[idx]);
        data.put("requestedDeliveryDate", deliveryDates[idx]);
        data.put("statusCode", statusCodes[idx]);

        // 품목
        java.util.List<Map<String, Object>> items = new java.util.ArrayList<>();
        Map<String, Object> item1 = new LinkedHashMap<>();
        item1.put("itemName", "강판");
        item1.put("quantity", 500);
        item1.put("unit", "kg");
        item1.put("unitPrice", 8000);
        item1.put("amount", 4_000_000);
        items.add(item1);

        Map<String, Object> item2 = new LinkedHashMap<>();
        item2.put("itemName", "알루미늄");
        item2.put("quantity", 300);
        item2.put("unit", "kg");
        item2.put("unitPrice", 3333);
        item2.put("amount", 1_000_000);
        items.add(item2);

        data.put("items", items);
        data.put("totalAmount", 5_000_000);
        data.put("note", "1월 생산용 원자재 주문");

        // 생성/수정 일시 (orderDate 기준 09:00Z와 +5분)
        java.time.LocalDate od = java.time.LocalDate.parse(orderDates[idx]);
        java.time.Instant createdAt = od.atTime(9,0).atZone(java.time.ZoneOffset.UTC).toInstant();
        java.time.Instant updatedAt = createdAt.plusSeconds(300);
        data.put("createdAt", createdAt);
        data.put("updatedAt", updatedAt);

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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"vendorId\": 1,\n        \"vendorCode\": \"SUP001\",\n        \"companyName\": \"한국철강\",\n        \"contactPhone\": \"02-1234-5678\",\n        \"contactEmail\": \"contact@koreasteel.com\",\n        \"category\": \"원자재\",\n        \"leadTimeDays\": 3,\n        \"leadTimeLabel\": \"3일\",\n        \"statusCode\": \"ACTIVE\",\n        \"actions\": [\"view\"],\n        \"createdAt\": \"2025-10-07T00:00:00Z\",\n        \"updatedAt\": \"2025-10-07T00:00:00Z\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 50,\n      \"totalPages\": 5,\n      \"hasNext\": true\n    }\n  }\n}"))
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
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"status\", \"reason\": \"ALLOWED_VALUES: ACTIVE, INACTIVE, ALL\" },\n    { \"field\": \"page\", \"reason\": \"MIN_0\" },\n    { \"field\": \"size\", \"reason\": \"MAX_200\" }\n  ]\n}"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> getVendors(
            @Parameter(description = "상태 필터: ACTIVE, INACTIVE, ALL(전체)")
            @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "카테고리 필터", example = "부품")
            @RequestParam(name = "category", required = false) String category,
            @Parameter(description = "페이지 번호(0-base)", example = "0")
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기(최대 200)", example = "10")
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        // 검증
        List<Map<String, String>> errors = new java.util.ArrayList<>();
        if (status != null) {
            String st = status.toUpperCase(Locale.ROOT);
            if (!"ALL".equals(st)) {
                var allowed = java.util.Set.of("ACTIVE", "INACTIVE");
                if (!allowed.contains(st)) {
                    errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: ACTIVE, INACTIVE, ALL"));
                }
            }
        }
        if (page != null && page < 0) {
            errors.add(Map.of("field", "page", "reason", "MIN_0"));
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

        java.util.List<Map<String, Object>> allVendors = new java.util.ArrayList<>();
        String[] codes = {"SUP001","SUP002","SUP003","SUP004","SUP005","SUP006","SUP007","SUP008","SUP009","SUP010"};
        String[] names = {"한국철강","대한전자부품","글로벌화학","한빛소재","스마트로지스틱스","태성테크","광명산업","한성전자","그린케미칼","아주금속"};
        String[] categories = {"원자재","전자부품","원자재","부품","기타","전자부품","원자재","부품","원자재","원자재"};
        int[] leadDays = {3,1,5,2,0,7,6,2,9,10};
        String[] phones = {"02-1234-5678","031-987-6543","051-555-0123","02-3456-7890","02-9999-1111","02-7777-8888","031-3333-4444","02-2222-1111","051-777-0000","032-101-2020"};
        String[] emails = {"contact@koreasteel.com","sales@dahanelec.com","info@globalchem.co.kr","info@hanbits.com","service@smartlogistics.kr","sales@taesung.com","contact@kwangmyung.co.kr","info@hanseong.com","sales@greenchem.co.kr","contact@ajumetal.co.kr"};
        String[] statusCodeArr = {"ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE"};
        for (int i = 0; i < 50; i++) {
            int idx = i % 10;
            Map<String, Object> v = new LinkedHashMap<>();
            v.put("vendorId", 1 + i);
            v.put("vendorCode", codes[idx]);
            v.put("companyName", names[idx]);
            v.put("contactPhone", phones[idx]);
            v.put("contactEmail", emails[idx]);
            v.put("category", categories[idx]);
            v.put("leadTimeDays", leadDays[idx]);
            v.put("leadTimeLabel", leadDays[idx] == 0 ? "당일 배송" : leadDays[idx] + "일");
            v.put("statusCode", statusCodeArr[idx]);
            v.put("actions", java.util.List.of("view"));
            v.put("createdAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));
            v.put("updatedAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));
            allVendors.add(v);
        }
        // 상태/카테고리 필터 적용
        java.util.List<Map<String, Object>> filtered = allVendors;
        if (status != null) {
            final String expectedStatus = status.toUpperCase(Locale.ROOT);
            if (!"ALL".equals(expectedStatus)) {
                filtered = filtered.stream()
                        .filter(v -> expectedStatus.equals(String.valueOf(v.get("statusCode"))))
                        .toList();
            }
        }
        if (category != null && !category.isBlank()) {
            final String expectedCategory = category.trim();
            filtered = filtered.stream()
                    .filter(v -> expectedCategory.equals(v.get("category")))
                    .toList();
        }

        int total = filtered.size();
        int pageIndex = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 10 : size;
        int fromIdx = Math.min(pageIndex * s, total);
        int toIdx = Math.min(fromIdx + s, total);
        java.util.List<Map<String, Object>> content = filtered.subList(fromIdx, toIdx);

        org.ever._4ever_be_gw.common.dto.PageDto pageDto = org.ever._4ever_be_gw.common.dto.PageDto.builder()
                .number(pageIndex)
                .size(s)
                .totalElements(total)
                .totalPages(s == 0 ? 0 : (int) Math.ceil((double) total / s))
                .hasNext(pageIndex + 1 < (s == 0 ? 0 : (int) Math.ceil((double) total / s)))
                .build();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", content);
        data.put("page", pageDto);

        return ResponseEntity.ok(ApiResponse.<Object>success(
                data, "공급업체 목록을 조회했습니다.", HttpStatus.OK
        ));
    }

    @PostMapping("/vendors")
    @Operation(
            summary = "공급업체 등록",
            description = "신규 공급업체를 등록하고 ERP 내부용 ID/코드를 발급하며 등록된 이메일로 임시 로그인 정보를 발송합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "등록 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체가 정상적으로 등록되었습니다.\",\n  \"data\": {\n    \"vendorId\": 101,\n    \"vendorCode\": \"SUP-2025-0001\",\n    \"companyName\": \"대한철강\",\n    \"contactPerson\": \"홍길동\",\n    \"email\": \"contact@koreasteel.com\",\n    \"createdAt\": \"2025-10-13T10:00:00Z\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"공급업체 등록 권한이 없습니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"companyName\", \"reason\": \"필수 입력값입니다.\" },\n    { \"field\": \"email\", \"reason\": \"올바른 이메일 형식이 아닙니다.\" }\n  ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"공급업체 등록 처리 중 오류가 발생했습니다.\" }"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> createVendor(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n  \"companyName\": \"대한철강\",\n  \"category\": \"원자재\",\n  \"contactPerson\": \"홍길동\",\n  \"contactPhone\": \"02-1234-5678\",\n  \"email\": \"contact@koreasteel.com\",\n  \"deliveryLeadTime\": 3,\n  \"address\": \"서울시 강남구 테헤란로 123\",\n  \"materialList\": [\n    {\n      \"materialName\": \"철강재\",\n      \"unit\": \"kg\",\n      \"unitPrice\": 1500\n    },\n    {\n      \"materialName\": \"스테인리스\",\n      \"unit\": \"kg\",\n      \"unitPrice\": 2500\n    },\n    {\n      \"materialName\": \"알루미늄\",\n      \"unit\": \"kg\",\n      \"unitPrice\": 2200\n    }\n  ]\n}"))
            )
            @RequestBody MmVendorCreateRequestDto request
    ) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        String token = authorization.trim().toUpperCase(Locale.ROOT);
        if (!token.contains("PR_APPROVER") && !token.contains("PURCHASING_MANAGER") && !token.contains("ADMIN")) {
            throw new BusinessException(ErrorCode.VENDOR_CREATE_FORBIDDEN);
        }
        if (token.contains("ERROR")) {
            throw new BusinessException(ErrorCode.VENDOR_CREATE_PROCESSING_ERROR);
        }

        List<Map<String, String>> errors = new java.util.ArrayList<>();
        if (request == null || request.getCompanyName() == null || request.getCompanyName().isBlank()) {
            errors.add(Map.of("field", "companyName", "reason", "필수 입력값입니다."));
        }
        if (request == null || request.getEmail() == null || request.getEmail().isBlank() || !request.getEmail().contains("@")) {
            errors.add(Map.of("field", "email", "reason", "올바른 이메일 형식이 아닙니다."));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VENDOR_CREATE_VALIDATION_FAILED, errors);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("vendorId", 101L);
        data.put("vendorCode", "SUP-2025-0001");
        data.put("companyName", request.getCompanyName());
        data.put("contactPerson", request.getContactPerson());
        data.put("email", request.getEmail());
        data.put("createdAt", java.time.Instant.parse("2025-10-13T10:00:00Z"));

        return ResponseEntity.ok(ApiResponse.success(data, "공급업체가 정상적으로 등록되었습니다.", HttpStatus.OK));
    }

    @PostMapping("/purchase-orders/{poId}/approve")
    @Operation(
            summary = "발주서 승인",
            description = "발주서의 상태를 대기 → 승인으로 변경합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "승인 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"발주서를 승인했습니다.\",\n  \"data\": {\n    \"id\": 1001,\n    \"poNumber\": \"PO-2024-001\",\n    \"statusCode\": \"APPROVED\",\n    \"approvedAt\": \"2025-10-07T09:15:00Z\",\n    \"approvedBy\": \"홍길동\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "승인 권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"발주서 승인 권한이 없습니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "발주서 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 발주서를 찾을 수 없습니다: poId=1001\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "승인 불가 상태",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "conflict", value = "{ \"status\": 409, \"success\": false, \"message\": \"현재 상태에서는 승인할 수 없습니다. (status=PENDING만 승인 가능)\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "처리 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"승인 처리 중 오류가 발생했습니다.\" }"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> approvePurchaseOrder(
            @Parameter(description = "발주서 ID", example = "1001")
            @PathVariable("poId") Long poId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        String token = authorization.trim().toUpperCase(Locale.ROOT);
        if (token.contains("ERROR")) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_APPROVAL_PROCESSING_ERROR);
        }

        // 승인 권한 체크: PO_APPROVER, PURCHASING_MANAGER, ADMIN
        boolean hasPrivilege = token.contains("PO_APPROVER") || token.contains("PURCHASING_MANAGER") || token.contains("ADMIN");
        if (!hasPrivilege) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_APPROVAL_FORBIDDEN);
        }

        // 유효한 발주서 ID 범위 (목업 데이터 기준: 1001~1050)
        if (poId == null || poId < 1001L || poId > 1050L) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + poId);
        }

        // 현재 상태 확인: PENDING만 승인 가능
        // 규칙: (poId-1001) % 5 in {1,4} → PENDING (목록 생성 규칙과 일치)
        int mod = (int)((poId - 1001) % 5);
        boolean isPending = (mod == 1 || mod == 4);
        if (!isPending) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_INVALID_TRANSITION);
        }

        int idxFull = (int)(poId - 1001);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", poId);
        data.put("poNumber", String.format("PO-2024-%03d", idxFull + 1));
        data.put("statusCode", "APPROVED");
        // statusLabel 제거
        data.put("approvedAt", java.time.Instant.parse("2025-10-07T09:15:00Z"));
        data.put("approvedBy", "홍길동");

        return ResponseEntity.ok(ApiResponse.success(data, "발주서를 승인했습니다.", HttpStatus.OK));
    }

    @PostMapping("/purchase-orders/{poId}/reject")
    @Operation(
            summary = "발주서 반려",
            description = "발주서의 상태를 반려로 변경합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "반려 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"발주서를 반려했습니다.\",\n  \"data\": {\n    \"id\": 1001,\n    \"poNumber\": \"PO-2024-001\",\n    \"statusCode\": \"REJECTED\",\n    \"rejectedAt\": \"2025-10-14T10:00:00Z\",\n    \"rejectedBy\": \"홍길동\",\n    \"reason\": \"납기일 미확정\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "반려 권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"발주서 반려 권한이 없습니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "발주서 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 발주서를 찾을 수 없습니다: poId=999999\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "본문 검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "reason_required", value = "{ \"status\": 422, \"success\": false, \"message\": \"반려 사유를 입력해야 합니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "처리 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"발주서 반려 처리 중 오류가 발생했습니다.\" }"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> rejectPurchaseOrder(
            @Parameter(description = "발주서 ID", example = "1001")
            @PathVariable("poId") Long poId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{ \"reason\": \"납기일 미확정\" }"))
            )
            @RequestBody org.ever._4ever_be_gw.scmpp.dto.MmPurchaseOrderRejectRequestDto request
    ) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        String token = authorization.trim().toUpperCase(Locale.ROOT);
        if (token.contains("ERROR")) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_REJECTION_PROCESSING_ERROR);
        }

        // 반려 권한 체크: PO_APPROVER, PURCHASING_MANAGER, ADMIN
        boolean hasPrivilege = token.contains("PO_APPROVER") || token.contains("PURCHASING_MANAGER") || token.contains("ADMIN");
        if (!hasPrivilege) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_REJECTION_FORBIDDEN);
        }

        if (poId == null || poId < 1001L || poId > 1050L) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + poId);
        }

        if (request == null || request.getReason() == null || request.getReason().isBlank()) {
            throw new BusinessException(ErrorCode.PURCHASE_ORDER_REJECTION_REASON_REQUIRED);
        }

        int idxFull = (int)(poId - 1001);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", poId);
        data.put("poNumber", String.format("PO-2024-%03d", idxFull + 1));
        data.put("statusCode", "REJECTED");
        // statusLabel 제거
        data.put("rejectedAt", java.time.Instant.parse("2025-10-14T10:00:00Z"));
        data.put("rejectedBy", "홍길동");
        data.put("reason", request.getReason());

        return ResponseEntity.ok(ApiResponse.success(data, "발주서를 반려했습니다.", HttpStatus.OK));
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
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"vendorId\": 1,\n    \"companyName\": \"한국철강\",\n    \"contactPhone\": \"02-1234-5678\",\n    \"contactEmail\": \"contact@koreasteel.com\",\n    \"category\": \"원자재\",\n    \"leadTimeDays\": 3,\n    \"leadTimeLabel\": \"3일 소요\",\n    \"statusCode\": \"ACTIVE\",\n    \"materials\": [\"철강재\", \"스테인리스\", \"알루미늄\"],\n    \"createdAt\": \"2025-10-07T00:00:00Z\",\n    \"updatedAt\": \"2025-10-07T00:00:00Z\"\n  }\n}"))
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
        if (vendorId == null || vendorId < 1 || vendorId > 50) {
            throw new BusinessException(ErrorCode.VENDOR_NOT_FOUND);
        }

        int idx = (int)((vendorId - 1) % 10);
        String[] codes = {"SUP001","SUP002","SUP003","SUP004","SUP005","SUP006","SUP007","SUP008","SUP009","SUP010"};
        String[] names = {"한국철강","대한전자부품","글로벌화학","한빛소재","스마트로지스틱스","태성테크","광명산업","한성전자","그린케미칼","아주금속"};
        String[] categories = {"원자재","전자부품","원자재","부품","기타","전자부품","원자재","부품","원자재","원자재"};
        int[] leadDays = {3,1,5,2,0,7,6,2,9,10};
        String[] phones = {"02-1234-5678","031-987-6543","051-555-0123","02-3456-7890","02-9999-1111","02-7777-8888","031-3333-4444","02-2222-1111","051-777-0000","032-101-2020"};
        String[] emails = {"contact@koreasteel.com","sales@dahanelec.com","info@globalchem.co.kr","info@hanbits.com","service@smartlogistics.kr","sales@taesung.com","contact@kwangmyung.co.kr","info@hanseong.com","sales@greenchem.co.kr","contact@ajumetal.co.kr"};
        String[] statusCode = {"ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE"};
        java.util.List<java.util.List<String>> materialsByVendor = java.util.List.of(
                java.util.List.of("철강재", "스테인리스", "알루미늄"),
                java.util.List.of("커넥터", "PCB", "센서"),
                java.util.List.of("유기용제", "촉매", "첨가제"),
                java.util.List.of("알루미늄 플레이트", "구조용 볼트"),
                java.util.List.of("물류 지원", "패킹"),
                java.util.List.of("전자모듈", "케이블"),
                java.util.List.of("강판", "합금"),
                java.util.List.of("반도체 부품", "커넥터"),
                java.util.List.of("산업용 화학", "첨가제"),
                java.util.List.of("철강 코일", "합금 파이프")
        );

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("vendorId", vendorId);
        data.put("vendorCode", codes[idx]);
        data.put("companyName", names[idx]);
        data.put("contactPhone", phones[idx]);
        data.put("contactEmail", emails[idx]);
        data.put("category", categories[idx]);
        data.put("leadTimeDays", leadDays[idx]);
        data.put("leadTimeLabel", leadDays[idx] == 0 ? "당일 배송" : leadDays[idx] + "일 소요");
        data.put("statusCode", statusCode[idx]);
        data.put("materials", materialsByVendor.get(idx));
        // 간단한 시계열 생성
        data.put("createdAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));
        data.put("updatedAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));

        return ResponseEntity.ok(ApiResponse.<Object>success(
                data, "공급업체 상세 정보를 조회했습니다.", HttpStatus.OK
        ));
    }

    @PostMapping("/vendors/{vendorId}/account")
    @Operation(
            summary = "공급업체 계정 생성",
            description = "공급업체 계정을 생성하고 임시 비밀번호가 포함된 초대 이메일을 발송합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 계정이 생성되고 초대 이메일이 발송되었습니다.\",\n  \"data\": {\n    \"vendorId\": 101,\n    \"vendorCode\": \"SUP-2025-0001\",\n    \"email\": \"contact@everp.com\",\n    \"tempPassword\": \"Abc12345!\",\n    \"invitedAt\": \"2025-10-13T10:05:00Z\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"계정 생성 권한이 없습니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "공급업체 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 공급업체를 찾을 수 없습니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "이미 계정 생성됨",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "conflict", value = "{ \"status\": 409, \"success\": false, \"message\": \"이미 계정이 발급된 공급업체입니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "처리 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"초대 이메일 발송 중 오류가 발생했습니다.\" }"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> inviteVendorAccount(
            @PathVariable("vendorId") Long vendorId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        String token = authorization.trim().toUpperCase(Locale.ROOT);
        if (!token.contains("PR_APPROVER") && !token.contains("PURCHASING_MANAGER") && !token.contains("ADMIN")) {
            throw new BusinessException(ErrorCode.VENDOR_ACCOUNT_FORBIDDEN);
        }
        if (token.contains("ERROR")) {
            throw new BusinessException(ErrorCode.VENDOR_ACCOUNT_PROCESSING_ERROR);
        }

        if (vendorId.equals(999L)) {
            throw new BusinessException(ErrorCode.VENDOR_ACCOUNT_ALREADY_EXISTS);
        }
        if (vendorId == null || vendorId < 1 || vendorId > 200) {
            throw new BusinessException(ErrorCode.VENDOR_NOT_FOUND);
        }

        String baseEmail = "contact@koreasteel.com";
        String accountEmail = baseEmail.substring(0, baseEmail.indexOf('@')) + "@everp.com";

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("vendorId", vendorId);
        data.put("vendorCode", "SUP-2025-0001");
        data.put("email", accountEmail);
        data.put("tempPassword", "Abc12345!");
        data.put("invitedAt", java.time.Instant.parse("2025-10-13T10:05:00Z"));

        return ResponseEntity.ok(ApiResponse.success(data, "공급업체 계정이 생성되고 초대 이메일이 발송되었습니다.", HttpStatus.OK));
    }

    @PatchMapping("/vendors/{vendorId}")
    @Operation(
            summary = "공급업체 정보 수정",
            description = "공급업체 기본 정보를 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "수정 성공",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 정보를 수정했습니다.\",\n  \"data\": {\n    \"vendorId\": 1,\n    \"vendorCode\": \"V-001\",\n    \"companyName\": \"대한철강\",\n    \"category\": \"원자재\",\n    \"address\": \"서울특별시 강남구 테헤란로 123\",\n    \"leadTimeDays\": 3,\n    \"materialList\": [\"철강재\", \"스테인리스\"],\n    \"statusCode\": \"ACTIVE\",\n    \"contactPerson\": \"홍길동\",\n    \"contactPosition\": \"영업팀장\",\n    \"contactPhone\": \"010-1234-5678\",\n    \"contactEmail\": \"contact@koreasteel.com\",\n    \"createdAt\": \"2025-10-07T00:00:00Z\",\n    \"updatedAt\": \"2025-10-13T12:00:00Z\"\n  }\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 필요",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "권한 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"공급업체 수정 권한이 없습니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "공급업체 없음",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"수정할 공급업체를 찾을 수 없습니다.\" }"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "422",
                            description = "검증 실패",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 본문 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"contactPerson\", \"reason\": \"FIELD_NOT_EDITABLE_BY_ADMIN\" },\n    { \"field\": \"contactPhone\", \"reason\": \"FIELD_NOT_EDITABLE_BY_ADMIN\" }\n  ]\n}"))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "처리 오류",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"공급업체 정보 수정 처리 중 오류가 발생했습니다.\" }"))
                    )
            }
    )
    public ResponseEntity<ApiResponse<Object>> updateVendor(
            @PathVariable("vendorId") Long vendorId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) MmVendorUpdateRequestDto request
    ) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        String token = authorization.trim().toUpperCase(Locale.ROOT);
        if (!token.contains("PR_APPROVER") && !token.contains("PURCHASING_MANAGER") && !token.contains("ADMIN")) {
            throw new BusinessException(ErrorCode.VENDOR_UPDATE_FORBIDDEN);
        }
        if (token.contains("ERROR")) {
            throw new BusinessException(ErrorCode.VENDOR_UPDATE_PROCESSING_ERROR);
        }

        java.util.List<Map<String, String>> errors = new java.util.ArrayList<>();
        if (request != null) {
            if (request.getContactPerson() != null) {
                errors.add(Map.of("field", "contactPerson", "reason", "FIELD_NOT_EDITABLE_BY_ADMIN"));
            }
            if (request.getContactPhone() != null) {
                errors.add(Map.of("field", "contactPhone", "reason", "FIELD_NOT_EDITABLE_BY_ADMIN"));
            }
            if (request.getContactEmail() != null) {
                errors.add(Map.of("field", "contactEmail", "reason", "FIELD_NOT_EDITABLE_BY_ADMIN"));
            }
            if (request.getStatusCode() != null && !java.util.Set.of("ACTIVE", "INACTIVE").contains(request.getStatusCode())) {
                errors.add(Map.of("field", "statusCode", "reason", "ALLOWED_VALUES: ACTIVE, INACTIVE"));
            }
            if (request.getLeadTimeDays() != null && request.getLeadTimeDays() < 0) {
                errors.add(Map.of("field", "leadTimeDays", "reason", "MUST_BE_POSITIVE_OR_ZERO"));
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(ErrorCode.VENDOR_UPDATE_VALIDATION_FAILED, errors);
        }

        if (vendorId == null || vendorId < 1 || vendorId > 200) {
            throw new BusinessException(ErrorCode.VENDOR_UPDATE_NOT_FOUND);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("vendorId", vendorId);
        data.put("vendorCode", "V-001");
        data.put("companyName", request != null && request.getCompanyName() != null ? request.getCompanyName() : "대한철강");
        data.put("category", request != null && request.getCategory() != null ? request.getCategory() : "원자재");
        data.put("address", request != null && request.getAddress() != null ? request.getAddress() : "서울특별시 강남구 테헤란로 123");
        data.put("leadTimeDays", request != null && request.getLeadTimeDays() != null ? request.getLeadTimeDays() : 3);
        data.put("materialList", request != null && request.getMaterialList() != null ? request.getMaterialList() : java.util.List.of("철강재", "스테인리스"));
        data.put("statusCode", request != null && request.getStatusCode() != null ? request.getStatusCode() : "ACTIVE");
        data.put("contactPerson", "홍길동");
        data.put("contactPosition", "영업팀장");
        data.put("contactPhone", "010-1234-5678");
        data.put("contactEmail", "contact@koreasteel.com");
        data.put("createdAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));
        data.put("updatedAt", java.time.Instant.parse("2025-10-13T12:00:00Z"));

        return ResponseEntity.ok(ApiResponse.success(data, "공급업체 정보를 수정했습니다.", HttpStatus.OK));
    }
}
