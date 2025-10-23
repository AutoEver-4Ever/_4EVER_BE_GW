package org.ever._4ever_be_gw.scmpp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.scmpp.dto.*;
import org.ever._4ever_be_gw.scmpp.dto.PeriodStatDto;
import org.ever._4ever_be_gw.scmpp.dto.bom.BomCreateRequestDto;
import org.ever._4ever_be_gw.scmpp.dto.bom.BomDetailDto;
import org.ever._4ever_be_gw.scmpp.dto.bom.BomListItemDto;
import org.ever._4ever_be_gw.scmpp.dto.mes.MesWorkOrderDetailDto;
import org.ever._4ever_be_gw.scmpp.dto.mes.MesWorkOrderDto;
import org.ever._4ever_be_gw.scmpp.dto.mes.MesWorkOrderSummaryDto;
import org.ever._4ever_be_gw.scmpp.dto.mps.MpsProductPlanDto;
import org.ever._4ever_be_gw.scmpp.dto.mrp.MrpOrderDto;
import org.ever._4ever_be_gw.scmpp.dto.mrp.MrpRequestBodyDto;
import org.ever._4ever_be_gw.scmpp.dto.mrp.MrpRequestSummaryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/scm-pp/pp")
@Tag(name = "생산관리(PP)", description = "생산 관리 API")
public class PpController {
    @PostMapping("/boms")
    @Operation(
            summary = "BOM 생성",
            description = "새로운 BOM을 생성합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> createBom(
            @RequestBody BomCreateRequestDto request
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("bomId", "1asd");
        response.put("bomCode", "BOM-001");

        return ResponseEntity.ok(ApiResponse.success(response, "BOM이 성공적으로 생성되었습니다.", HttpStatus.OK));
    }

    private static final Set<String> ALLOWED_PERIODS = Set.of("week", "month", "quarter", "year");

    @GetMapping("/statistics")
    @Operation(
            summary = "PP 통계 조회",
            description = "생산 진행 현황 및 BOM 관련 통계를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<StatsResponseDto<StatsMetricsDto>>> getProductionStatistics(
    ) {
        List<String> ALLOWED_PERIODS = List.of("week", "month", "quarter", "year");

        String periods = "";
        // periods 파라미터 없으면 기본적으로 전체 조회
        List<String> requested = (periods == null || periods.isBlank())
                ? ALLOWED_PERIODS
                : Arrays.stream(periods.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();

        List<String> invalid = requested.stream().filter(p -> !ALLOWED_PERIODS.contains(p)).toList();
        if (periods != null && !periods.isBlank() && (!invalid.isEmpty() || requested.stream().noneMatch(ALLOWED_PERIODS::contains))) {
            throw new BusinessException(ErrorCode.INVALID_PERIODS);
        }

        List<String> finalPeriods = requested.stream().filter(ALLOWED_PERIODS::contains).toList();

        StatsResponseDto.StatsResponseDtoBuilder<StatsMetricsDto> builder = StatsResponseDto.<StatsMetricsDto>builder();

        // week
        if (finalPeriods.contains("week")) {
            builder.week(StatsMetricsDto.builder()
                    .put("production_in_progress", PeriodStatDto.builder().value(42L).deltaRate(new BigDecimal("0.087")).build())
                    .put("production_completed", PeriodStatDto.builder().value(35L).deltaRate(new BigDecimal("0.062")).build())
                    .put("bom_count", PeriodStatDto.builder().value(18L).deltaRate(new BigDecimal("0.045")).build())
                    .build());
        }

        // month
        if (finalPeriods.contains("month")) {
            builder.month(StatsMetricsDto.builder()
                    .put("production_in_progress", PeriodStatDto.builder().value(168L).deltaRate(new BigDecimal("0.094")).build())
                    .put("production_completed", PeriodStatDto.builder().value(147L).deltaRate(new BigDecimal("0.078")).build())
                    .put("bom_count", PeriodStatDto.builder().value(72L).deltaRate(new BigDecimal("0.052")).build())
                    .build());
        }

        // quarter
        if (finalPeriods.contains("quarter")) {
            builder.quarter(StatsMetricsDto.builder()
                    .put("production_in_progress", PeriodStatDto.builder().value(498L).deltaRate(new BigDecimal("0.081")).build())
                    .put("production_completed", PeriodStatDto.builder().value(462L).deltaRate(new BigDecimal("0.069")).build())
                    .put("bom_count", PeriodStatDto.builder().value(210L).deltaRate(new BigDecimal("0.048")).build())
                    .build());
        }

        // year
        if (finalPeriods.contains("year")) {
            builder.year(StatsMetricsDto.builder()
                    .put("production_in_progress", PeriodStatDto.builder().value(2045L).deltaRate(new BigDecimal("0.073")).build())
                    .put("production_completed", PeriodStatDto.builder().value(1912L).deltaRate(new BigDecimal("0.061")).build())
                    .put("bom_count", PeriodStatDto.builder().value(865L).deltaRate(new BigDecimal("0.041")).build())
                    .build());
        }

        StatsResponseDto<StatsMetricsDto> response = builder.build();

        return ResponseEntity.ok(ApiResponse.success(response, "생산 통계 정보를 조회했습니다.", HttpStatus.OK));
    }




    @GetMapping("/boms")
    @Operation(
            summary = "BOM 목록 조회",
            description = "BOM 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBomList(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<BomListItemDto> items = Arrays.asList(
                BomListItemDto.builder()
                        .bomId("1")
                        .bomNumber("BOM-001")
                        .itemId("1")
                        .itemCode("PRD-001")
                        .itemName("스마트폰 케이스")
                        .version("v1.2")
                        .status("활성")
                        .lastModifiedAt(LocalDateTime.parse("2024-01-20T00:00:00"))
                        .build(),
                BomListItemDto.builder()
                        .bomId("2")
                        .bomNumber("BOM-002")
                        .itemId("2")
                        .itemCode("PRD-002")
                        .itemName("무선 이어폰")
                        .version("v2.0")
                        .status("활성")
                        .lastModifiedAt(LocalDateTime.parse("2024-01-18T00:00:00"))
                        .build()
        );

        PageDto pageInfo = PageDto.builder()
                .number(page)
                .size(size)
                .totalElements(2)
                .totalPages(1)
                .hasNext(false)
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("content", items);
        response.put("page", pageInfo);

        return ResponseEntity.ok(ApiResponse.success(response, "BOM 목록 조회 성공", HttpStatus.OK));
    }

    @GetMapping("/boms/{bomId}")
    @Operation(
            summary = "BOM 상세 조회",
            description = "BOM 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<BomDetailDto>> getBomDetail(
            @Parameter(name = "bomId", description = "BOM ID")
            @PathVariable String bomId
    ) {
        List<BomCreateRequestDto.ComponentDto> components = Arrays.asList(
                BomCreateRequestDto.ComponentDto.builder()
                        .itemId("1")
                        .itemNumber("MAT-001")
                        .itemName("TPU 소재")
                        .quantity(1)
                        .uomName("EA")
                        .level("Level 1")
                        .supplierCompanyName("공급사 C")
                        .operationId("1")
                        .operationName("사출성형")
                        .build(),
                BomCreateRequestDto.ComponentDto.builder()
                        .itemId("2")
                        .itemNumber("MAT-002")
                        .itemName("실리콘 패드")
                        .quantity(2)
                        .uomName("EA")
                        .level("Level 2")
                        .supplierCompanyName("공급사 D")
                        .operationId("2")
                        .operationName("조립")
                        .build(),
                BomCreateRequestDto.ComponentDto.builder()
                        .itemId("3")
                        .itemNumber("MAT-003")
                        .itemName("포장재")
                        .quantity(1)
                        .uomName("SET")
                        .level("Level 1")
                        .supplierCompanyName("공급사 C")
                        .operationId("3")
                        .operationName("검사")
                        .build()
        );

        Map<String, List<BomDetailDto.LevelComponentDto>> levelStructure = new HashMap<>();

        List<BomDetailDto.LevelComponentDto> level1 = Arrays.asList(
                BomDetailDto.LevelComponentDto.builder()
                        .itemId("1")
                        .itemNumber("MAT-001")
                        .itemName("TPU 소재")
                        .quantity(1)
                        .uomName("EA")
                        .build(),
                BomDetailDto.LevelComponentDto.builder()
                        .itemId("3")
                        .itemNumber("MAT-003")
                        .itemName("포장재")
                        .quantity(1)
                        .uomName("EA")
                        .build()
        );

        List<BomDetailDto.LevelComponentDto> level2 = Collections.singletonList(
                BomDetailDto.LevelComponentDto.builder()
                        .itemId("2")
                        .itemNumber("MAT-002")
                        .itemName("실리콘 패드")
                        .quantity(2)
                        .uomName("EA")
                        .build()
        );

        levelStructure.put("Level 1", level1);
        levelStructure.put("Level 2", level2);

        List<BomCreateRequestDto.RoutingDto> routing = Arrays.asList(
                BomCreateRequestDto.RoutingDto.builder()
                        .sequence(10)
                        .operationId("1")
                        .operationName("사출성형")
                        .runTime(5)
                        .build(),
                BomCreateRequestDto.RoutingDto.builder()
                        .sequence(20)
                        .operationId("2")
                        .operationName("조립")
                        .runTime(3)
                        .build(),
                BomCreateRequestDto.RoutingDto.builder()
                        .sequence(30)
                        .operationId("3")
                        .operationName("포장")
                        .runTime(2)
                        .build()
        );

        BomDetailDto response = BomDetailDto.builder()
                .bomId(bomId)
                .bomNumber("BOM-001")
                .productId("1")
                .productNumber("PRD-001")
                .productName("스마트폰 케이스")
                .version("v1.2")
                .status("활성")
                .lastModifiedAt(LocalDateTime.parse("2024-01-20T00:00:00"))
                .components(components)
                .levelStructure(levelStructure)
                .routing(routing)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "BOM 상세 조회 성공", HttpStatus.OK));
    }


    @DeleteMapping("/boms/{bomId}")
    @Operation(
            summary = "BOM 삭제",
            description = "BOM을 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"BOM이 성공적으로 삭제되었습니다.\",\n  \"data\": null\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Void>> deleteBom(
            @Parameter(name = "bomId", description = "BOM ID")
            @PathVariable String bomId
    ) {
        return ResponseEntity.ok(ApiResponse.success(null, "BOM이 성공적으로 삭제되었습니다.", HttpStatus.OK));
    }

    @PostMapping("/mrp/request-summary")
    @Operation(
            summary = "MRP 자재 구매 요청 리스트",
            description = "MRP 자재 구매 요청 목록을 생성합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<MrpRequestSummaryDto>> getMrpRequestSummary(
            @RequestBody MrpRequestBodyDto request
    ) {
        List<MrpRequestSummaryDto.MrpItemDto> items = Arrays.asList(
                MrpRequestSummaryDto.MrpItemDto.builder()
                        .mrpId("1")
                        .quotationNumber("Q-2024-001")
                        .itemName("스테인리스 스틸")
                        .quantity(400)
                        .unitPrice(1200)
                        .totalAmount(480000)
                        .supplierCompanyName("포스코")
                        .dueDate("2024-02-08")
                        .status("계획")
                        .build(),
                MrpRequestSummaryDto.MrpItemDto.builder()
                        .mrpId("2")
                        .quotationNumber("Q-2024-002")
                        .itemName("구리선")
                        .quantity(600)
                        .unitPrice(800)
                        .totalAmount(480000)
                        .supplierCompanyName("LS전선")
                        .dueDate("2024-02-09")
                        .status("계획")
                        .build(),
                MrpRequestSummaryDto.MrpItemDto.builder()
                        .mrpId("3")
                        .quotationNumber("Q-2024-003")
                        .itemName("베어링 6205")
                        .quantity(100)
                        .unitPrice(15000)
                        .totalAmount(1500000)
                        .supplierCompanyName("SKF코리아")
                        .dueDate("2024-02-07")
                        .status("계획")
                        .build(),
                MrpRequestSummaryDto.MrpItemDto.builder()
                        .mrpId("4")
                        .quotationNumber("Q-2024-001")
                        .itemName("알루미늄 프로파일")
                        .quantity(300)
                        .unitPrice(2500)
                        .totalAmount(750000)
                        .supplierCompanyName("한국알루미늄")
                        .dueDate("2024-02-10")
                        .status("계획")
                        .build()
        );

        MrpRequestSummaryDto response = MrpRequestSummaryDto.builder()
                .selectedOrderCount(4)
                .totalExpectedAmount(3210000)
                .requestDate("2025-10-13")
                .items(items)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "구매 요청 요약을 계산했습니다.", HttpStatus.OK));
    }

    @GetMapping("/mrp/orders")
    @Operation(
            summary = "MRP 순소요 목록 조회",
            description = "MRP 순소요 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<List<MrpOrderDto>>> getMrpOrders(
            @Parameter(name = "productId", description = "제품 ID")
            @RequestParam(required = false) String productId,
            @Parameter(name = "quotationId", description = "견적 ID")
            @RequestParam(required = false) String quotationId,
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<MrpOrderDto> response = Arrays.asList(
                MrpOrderDto.builder()
                        .itemId("1")
                        .itemName("스테인리스 스틸")
                        .requiredQuantity(500)
                        .currentStock(200)
                        .safetyStock(50)
                        .availableStock(150)
                        .availableStatusCode("INSUFFICIENT")
                        .shortageQty(350)
                        .itemType("구매품")
                        .procurementStartDate("2024-02-01")
                        .expectedArrivalDate("2024-02-08")
                        .supplierCompanyName("포스코")
                        .build(),
                MrpOrderDto.builder()
                        .itemId("2")
                        .itemName("구리선")
                        .requiredQuantity(800)
                        .currentStock(300)
                        .safetyStock(100)
                        .availableStock(200)
                        .availableStatusCode("INSUFFICIENT")
                        .shortageQty(600)
                        .itemType("구매품")
                        .procurementStartDate("2024-02-02")
                        .expectedArrivalDate("2024-02-09")
                        .supplierCompanyName("LS전선")
                        .build(),
                MrpOrderDto.builder()
                        .itemId("3")
                        .itemName("베어링 6205")
                        .requiredQuantity(200)
                        .currentStock(150)
                        .safetyStock(30)
                        .availableStock(120)
                        .availableStatusCode("INSUFFICIENT")
                        .shortageQty(80)
                        .itemType("구매품")
                        .procurementStartDate("2024-02-03")
                        .expectedArrivalDate("2024-02-07")
                        .supplierCompanyName("SKF코리아")
                        .build(),
                MrpOrderDto.builder()
                        .itemId("4")
                        .itemName("볼트 M8x20")
                        .requiredQuantity(1000)
                        .currentStock(1200)
                        .safetyStock(200)
                        .availableStock(1000)
                        .availableStatusCode("SUFFICIENT")
                        .shortageQty(null)
                        .itemType("구매품")
                        .procurementStartDate(null)
                        .expectedArrivalDate(null)
                        .supplierCompanyName("동양볼트")
                        .build(),
                MrpOrderDto.builder()
                        .itemId("5")
                        .itemName("알루미늄 프로파일")
                        .requiredQuantity(300)
                        .currentStock(100)
                        .safetyStock(50)
                        .availableStock(50)
                        .availableStatusCode("INSUFFICIENT")
                        .shortageQty(250)
                        .itemType("구매품")
                        .procurementStartDate("2024-02-01")
                        .expectedArrivalDate("2024-02-10")
                        .supplierCompanyName("한국알루미늄")
                        .build()
        );

        return ResponseEntity.ok(ApiResponse.success(response, "자재 조달 계획을 조회했습니다.", HttpStatus.OK));
    }





    @GetMapping("/mrp/planned-orders/detail/{mrpId}")
    @Operation(
            summary = "MRP 계획 주문 상세 조회",
            description = "MRP 계획 주문 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<PlannedOrderDetailDto>> getPlannedOrderDetail(
            @Parameter(name = "mrpId", description = "계획 주문 ID")
            @PathVariable String mrpId
    ) {
        List<PlannedOrderDetailDto.OrderItemDto> orderItems = Arrays.asList(
                PlannedOrderDetailDto.OrderItemDto.builder()
                        .itemId("1")
                        .itemName("강판")
                        .quantity(500)
                        .uomName("EA")
                        .unitPrice(5000)
                        .build(),
                PlannedOrderDetailDto.OrderItemDto.builder()
                        .itemId("2")
                        .itemName("볼트")
                        .quantity(100)
                        .uomName("EA")
                        .unitPrice(500)
                        .build()
        );

        PlannedOrderDetailDto response = PlannedOrderDetailDto.builder()
                .mrpId(mrpId)
                .quotationId("1")
                .quotationCode("Q-2024-001")
                .requesterId("1")
                .requesterName("김철수")
                .departmentName("생산팀")
                .requestDate("2024-01-15")
                .desiredDueDate("2024-01-25")
                .status("승인")
                .orderItems(orderItems)
                .totalAmount(2500000)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "계획 주문 요청 상세를 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/mrp/planned-orders/list")
    @Operation(
            summary = "MRP 계획 주문 목록 조회",
            description = "MRP 계획 주문 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlannedOrderList(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<PlannedOrderListItemDto> items = Arrays.asList(
                PlannedOrderListItemDto.builder()
                        .mrpId("1")
                        .quotationId("1")
                        .quotationNumber("Q-2024-001")
                        .itemId("1")
                        .itemName("스테인리스 스틸")
                        .quantity(400)
                        .procurementStartDate("2024-02-01")
                        .statusCode("PLANNING")
                        .build(),
                PlannedOrderListItemDto.builder()
                        .mrpId("2")
                        .quotationId("2")
                        .quotationNumber("Q-2024-002")
                        .itemId("2")
                        .itemName("구리선")
                        .quantity(600)
                        .procurementStartDate("2024-02-02")
                        .statusCode("PENDING")
                        .build(),
                PlannedOrderListItemDto.builder()
                        .mrpId("3")
                        .quotationId("3")
                        .quotationNumber("Q-2024-003")
                        .itemId("3")
                        .itemName("베어링 6205")
                        .quantity(100)
                        .procurementStartDate("2024-02-03")
                        .statusCode("APPROVAL")
                        .build(),
                PlannedOrderListItemDto.builder()
                        .mrpId("4")
                        .quotationId("1")
                        .quotationNumber("Q-2024-001")
                        .itemId("4")
                        .itemName("알루미늄 프로파일")
                        .quantity(300)
                        .procurementStartDate("2024-02-01")
                        .statusCode("REJECTED")
                        .build()
        );

        PageDto pageInfo = PageDto.builder()
                .number(page)
                .size(size)
                .totalElements(4)
                .totalPages(1)
                .hasNext(false)
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("content", items);
        response.put("page", pageInfo);

        return ResponseEntity.ok(ApiResponse.success(response, "계획 주문 요청 목록을 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/mps/plans")
    @Operation(
            summary = "제품별 MPS 조회",
            description = "제품별 Master Production Schedule(MPS) 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<MpsProductPlanDto>> getMpsPlans(
            @Parameter(name = "itemId", description = "제품 ID")
            @RequestParam(required = false) String itemId,
            @Parameter(name = "startdate", description = "시작일")
            @RequestParam(required = false) String startdate,
            @Parameter(name = "enddate", description = "종료일")
            @RequestParam(required = false) String enddate
    ) {
        List<String> periods = Arrays.asList("9월 1주차", "9월 2주차", "9월 3주차", "9월 4주차", "10월 1주차", "10월 2주차");

        List<Integer> demand = Arrays.asList(null, null, null, null, 20, 15);
        List<Integer> requiredInventory = Arrays.asList(null, null, 20, 15, 20, 15);
        List<Integer> productionNeeded = Arrays.asList(null, null, 20, 15, null, null);
        List<Integer> plannedProduction = Arrays.asList(null, null, 20, 15, 20, 15);

        MpsProductPlanDto response = MpsProductPlanDto.builder()
                .productId("1")
                .productName("도어패널")
                .startDate(startdate)
                .endDate(enddate)
                .periods(periods)
                .demand(demand)
                .requiredInventory(requiredInventory)
                .productionNeeded(productionNeeded)
                .plannedProduction(plannedProduction)
                .totalPlannedProduction(70)
                .totalDemand(35)
                .productionWeeks(2)
                .averageWeeklyProduction(2)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "제품별 MPS 조회에 성공했습니다.", HttpStatus.OK));
    }

    @GetMapping("/quotations/{quotationId}/simulate")
    @Operation(
            summary = "견적에 대한 ATP + MPS + MRP 시뮬레이션 실행",
            description = "견적에 대한 ATP(Available to Promise), MPS, MRP 시뮬레이션을 실행합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<QuotationSimulationDto>> simulateQuotation(
            @Parameter(name = "quotationId", description = "견적 ID")
            @PathVariable String quotationId,
            @RequestParam(required = false) Boolean forceRecalculate
    ) {
        List<QuotationSimulationDto.ShortageItemDto> shortages = Arrays.asList(
                QuotationSimulationDto.ShortageItemDto.builder()
                        .itemId("1")
                        .itemName("스테인리스 스틸")
                        .requiredQuantity(100)
                        .currentStock(50)
                        .shortQuantity(50)
                        .build(),
                QuotationSimulationDto.ShortageItemDto.builder()
                        .itemId("2")
                        .itemName("구리선")
                        .requiredQuantity(200)
                        .currentStock(150)
                        .shortQuantity(50)
                        .build(),
                QuotationSimulationDto.ShortageItemDto.builder()
                        .itemId("3")
                        .itemName("베어링")
                        .requiredQuantity(50)
                        .currentStock(30)
                        .shortQuantity(20)
                        .build()
        );

        QuotationSimulationDto response = QuotationSimulationDto.builder()
                .quotationId(quotationId)
                .quotationCode("Q-2024-001")
                .customerCompanyId("1")
                .customerCompanyId("현대자동차")
                .productId("1")
                .productName("도어패널")
                .requestQuantity(500)
                .requestDueDate("2024-02-15")
                .simulation(QuotationSimulationDto.SimulationResultDto.builder()
                        .status("FAIL")
                        .availableQty(130)
                        .suggestedDueDate("2024-03-10")
                        .generatedAt("2025-10-08T12:00:00Z")
                        .build())
                .shortages(shortages)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "견적 시뮬레이션이 성공적으로 완료되었습니다.", HttpStatus.OK));
    }

    @GetMapping("/quotations/{quotationId}/preview")
    @Operation(
            summary = "제안납기 확정 프리뷰",
            description = "제안 납기 계획 프리뷰를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<DueDatePreviewDto>> getQuotationPreview(
            @Parameter(name = "quotationId", description = "견적 ID")
            @PathVariable String quotationId
    ) {
        List<DueDatePreviewDto.WeekPlanDto> weeks = Arrays.asList(
                DueDatePreviewDto.WeekPlanDto.builder()
                        .week("2024-02-3W")
                        .demand(0)
                        .requiredQuantity(0)
                        .productionQuantity(300)
                        .mps(300)
                        .build(),
                DueDatePreviewDto.WeekPlanDto.builder()
                        .week("2024-02-4W")
                        .demand(500)
                        .requiredQuantity(500)
                        .productionQuantity(200)
                        .mps(200)
                        .build(),
                DueDatePreviewDto.WeekPlanDto.builder()
                        .week("2024-03-1W")
                        .demand(0)
                        .requiredQuantity(0)
                        .productionQuantity(0)
                        .mps(0)
                        .build(),
                DueDatePreviewDto.WeekPlanDto.builder()
                        .week("2024-03-2W")
                        .demand(0)
                        .requiredQuantity(0)
                        .productionQuantity(0)
                        .mps(0)
                        .build()
        );

        DueDatePreviewDto response = DueDatePreviewDto.builder()
                .quotationNumber("Q-2024-001")
                .customerCompanyName("현대자동차")
                .productName("도어패널")
                .confirmedDueDate("2024-03-10")
                .weeks(weeks)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "제안 납기 계획을 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/mes/work-orders")
    @Operation(
            summary = "MES 작업 목록 조회",
            description = "MES(Manufacturing Execution System) 작업 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMesWorkOrders(
            @Parameter(name = "status", description = "작업 상태")
            @RequestParam(required = false) String status,
            @Parameter(name = "quotationId", description = "견적 ID")
            @RequestParam(required = false) String quotationId,
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        List<MesWorkOrderDto> items = Arrays.asList(
                MesWorkOrderDto.builder()
                        .mesId("1")
                        .mesNumber("WO-2024-001")
                        .productId("1")
                        .productName("산업용 모터 5HP")
                        .quantity(50)
                        .uomName("EA")
                        .quotationId("1")
                        .quotationNumber("Q-2024-001")
                        .status("IN_PROGRESS")
                        .currentOperation("OP30")
                        .startDate("2024-01-15")
                        .endDate("2024-02-10")
                        .progressRate(65)
                        .sequence(Arrays.asList("OP10", "OP20", "OP30", "OP40", "OP50", "OP60"))
                        .build(),
                MesWorkOrderDto.builder()
                        .mesId("2")
                        .mesNumber("WO-2024-002")
                        .productId("2")
                        .productName("알루미늄 프레임")
                        .quantity(100)
                        .uomName("EA")
                        .quotationId("2")
                        .quotationNumber("Q-2024-002")
                        .status("PLANNED")
                        .currentOperation("OP10")
                        .startDate("2024-01-20")
                        .endDate("2024-02-15")
                        .progressRate(0)
                        .sequence(Arrays.asList("OP10", "OP20", "OP30", "OP40", "OP50", "OP60"))
                        .build()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("content", items);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", 2);
        response.put("totalPages", 1);

        return ResponseEntity.ok(ApiResponse.success(response, "성공적으로 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/mes/work-orders/{mesId}")
    @Operation(
            summary = "MES 작업 상세 조회",
            description = "MES 작업 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공"
                    )
            }
    )
    public ResponseEntity<ApiResponse<MesWorkOrderDetailDto>> getMesWorkOrderDetail(
            @Parameter(name = "mesId", description = "MES 작업 ID")
            @PathVariable String mesId
    ) {
        List<MesWorkOrderDetailDto.OperationDto> operations = Arrays.asList(
                MesWorkOrderDetailDto.OperationDto.builder()
                        .operationNumber("OP10")
                        .operationName("재료 준비")
                        .sequence(1)
                        .statusCode("COMPLETED")
                        .startedAt("09:00")
                        .finishedAt("10:30")
                        .durationHours(3.5)
                        .manager(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
                        .build(),
                MesWorkOrderDetailDto.OperationDto.builder()
                        .operationNumber("OP20")
                        .operationName("가공")
                        .sequence(2)
                        .statusCode("COMPLETED")
                        .startedAt("10:30")
                        .finishedAt("14:00")
                        .durationHours(3.5)
                        .manager(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
                        .build(),
                MesWorkOrderDetailDto.OperationDto.builder()
                        .operationNumber("OP30")
                        .operationName("조립")
                        .sequence(3)
                        .statusCode("IN_PROGRESS")
                        .startedAt("14:00")
                        .finishedAt(null)
                        .durationHours(null)
                        .manager(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
                        .build(),
                MesWorkOrderDetailDto.OperationDto.builder()
                        .operationNumber("OP40")
                        .operationName("테스트")
                        .sequence(4)
                        .statusCode("PENDING")
                        .startedAt(null)
                        .finishedAt(null)
                        .durationHours(null)
                        .manager(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
                        .build()
        );

        MesWorkOrderDetailDto response = MesWorkOrderDetailDto.builder()
                .mesId("1")
                .mesNumber("WO-2024-001")
                .productId("1")
                .productName("산업용 모터 5HP")
                .quantity(50)
                .uomName("EA")
                .progressPercent(65)
                .statusCode("IN_PROGRESS")
                .plan(MesWorkOrderDetailDto.PlanInfo.builder().startDate("2024-01-15").dueDate("2024-02-10").build())
                .currentOperation("OP30")
                .operations(operations)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "작업 지시 상세를 조회했습니다.", HttpStatus.OK));
    }

}
