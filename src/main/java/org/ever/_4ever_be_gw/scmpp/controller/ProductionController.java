package org.ever._4ever_be_gw.scmpp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.scmpp.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/scm-pp/pp")
@Tag(name = "Production Management", description = "생산 관리 API")
public class ProductionController {
    @PostMapping("/boms")
    @Operation(
            summary = "BOM 생성",
            description = "새로운 BOM을 생성합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"BOM이 성공적으로 생성되었습니다.\",\n  \"data\": {\n    \"bomId\": 1,\n    \"bomCode\": \"BOM-001\"\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> createBom(
            @RequestBody BomCreateRequestDto request
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("bomId", 1L);
        response.put("bomCode", "BOM-001");

        return ResponseEntity.ok(ApiResponse.success(response, "BOM이 성공적으로 생성되었습니다.", HttpStatus.OK));
    }



    @GetMapping("/boms")
    @Operation(
            summary = "BOM 목록 조회",
            description = "BOM 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"BOM 목록 조회 성공\",\n  \"data\": {\n    \"content\": [\n      {\n        \"bomId\": 1,\n        \"bomCode\": \"BOM-001\",\n        \"productId\": 1,\n        \"productCode\": \"PRD-001\",\n        \"productName\": \"스마트폰 케이스\",\n        \"version\": \"v1.2\",\n        \"status\": \"활성\",\n        \"lastModifiedAt\": \"2024-01-20T00:00:00Z\"\n      },\n      {\n        \"bomId\": 2,\n        \"bomCode\": \"BOM-002\",\n        \"productId\": 2,\n        \"productCode\": \"PRD-002\",\n        \"productName\": \"무선 이어폰\",\n        \"version\": \"v2.0\",\n        \"status\": \"활성\",\n        \"lastModifiedAt\": \"2024-01-18T00:00:00Z\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 2,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
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
                        .bomId(1L)
                        .bomCode("BOM-001")
                        .productId(1L)
                        .productCode("PRD-001")
                        .productName("스마트폰 케이스")
                        .version("v1.2")
                        .status("활성")
                        .lastModifiedAt(LocalDateTime.parse("2024-01-20T00:00:00"))
                        .build(),
                BomListItemDto.builder()
                        .bomId(2L)
                        .bomCode("BOM-002")
                        .productId(2L)
                        .productCode("PRD-002")
                        .productName("무선 이어폰")
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
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"BOM 상세 조회 성공\",\n  \"data\": {\n    \"bomId\": 1,\n    \"bomCode\": \"BOM-001\",\n    \"productId\": 1,\n    \"productCode\": \"PRD-001\",\n    \"productName\": \"스마트폰 케이스\",\n    \"version\": \"v1.2\",\n    \"status\": \"활성\",\n    \"lastModifiedAt\": \"2024-01-20T00:00:00Z\",\n    \"components\": [\n      {\n        \"id\": 1,\n        \"code\": \"MAT-001\",\n        \"name\": \"TPU 소재\",\n        \"quantity\": 1,\n        \"unit\": \"EA\",\n        \"level\": \"Level 1\",\n        \"supplier\": \"공급사 C\",\n        \"operationId\": 1,\n        \"operationName\": \"사출성형\"\n      },\n      {\n        \"id\": 2,\n        \"code\": \"MAT-002\",\n        \"name\": \"실리콘 패드\",\n        \"quantity\": 2,\n        \"unit\": \"EA\",\n        \"level\": \"Level 2\",\n        \"supplier\": \"공급사 D\",\n        \"operationId\": 2,\n        \"operationName\": \"조립\"\n      },\n      {\n        \"id\": 3,\n        \"code\": \"MAT-003\",\n        \"name\": \"포장재\",\n        \"quantity\": 1,\n        \"unit\": \"SET\",\n        \"level\": \"Level 1\",\n        \"supplier\": \"공급사 C\",\n        \"operationId\": 3,\n        \"operationName\": \"검사\"\n      }\n    ],\n    \"levelStructure\": {\n      \"Level 1\": [\n        {\n          \"code\": \"MAT-001\",\n          \"name\": \"TPU 소재\",\n          \"quantity\": \"1 EA\"\n        },\n        {\n          \"code\": \"MAT-003\",\n          \"name\": \"포장재\",\n          \"quantity\": \"1 SET\"\n        }\n      ],\n      \"Level 2\": [\n        {\n          \"code\": \"MAT-002\",\n          \"name\": \"실리콘 패드\",\n          \"quantity\": \"2 EA\"\n        }\n      ]\n    },\n    \"routing\": [\n      {\n        \"sequence\": 10,\n        \"operationName\": \"사출성형\",\n        \"setupTime\": 30,\n        \"runTime\": 5\n      },\n      {\n        \"sequence\": 20,\n        \"operationName\": \"조립\",\n        \"setupTime\": 15,\n        \"runTime\": 3\n      },\n      {\n        \"sequence\": 30,\n        \"operationName\": \"포장\",\n        \"setupTime\": 10,\n        \"runTime\": 2\n      }\n    ]\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<BomDetailDto>> getBomDetail(
            @Parameter(name = "bomId", description = "BOM ID")
            @PathVariable Long bomId
    ) {
        List<BomCreateRequestDto.ComponentDto> components = Arrays.asList(
                BomCreateRequestDto.ComponentDto.builder()
                        .id(1L)
                        .code("MAT-001")
                        .name("TPU 소재")
                        .quantity(1)
                        .unit("EA")
                        .level("Level 1")
                        .supplier("공급사 C")
                        .operationId(1)
                        .operationName("사출성형")
                        .build(),
                BomCreateRequestDto.ComponentDto.builder()
                        .id(2L)
                        .code("MAT-002")
                        .name("실리콘 패드")
                        .quantity(2)
                        .unit("EA")
                        .level("Level 2")
                        .supplier("공급사 D")
                        .operationId(2)
                        .operationName("조립")
                        .build(),
                BomCreateRequestDto.ComponentDto.builder()
                        .id(3L)
                        .code("MAT-003")
                        .name("포장재")
                        .quantity(1)
                        .unit("SET")
                        .level("Level 1")
                        .supplier("공급사 C")
                        .operationId(3)
                        .operationName("검사")
                        .build()
        );

        Map<String, List<BomDetailDto.LevelComponentDto>> levelStructure = new HashMap<>();

        List<BomDetailDto.LevelComponentDto> level1 = Arrays.asList(
                BomDetailDto.LevelComponentDto.builder()
                        .code("MAT-001")
                        .name("TPU 소재")
                        .quantity("1 EA")
                        .build(),
                BomDetailDto.LevelComponentDto.builder()
                        .code("MAT-003")
                        .name("포장재")
                        .quantity("1 SET")
                        .build()
        );

        List<BomDetailDto.LevelComponentDto> level2 = Collections.singletonList(
                BomDetailDto.LevelComponentDto.builder()
                        .code("MAT-002")
                        .name("실리콘 패드")
                        .quantity("2 EA")
                        .build()
        );

        levelStructure.put("Level 1", level1);
        levelStructure.put("Level 2", level2);

        List<BomCreateRequestDto.RoutingDto> routing = Arrays.asList(
                BomCreateRequestDto.RoutingDto.builder()
                        .sequence(10)
                        .operationId(1)
                        .operationName("사출성형")
                        .setupTime(30)
                        .runTime(5)
                        .build(),
                BomCreateRequestDto.RoutingDto.builder()
                        .sequence(20)
                        .operationId(2)
                        .operationName("조립")
                        .setupTime(15)
                        .runTime(3)
                        .build(),
                BomCreateRequestDto.RoutingDto.builder()
                        .sequence(30)
                        .operationId(3)
                        .operationName("포장")
                        .setupTime(10)
                        .runTime(2)
                        .build()
        );

        BomDetailDto response = BomDetailDto.builder()
                .bomId(bomId)
                .bomCode("BOM-001")
                .productId(1L)
                .productCode("PRD-001")
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

    @PatchMapping("/boms/{bomId}")
    @Operation(
            summary = "BOM 수정",
            description = "BOM 정보를 수정합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"BOM이 성공적으로 수정되었습니다.\",\n  \"data\": {\n    \"bomId\": 1,\n    \"bomCode\": \"BOM-001\"\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateBom(
            @Parameter(name = "bomId", description = "BOM ID")
            @PathVariable Long bomId,
            @RequestBody BomCreateRequestDto request
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("bomId", bomId);
        response.put("bomCode", "BOM-001");

        return ResponseEntity.ok(ApiResponse.success(response, "BOM이 성공적으로 수정되었습니다.", HttpStatus.OK));
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
            @PathVariable Long bomId
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
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매 요청 요약을 계산했습니다.\",\n  \"data\": {\n    \"selectedOrderCount\": 4,\n    \"totalExpectedAmount\": 3210000,\n    \"requestDate\": \"2025-10-13\",\n    \"items\": [\n      {\n        \"plannedId\": 1,\n        \"quotationCode\": \"Q-2024-001\",\n        \"itemName\": \"스테인리스 스틸\",\n        \"quantity\": 400,\n        \"unitPrice\": 1200,\n        \"totalAmount\": 480000,\n        \"supplier\": \"포스코\",\n        \"dueDate\": \"2024-02-08\",\n        \"status\": \"계획\"\n      },\n      {\n        \"plannedId\": 2,\n        \"quotationCode\": \"Q-2024-002\",\n        \"itemName\": \"구리선\",\n        \"quantity\": 600,\n        \"unitPrice\": 800,\n        \"totalAmount\": 480000,\n        \"supplier\": \"LS전선\",\n        \"dueDate\": \"2024-02-09\",\n        \"status\": \"계획\"\n      },\n      {\n        \"plannedId\": 3,\n        \"quotationCode\": \"Q-2024-003\",\n        \"itemName\": \"베어링 6205\",\n        \"quantity\": 100,\n        \"unitPrice\": 15000,\n        \"totalAmount\": 1500000,\n        \"supplier\": \"SKF코리아\",\n        \"dueDate\": \"2024-02-07\",\n        \"status\": \"계획\"\n      },\n      {\n        \"plannedId\": 4,\n        \"quotationCode\": \"Q-2024-001\",\n        \"itemName\": \"알루미늄 프로파일\",\n        \"quantity\": 300,\n        \"unitPrice\": 2500,\n        \"totalAmount\": 750000,\n        \"supplier\": \"한국알루미늄\",\n        \"dueDate\": \"2024-02-10\",\n        \"status\": \"계획\"\n      }\n    ]\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<MrpRequestSummaryDto>> getMrpRequestSummary(
            @RequestBody MrpRequestBodyDto request
    ) {
        List<MrpRequestSummaryDto.MrpItemDto> items = Arrays.asList(
                MrpRequestSummaryDto.MrpItemDto.builder()
                        .plannedId(1L)
                        .quotationCode("Q-2024-001")
                        .itemName("스테인리스 스틸")
                        .quantity(400)
                        .unitPrice(1200)
                        .totalAmount(480000)
                        .supplier("포스코")
                        .dueDate("2024-02-08")
                        .status("계획")
                        .build(),
                MrpRequestSummaryDto.MrpItemDto.builder()
                        .plannedId(2L)
                        .quotationCode("Q-2024-002")
                        .itemName("구리선")
                        .quantity(600)
                        .unitPrice(800)
                        .totalAmount(480000)
                        .supplier("LS전선")
                        .dueDate("2024-02-09")
                        .status("계획")
                        .build(),
                MrpRequestSummaryDto.MrpItemDto.builder()
                        .plannedId(3L)
                        .quotationCode("Q-2024-003")
                        .itemName("베어링 6205")
                        .quantity(100)
                        .unitPrice(15000)
                        .totalAmount(1500000)
                        .supplier("SKF코리아")
                        .dueDate("2024-02-07")
                        .status("계획")
                        .build(),
                MrpRequestSummaryDto.MrpItemDto.builder()
                        .plannedId(4L)
                        .quotationCode("Q-2024-001")
                        .itemName("알루미늄 프로파일")
                        .quantity(300)
                        .unitPrice(2500)
                        .totalAmount(750000)
                        .supplier("한국알루미늄")
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
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"자재 조달 계획을 조회했습니다.\",\n  \"data\": [\n    {\n      \"itemId\": 1,\n      \"itemName\": \"스테인리스 스틸\",\n      \"requiredQty\": 500,\n      \"currentStock\": 200,\n      \"safetyStock\": 50,\n      \"availableStock\": 150,\n      \"availableStatus\": \"부족\",\n      \"shortageQty\": 350,\n      \"itemType\": \"구매품\",\n      \"procurementStartDate\": \"2024-02-01\",\n      \"expectedArrivalDate\": \"2024-02-08\",\n      \"supplier\": \"포스코\"\n    },\n    {\n      \"itemId\": 2,\n      \"itemName\": \"구리선\",\n      \"requiredQty\": 800,\n      \"currentStock\": 300,\n      \"safetyStock\": 100,\n      \"availableStock\": 200,\n      \"availableStatus\": \"부족\",\n      \"shortageQty\": 600,\n      \"itemType\": \"구매품\",\n      \"procurementStartDate\": \"2024-02-02\",\n      \"expectedArrivalDate\": \"2024-02-09\",\n      \"supplier\": \"LS전선\"\n    },\n    {\n      \"itemId\": 3,\n      \"itemName\": \"베어링 6205\",\n      \"requiredQty\": 200,\n      \"currentStock\": 150,\n      \"safetyStock\": 30,\n      \"availableStock\": 120,\n      \"availableStatus\": \"부족\",\n      \"shortageQty\": 80,\n      \"itemType\": \"구매품\",\n      \"procurementStartDate\": \"2024-02-03\",\n      \"expectedArrivalDate\": \"2024-02-07\",\n      \"supplier\": \"SKF코리아\"\n    },\n    {\n      \"itemId\": 4,\n      \"itemName\": \"볼트 M8x20\",\n      \"requiredQty\": 1000,\n      \"currentStock\": 1200,\n      \"safetyStock\": 200,\n      \"availableStock\": 1000,\n      \"availableStatus\": \"충족\",\n      \"shortageQty\": null,\n      \"itemType\": \"구매품\",\n      \"procurementStartDate\": null,\n      \"expectedArrivalDate\": null,\n      \"supplier\": \"동양볼트\"\n    },\n    {\n      \"itemId\": 5,\n      \"itemName\": \"알루미늄 프로파일\",\n      \"requiredQty\": 300,\n      \"currentStock\": 100,\n      \"safetyStock\": 50,\n      \"availableStock\": 50,\n      \"availableStatus\": \"부족\",\n      \"shortageQty\": 250,\n      \"itemType\": \"구매품\",\n      \"procurementStartDate\": \"2024-02-01\",\n      \"expectedArrivalDate\": \"2024-02-10\",\n      \"supplier\": \"한국알루미늄\"\n    }\n  ]\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<List<MrpOrderDto>>> getMrpOrders(
            @Parameter(name = "productId", description = "제품 ID")
            @RequestParam(required = false) Long productId,
            @Parameter(name = "quotationId", description = "견적 ID")
            @RequestParam(required = false) Long quotationId,
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<MrpOrderDto> response = Arrays.asList(
                MrpOrderDto.builder()
                        .itemId(1L)
                        .itemName("스테인리스 스틸")
                        .requiredQty(500)
                        .currentStock(200)
                        .safetyStock(50)
                        .availableStock(150)
                        .availableStatus("부족")
                        .shortageQty(350)
                        .itemType("구매품")
                        .procurementStartDate("2024-02-01")
                        .expectedArrivalDate("2024-02-08")
                        .supplier("포스코")
                        .build(),
                MrpOrderDto.builder()
                        .itemId(2L)
                        .itemName("구리선")
                        .requiredQty(800)
                        .currentStock(300)
                        .safetyStock(100)
                        .availableStock(200)
                        .availableStatus("부족")
                        .shortageQty(600)
                        .itemType("구매품")
                        .procurementStartDate("2024-02-02")
                        .expectedArrivalDate("2024-02-09")
                        .supplier("LS전선")
                        .build(),
                MrpOrderDto.builder()
                        .itemId(3L)
                        .itemName("베어링 6205")
                        .requiredQty(200)
                        .currentStock(150)
                        .safetyStock(30)
                        .availableStock(120)
                        .availableStatus("부족")
                        .shortageQty(80)
                        .itemType("구매품")
                        .procurementStartDate("2024-02-03")
                        .expectedArrivalDate("2024-02-07")
                        .supplier("SKF코리아")
                        .build(),
                MrpOrderDto.builder()
                        .itemId(4L)
                        .itemName("볼트 M8x20")
                        .requiredQty(1000)
                        .currentStock(1200)
                        .safetyStock(200)
                        .availableStock(1000)
                        .availableStatus("충족")
                        .shortageQty(null)
                        .itemType("구매품")
                        .procurementStartDate(null)
                        .expectedArrivalDate(null)
                        .supplier("동양볼트")
                        .build(),
                MrpOrderDto.builder()
                        .itemId(5L)
                        .itemName("알루미늄 프로파일")
                        .requiredQty(300)
                        .currentStock(100)
                        .safetyStock(50)
                        .availableStock(50)
                        .availableStatus("부족")
                        .shortageQty(250)
                        .itemType("구매품")
                        .procurementStartDate("2024-02-01")
                        .expectedArrivalDate("2024-02-10")
                        .supplier("한국알루미늄")
                        .build()
        );

        return ResponseEntity.ok(ApiResponse.success(response, "자재 조달 계획을 조회했습니다.", HttpStatus.OK));
    }





    @GetMapping("/mrp/planned-orders/detail/{plannedId}")
    @Operation(
            summary = "MRP 계획 주문 상세 조회",
            description = "MRP 계획 주문 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"계획 주문 요청 상세를 조회했습니다.\",\n  \"data\": {\n    \"plannedId\": 1,\n    \"quotationId\": 1,\n    \"quotationCode\": \"Q-2024-001\",\n    \"requester\": \"김철수\",\n    \"department\": \"생산팀\",\n    \"requestDate\": \"2024-01-15\",\n    \"desiredDueDate\": \"2024-01-25\",\n    \"status\": \"승인\",\n    \"orderItems\": [\n      {\n        \"itemId\": 1,\n        \"itemName\": \"강판\",\n        \"quantity\": 500,\n        \"unit\": \"EA\",\n        \"unitPrice\": 5000\n      },\n      {\n        \"itemId\": 2,\n        \"itemName\": \"볼트\",\n        \"quantity\": 100,\n        \"unit\": \"EA\",\n        \"unitPrice\": 500\n      }\n    ],\n    \"totalAmount\": 2500000\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<PlannedOrderDetailDto>> getPlannedOrderDetail(
            @Parameter(name = "plannedId", description = "계획 주문 ID")
            @PathVariable Long plannedId
    ) {
        List<PlannedOrderDetailDto.OrderItemDto> orderItems = Arrays.asList(
                PlannedOrderDetailDto.OrderItemDto.builder()
                        .itemId(1L)
                        .itemName("강판")
                        .quantity(500)
                        .unit("EA")
                        .unitPrice(5000)
                        .build(),
                PlannedOrderDetailDto.OrderItemDto.builder()
                        .itemId(2L)
                        .itemName("볼트")
                        .quantity(100)
                        .unit("EA")
                        .unitPrice(500)
                        .build()
        );

        PlannedOrderDetailDto response = PlannedOrderDetailDto.builder()
                .plannedId(plannedId)
                .quotationId(1L)
                .quotationCode("Q-2024-001")
                .requestId(1L)
                .requester("김철수")
                .department("생산팀")
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
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"계획 주문 요청 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"plannedId\": 1,\n        \"quotationId\": 1,\n        \"quotationCode\": \"Q-2024-001\",\n        \"itemId\": 1,\n        \"itemName\": \"스테인리스 스틸\",\n        \"quantity\": 400,\n        \"procurementStartDate\": \"2024-02-01\",\n        \"status\": \"계획\"\n      },\n      {\n        \"plannedId\": 2,\n        \"quotationId\": 2,\n        \"quotationCode\": \"Q-2024-002\",\n        \"itemId\": 2,\n        \"itemName\": \"구리선\",\n        \"quantity\": 600,\n        \"procurementStartDate\": \"2024-02-02\",\n        \"status\": \"대기\"\n      },\n      {\n        \"plannedId\": 3,\n        \"quotationId\": 3,\n        \"quotationCode\": \"Q-2024-003\",\n        \"itemId\": 3,\n        \"itemName\": \"베어링 6205\",\n        \"quantity\": 100,\n        \"procurementStartDate\": \"2024-02-03\",\n        \"status\": \"승인\"\n      },\n      {\n        \"plannedId\": 4,\n        \"quotationId\": 1,\n        \"quotationCode\": \"Q-2024-001\",\n        \"itemId\": 4,\n        \"itemName\": \"알루미늄 프로파일\",\n        \"quantity\": 300,\n        \"procurementStartDate\": \"2024-02-01\",\n        \"status\": \"반려\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 4,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
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
                        .plannedId(1L)
                        .quotationId(1L)
                        .quotationCode("Q-2024-001")
                        .itemId(1L)
                        .itemName("스테인리스 스틸")
                        .quantity(400)
                        .procurementStartDate("2024-02-01")
                        .status("계획")
                        .build(),
                PlannedOrderListItemDto.builder()
                        .plannedId(2L)
                        .quotationId(2L)
                        .quotationCode("Q-2024-002")
                        .itemId(2L)
                        .itemName("구리선")
                        .quantity(600)
                        .procurementStartDate("2024-02-02")
                        .status("대기")
                        .build(),
                PlannedOrderListItemDto.builder()
                        .plannedId(3L)
                        .quotationId(3L)
                        .quotationCode("Q-2024-003")
                        .itemId(3L)
                        .itemName("베어링 6205")
                        .quantity(100)
                        .procurementStartDate("2024-02-03")
                        .status("승인")
                        .build(),
                PlannedOrderListItemDto.builder()
                        .plannedId(4L)
                        .quotationId(1L)
                        .quotationCode("Q-2024-001")
                        .itemId(4L)
                        .itemName("알루미늄 프로파일")
                        .quantity(300)
                        .procurementStartDate("2024-02-01")
                        .status("반려")
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
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{ \"status\": 200, \"success\": true, \"message\": \"제품별 MPS 조회에 성공했습니다.\", \"data\": { \"productId\": 1, \"productName\": \"도어패널\", \"periodType\": \"WEEK\", \"periods\": [ \"9월 1주차\", \"9월 2주차\", \"9월 3주차\", \"9월 4주차\", \"10월 1주차\", \"10월 2주차\" ], \"demand\": [null, null, null, null, 20, 15], \"requiredInventory\": [null, null, 20, 15, 20, 15], \"productionNeeded\": [null, null, 20, 15, null, null], \"plannedProduction\": [null, null, 20, 15, 20, 15], \"totalPlannedProduction\": 70, \"totalDemand\": 35, \"productionWeeks\": 2, \"averageWeeklyProduction\": 2 } }")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<MpsProductPlanDto>> getMpsPlans(
            @Parameter(name = "itemId", description = "제품 ID")
            @RequestParam(required = false) Long itemId,
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
                .productId(1L)
                .productName("도어패널")
                .periodType("WEEK")
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
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"견적 시뮬레이션이 성공적으로 완료되었습니다.\",\n  \"data\": {\n    \"quotationId\": 1,\n    \"quotationCode\": \"Q-2024-001\",\n    \"customerId\": 1,\n    \"customerName\": \"현대자동차\",\n    \"productId\": 1,\n    \"productName\": \"도어패널\",\n    \"requestQty\": 500,\n    \"requestDueDate\": \"2024-02-15\",\n    \"simulation\": {\n      \"status\": \"FAIL\",\n      \"availableQty\": 130,\n      \"suggestedDueDate\": \"2024-03-10\",\n      \"generatedAt\": \"2025-10-08T12:00:00Z\"\n    },\n    \"shortages\": [\n      {\n        \"itemId\": 1,\n        \"itemName\": \"스테인리스 스틸\",\n        \"requiredQty\": 100,\n        \"stockQty\": 50,\n        \"shortQty\": 50\n      },\n      {\n        \"itemId\": 2,\n        \"itemName\": \"구리선\",\n        \"requiredQty\": 200,\n        \"stockQty\": 150,\n        \"shortQty\": 50\n      },\n      {\n        \"itemId\": 3,\n        \"itemName\": \"베어링\",\n        \"requiredQty\": 50,\n        \"stockQty\": 30,\n        \"shortQty\": 20\n      }\n    ]\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<QuotationSimulationDto>> simulateQuotation(
            @Parameter(name = "quotationId", description = "견적 ID")
            @PathVariable Long quotationId,
            @RequestParam(required = false) Boolean forceRecalculate
    ) {
        List<QuotationSimulationDto.ShortageItemDto> shortages = Arrays.asList(
                QuotationSimulationDto.ShortageItemDto.builder()
                        .itemId(1L)
                        .itemName("스테인리스 스틸")
                        .requiredQty(100)
                        .stockQty(50)
                        .shortQty(50)
                        .build(),
                QuotationSimulationDto.ShortageItemDto.builder()
                        .itemId(2L)
                        .itemName("구리선")
                        .requiredQty(200)
                        .stockQty(150)
                        .shortQty(50)
                        .build(),
                QuotationSimulationDto.ShortageItemDto.builder()
                        .itemId(3L)
                        .itemName("베어링")
                        .requiredQty(50)
                        .stockQty(30)
                        .shortQty(20)
                        .build()
        );

        QuotationSimulationDto response = QuotationSimulationDto.builder()
                .quotationId(quotationId)
                .quotationCode("Q-2024-001")
                .customerId(1L)
                .customerName("현대자동차")
                .productId(1L)
                .productName("도어패널")
                .requestQty(500)
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
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{ \"status\": 200, \"success\": true, \"message\": \"제안 납기 계획을 조회했습니다.\", \"data\": { \"quotationCode\": \"Q-2024-001\", \"customerName\": \"현대자동차\", \"productName\": \"도어패널\", \"confirmedDueDate\": \"2024-03-10\", \"weeks\": [ { \"week\": \"2024-02-3W\", \"demand\": 0, \"requiredStock\": 0, \"productionQty\": 300, \"mps\": 300 }, { \"week\": \"2024-02-4W\", \"demand\": 500, \"requiredStock\": 500, \"productionQty\": 200, \"mps\": 200 }, { \"week\": \"2024-03-1W\", \"demand\": 0, \"requiredStock\": 0, \"productionQty\": 0, \"mps\": 0 }, { \"week\": \"2024-03-2W\", \"demand\": 0, \"requiredStock\": 0, \"productionQty\": 0, \"mps\": 0 } ] } }")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<DueDatePreviewDto>> getQuotationPreview(
            @Parameter(name = "quotationId", description = "견적 ID")
            @PathVariable Long quotationId
    ) {
        List<DueDatePreviewDto.WeekPlanDto> weeks = Arrays.asList(
                DueDatePreviewDto.WeekPlanDto.builder()
                        .week("2024-02-3W")
                        .demand(0)
                        .requiredStock(0)
                        .productionQty(300)
                        .mps(300)
                        .build(),
                DueDatePreviewDto.WeekPlanDto.builder()
                        .week("2024-02-4W")
                        .demand(500)
                        .requiredStock(500)
                        .productionQty(200)
                        .mps(200)
                        .build(),
                DueDatePreviewDto.WeekPlanDto.builder()
                        .week("2024-03-1W")
                        .demand(0)
                        .requiredStock(0)
                        .productionQty(0)
                        .mps(0)
                        .build(),
                DueDatePreviewDto.WeekPlanDto.builder()
                        .week("2024-03-2W")
                        .demand(0)
                        .requiredStock(0)
                        .productionQty(0)
                        .mps(0)
                        .build()
        );

        DueDatePreviewDto response = DueDatePreviewDto.builder()
                .quotationCode("Q-2024-001")
                .customerName("현대자동차")
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
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{ \"status\": 200, \"success\": true, \"message\": \"성공적으로 조회했습니다.\", \"data\": { \"content\": [ { \"workOrderId\": 1, \"workOrderCode\": \"WO-2024-001\", \"productId\": 1, \"productName\": \"산업용 모터 5HP\", \"quantity\": 50, \"unit\": \"EA\", \"quotationId\": 1, \"quotationCode\": \"Q-2024-001\", \"status\": \"IN_PROGRESS\", \"currentOperation\": \"OP30\", \"startDate\": \"2024-01-15\", \"endDate\": \"2024-02-10\", \"progressRate\": 65, \"operationSequence\": [\"OP10\", \"OP20\", \"OP30\", \"OP40\", \"OP50\", \"OP60\"] }, { \"workOrderId\": 2, \"workOrderCode\": \"WO-2024-002\", \"productId\": 2, \"productName\": \"알루미늄 프레임\", \"quantity\": 100, \"unit\": \"EA\", \"quotationId\": 2, \"quotationCode\": \"Q-2024-002\", \"status\": \"PLANNED\", \"currentOperation\": \"OP10\", \"startDate\": \"2024-01-20\", \"endDate\": \"2024-02-15\", \"progressRate\": 0, \"operationSequence\": [\"OP10\", \"OP20\", \"OP30\", \"OP40\", \"OP50\", \"OP60\"] } ], \"page\": 0, \"size\": 20, \"totalElements\": 2, \"totalPages\": 1 } }")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMesWorkOrders(
            @Parameter(name = "status", description = "작업 상태")
            @RequestParam(required = false) String status,
            @Parameter(name = "quotationId", description = "견적 ID")
            @RequestParam(required = false) Long quotationId,
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        List<MesWorkOrderDto> items = Arrays.asList(
                MesWorkOrderDto.builder()
                        .workOrderId(1L)
                        .workOrderCode("WO-2024-001")
                        .productId(1L)
                        .productName("산업용 모터 5HP")
                        .quantity(50)
                        .unit("EA")
                        .quotationId(1L)
                        .quotationCode("Q-2024-001")
                        .status("IN_PROGRESS")
                        .currentOperation("OP30")
                        .startDate("2024-01-15")
                        .endDate("2024-02-10")
                        .progressRate(65)
                        .operationSequence(Arrays.asList("OP10", "OP20", "OP30", "OP40", "OP50", "OP60"))
                        .build(),
                MesWorkOrderDto.builder()
                        .workOrderId(2L)
                        .workOrderCode("WO-2024-002")
                        .productId(2L)
                        .productName("알루미늄 프레임")
                        .quantity(100)
                        .unit("EA")
                        .quotationId(2L)
                        .quotationCode("Q-2024-002")
                        .status("PLANNED")
                        .currentOperation("OP10")
                        .startDate("2024-01-20")
                        .endDate("2024-02-15")
                        .progressRate(0)
                        .operationSequence(Arrays.asList("OP10", "OP20", "OP30", "OP40", "OP50", "OP60"))
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
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{ \"status\": 200, \"success\": true, \"message\": \"작업 지시 상세를 조회했습니다.\", \"data\": { \"workOrderId\": 1001, \"workOrderCode\": \"WO-2024-001\", \"productId\": 1, \"productName\": \"산업용 모터 5HP\", \"quantity\": 50, \"unit\": \"EA\", \"progressPercent\": 65, \"status\": { \"code\": \"IN_PROGRESS\", \"label\": \"진행중\" }, \"plan\": { \"startDate\": \"2024-01-15\", \"dueDate\": \"2024-02-10\" }, \"currentOperation\": \"OP30\", \"operations\": [ { \"operationCode\": \"OP10\", \"operationName\": \"재료 준비\", \"sequence\": 1, \"status\": { \"code\": \"DONE\", \"label\": \"완료\" }, \"startedAt\": \"09:00\", \"finishedAt\": \"10:30\", \"durationHours\": 3.5, \"assignee\": { \"id\": 501, \"name\": \"김작업\" } }, { \"operationCode\": \"OP20\", \"operationName\": \"가공\", \"sequence\": 2, \"status\": { \"code\": \"DONE\", \"label\": \"완료\" }, \"startedAt\": \"10:30\", \"finishedAt\": \"14:00\", \"durationHours\": 3.5, \"assignee\": { \"id\": 501, \"name\": \"김작업\" } }, { \"operationCode\": \"OP30\", \"operationName\": \"조립\", \"sequence\": 3, \"status\": { \"code\": \"IN_PROGRESS\", \"label\": \"진행중\" }, \"startedAt\": \"14:00\", \"finishedAt\": null, \"durationHours\": null, \"assignee\": { \"id\": 501, \"name\": \"김작업\" } }, { \"operationCode\": \"OP40\", \"operationName\": \"테스트\", \"sequence\": 4, \"status\": { \"code\": \"PENDING\", \"label\": \"대기\" }, \"startedAt\": null, \"finishedAt\": null, \"durationHours\": null, \"assignee\": { \"id\": 501, \"name\": \"김작업\" } } ] } }")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<MesWorkOrderDetailDto>> getMesWorkOrderDetail(
            @Parameter(name = "mesId", description = "MES 작업 ID")
            @PathVariable Long mesId
    ) {
        List<MesWorkOrderDetailDto.OperationDto> operations = Arrays.asList(
                MesWorkOrderDetailDto.OperationDto.builder()
                        .operationCode("OP10")
                        .operationName("재료 준비")
                        .sequence(1)
                        .status(MesWorkOrderDetailDto.StatusInfo.builder().code("DONE").label("완료").build())
                        .startedAt("09:00")
                        .finishedAt("10:30")
                        .durationHours(3.5)
                        .assignee(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
                        .build(),
                MesWorkOrderDetailDto.OperationDto.builder()
                        .operationCode("OP20")
                        .operationName("가공")
                        .sequence(2)
                        .status(MesWorkOrderDetailDto.StatusInfo.builder().code("DONE").label("완료").build())
                        .startedAt("10:30")
                        .finishedAt("14:00")
                        .durationHours(3.5)
                        .assignee(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
                        .build(),
                MesWorkOrderDetailDto.OperationDto.builder()
                        .operationCode("OP30")
                        .operationName("조립")
                        .sequence(3)
                        .status(MesWorkOrderDetailDto.StatusInfo.builder().code("IN_PROGRESS").label("진행중").build())
                        .startedAt("14:00")
                        .finishedAt(null)
                        .durationHours(null)
                        .assignee(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
                        .build(),
                MesWorkOrderDetailDto.OperationDto.builder()
                        .operationCode("OP40")
                        .operationName("테스트")
                        .sequence(4)
                        .status(MesWorkOrderDetailDto.StatusInfo.builder().code("PENDING").label("대기").build())
                        .startedAt(null)
                        .finishedAt(null)
                        .durationHours(null)
                        .assignee(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
                        .build()
        );

        MesWorkOrderDetailDto response = MesWorkOrderDetailDto.builder()
                .workOrderId(1001L)
                .workOrderCode("WO-2024-001")
                .productId(1L)
                .productName("산업용 모터 5HP")
                .quantity(50)
                .unit("EA")
                .progressPercent(65)
                .status(MesWorkOrderDetailDto.StatusInfo.builder().code("IN_PROGRESS").label("진행중").build())
                .plan(MesWorkOrderDetailDto.PlanInfo.builder().startDate("2024-01-15").dueDate("2024-02-10").build())
                .currentOperation("OP30")
                .operations(operations)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "작업 지시 상세를 조회했습니다.", HttpStatus.OK));
    }






    @GetMapping("/mes/work-orders/summary")
    @Operation(
            summary = "생산관리 페이지 카드뷰 데이터 조회",
            description = "생산관리 페이지의 요약 데이터를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{ \"status\": 200, \"success\": true, \"message\": \"요약 지표를 조회했습니다.\", \"data\": { \"referenceDate\": \"2025-10-08\", \"generatedAt\": \"2025-10-08T23:30:00+09:00\", \"compare\": { \"inProgress\": { \"prevDay\": { \"delta\": 1, \"pct\": 9.1 }, \"prevMonth\": { \"delta\": 2, \"pct\": 20.0 }, \"prevYear\": { \"delta\": -3, \"pct\": -20.0 } }, \"startedThisMonth\": { \"prevDay\": { \"delta\": 12, \"pct\": 8.3 }, \"prevMonth\": { \"delta\": 23, \"pct\": 17.3 }, \"prevYear\": { \"delta\": 9, \"pct\": 6.1 } }, \"completedThisMonth\": { \"prevDay\": { \"delta\": 5, \"pct\": 6.0 }, \"prevMonth\": { \"delta\": 12, \"pct\": 15.6 }, \"prevYear\": { \"delta\": 4, \"pct\": 4.7 } } } } }")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<MesWorkOrderSummaryDto>> getMesWorkOrderSummary() {
        MesWorkOrderSummaryDto.CompareValueDto prevDayInProgress = MesWorkOrderSummaryDto.CompareValueDto.builder()
                .delta(1)
                .pct(9.1)
                .build();
        MesWorkOrderSummaryDto.CompareValueDto prevMonthInProgress = MesWorkOrderSummaryDto.CompareValueDto.builder()
                .delta(2)
                .pct(20.0)
                .build();
        MesWorkOrderSummaryDto.CompareValueDto prevYearInProgress = MesWorkOrderSummaryDto.CompareValueDto.builder()
                .delta(-3)
                .pct(-20.0)
                .build();

        MesWorkOrderSummaryDto.CompareItemDto inProgress = MesWorkOrderSummaryDto.CompareItemDto.builder()
                .prevDay(prevDayInProgress)
                .prevMonth(prevMonthInProgress)
                .prevYear(prevYearInProgress)
                .build();

        MesWorkOrderSummaryDto.CompareValueDto prevDayStarted = MesWorkOrderSummaryDto.CompareValueDto.builder()
                .delta(12)
                .pct(8.3)
                .build();
        MesWorkOrderSummaryDto.CompareValueDto prevMonthStarted = MesWorkOrderSummaryDto.CompareValueDto.builder()
                .delta(23)
                .pct(17.3)
                .build();
        MesWorkOrderSummaryDto.CompareValueDto prevYearStarted = MesWorkOrderSummaryDto.CompareValueDto.builder()
                .delta(9)
                .pct(6.1)
                .build();

        MesWorkOrderSummaryDto.CompareItemDto startedThisMonth = MesWorkOrderSummaryDto.CompareItemDto.builder()
                .prevDay(prevDayStarted)
                .prevMonth(prevMonthStarted)
                .prevYear(prevYearStarted)
                .build();

        MesWorkOrderSummaryDto.CompareValueDto prevDayCompleted = MesWorkOrderSummaryDto.CompareValueDto.builder()
                .delta(5)
                .pct(6.0)
                .build();
        MesWorkOrderSummaryDto.CompareValueDto prevMonthCompleted = MesWorkOrderSummaryDto.CompareValueDto.builder()
                .delta(12)
                .pct(15.6)
                .build();
        MesWorkOrderSummaryDto.CompareValueDto prevYearCompleted = MesWorkOrderSummaryDto.CompareValueDto.builder()
                .delta(4)
                .pct(4.7)
                .build();

        MesWorkOrderSummaryDto.CompareItemDto completedThisMonth = MesWorkOrderSummaryDto.CompareItemDto.builder()
                .prevDay(prevDayCompleted)
                .prevMonth(prevMonthCompleted)
                .prevYear(prevYearCompleted)
                .build();

        MesWorkOrderSummaryDto.CompareSummaryDto compareSummary = MesWorkOrderSummaryDto.CompareSummaryDto.builder()
                .inProgress(inProgress)
                .startedThisMonth(startedThisMonth)
                .completedThisMonth(completedThisMonth)
                .build();

        MesWorkOrderSummaryDto response = MesWorkOrderSummaryDto.builder()
                .referenceDate("2025-10-08")
                .generatedAt("2025-10-08T23:30:00+09:00")
                .compare(compareSummary)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "요약 지표를 조회했습니다.", HttpStatus.OK));
    }

}
