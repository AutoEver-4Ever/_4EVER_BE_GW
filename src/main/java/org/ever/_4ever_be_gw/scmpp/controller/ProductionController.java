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
}
