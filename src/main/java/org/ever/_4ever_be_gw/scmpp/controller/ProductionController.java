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


}
