package org.ever._4ever_be_gw.scmpp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.scmpp.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/scm-pp")
@Tag(name = "Inventory Management", description = "재고 관리 API")
public class InventoryController {

    @GetMapping("/inventory/shortage/count/critical/statistic")
    @Operation(
            summary = "재고 부족 관리 통계",
            description = "긴급 및 주의 재고 부족 품목 수를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"부족 재고 통계 정보를 조회했습니다.\",\n  \"data\": {\n    \"totalEmergency\": {\n      \"value\": \"8\",\n      \"comparedPrev\": 2\n    },\n    \"totalWarning\": {\n      \"value\": 15,\n      \"comparedPrev\": 3\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<ShortageStatisticDto>> getShortageStatistics() {
        ShortageStatisticDto.TotalItemDto totalEmergency = ShortageStatisticDto.TotalItemDto.builder()
                .value("8")
                .comparedPrev(2)
                .build();
        
        ShortageStatisticDto.TotalItemDto totalWarning = ShortageStatisticDto.TotalItemDto.builder()
                .value("15")
                .comparedPrev(3)
                .build();
        
        ShortageStatisticDto response = ShortageStatisticDto.builder()
                .totalEmergency(totalEmergency)
                .totalWarning(totalWarning)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response, "부족 재고 통계 정보를 조회했습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/inventory/shortage/preview")
    @Operation(
            summary = "부족 재고 간단 조회",
            description = "재고 부족 목록을 간략하게 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"재고 부족 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"itemName\": \"강판 (두께 5mm)\",\n        \"currentStock\": 50,\n        \"currentUnit\": \"EA\",\n        \"safetyStock\": 100,\n        \"safetyUnit\": \"EA\",\n        \"status\": \"긴급\"\n      },\n      {\n        \"itemName\": \"알루미늄 프로파일\",\n        \"currentStock\": 25,\n        \"currentUnit\": \"M\",\n        \"safetyStock\": 50,\n        \"safetyUnit\": \"M\",\n        \"status\": \"주의\"\n      },\n      {\n        \"itemName\": \"스테인리스 파이프\",\n        \"currentStock\": 8,\n        \"currentUnit\": \"EA\",\n        \"safetyStock\": 20,\n        \"safetyUnit\": \"EA\",\n        \"status\": \"긴급\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 3,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getShortageItemsPreview() {
        List<ShortageItemPreviewDto> items = Arrays.asList(
                ShortageItemPreviewDto.builder()
                        .itemName("강판 (두께 5mm)")
                        .currentStock(50)
                        .currentUnit("EA")
                        .safetyStock(100)
                        .safetyUnit("EA")
                        .status("긴급")
                        .build(),
                ShortageItemPreviewDto.builder()
                        .itemName("알루미늄 프로파일")
                        .currentStock(25)
                        .currentUnit("M")
                        .safetyStock(50)
                        .safetyUnit("M")
                        .status("주의")
                        .build(),
                ShortageItemPreviewDto.builder()
                        .itemName("스테인리스 파이프")
                        .currentStock(8)
                        .currentUnit("EA")
                        .safetyStock(20)
                        .safetyUnit("EA")
                        .status("긴급")
                        .build()
        );
        
        PageDto pageInfo = PageDto.builder()
                .number(0)
                .size(10)
                .totalElements(3)
                .totalPages(1)
                .hasNext(false)
                .build();
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", items);
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(ApiResponse.success(response, "재고 부족 목록을 조회했습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/inventory/shortage")
    @Operation(
            summary = "부족재고 목록 조회",
            description = "부족 재고 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"부족 재고 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"itemName\": \"스테인리스 스틸 파이프\",\n        \"itemCode\": \"SS-PIPE-001\",\n        \"category\": \"원자재\",\n        \"currentStock\": 45,\n        \"currentUnit\": \"EA\",\n        \"safetyStock\": 50,\n        \"safetyUnit\": \"EA\",\n        \"unitPrice\": 25000,\n        \"totalValue\": 1125000,\n        \"warehouseName\": \"제1창고\",\n        \"warehouseCode\": \"A-01-01\",\n        \"status\": \"긴급\"\n      },\n      {\n        \"itemName\": \"산업용 모터 5HP\",\n        \"itemCode\": \"MOTOR-5HP-001\",\n        \"category\": \"부품\",\n        \"currentStock\": 5,\n        \"currentUnit\": \"EA\",\n        \"safetyStock\": 10,\n        \"safetyUnit\": \"EA\",\n        \"unitPrice\": 850000,\n        \"totalValue\": 4250000,\n        \"warehouseName\": \"제2창고\",\n        \"warehouseCode\": \"C-01-05\",\n        \"status\": \"긴급\"\n      },\n      {\n        \"itemName\": \"용접봉 3.2mm\",\n        \"itemCode\": \"WELD-ROD-32\",\n        \"category\": \"원자재\",\n        \"currentStock\": 5,\n        \"currentUnit\": \"KG\",\n        \"safetyStock\": 20,\n        \"safetyUnit\": \"KG\",\n        \"unitPrice\": 8000,\n        \"totalValue\": 40000,\n        \"warehouseName\": \"제1창고\",\n        \"warehouseCode\": \"D-03-08\",\n        \"status\": \"긴급\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 3,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getShortageItems(
            @Parameter(name = "status", description = "재고 상태(긴급, 주의)")
            @RequestParam(required = false) String status,
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<ShortageItemDetailDto> items = Arrays.asList(
                ShortageItemDetailDto.builder()
                        .itemName("스테인리스 스틸 파이프")
                        .itemCode("SS-PIPE-001")
                        .category("원자재")
                        .currentStock(45)
                        .currentUnit("EA")
                        .safetyStock(50)
                        .safetyUnit("EA")
                        .unitPrice(25000)
                        .totalValue(1125000)
                        .warehouseName("제1창고")
                        .warehouseCode("A-01-01")
                        .status("긴급")
                        .build(),
                ShortageItemDetailDto.builder()
                        .itemName("산업용 모터 5HP")
                        .itemCode("MOTOR-5HP-001")
                        .category("부품")
                        .currentStock(5)
                        .currentUnit("EA")
                        .safetyStock(10)
                        .safetyUnit("EA")
                        .unitPrice(850000)
                        .totalValue(4250000)
                        .warehouseName("제2창고")
                        .warehouseCode("C-01-05")
                        .status("긴급")
                        .build(),
                ShortageItemDetailDto.builder()
                        .itemName("용접봉 3.2mm")
                        .itemCode("WELD-ROD-32")
                        .category("원자재")
                        .currentStock(5)
                        .currentUnit("KG")
                        .safetyStock(20)
                        .safetyUnit("KG")
                        .unitPrice(8000)
                        .totalValue(40000)
                        .warehouseName("제1창고")
                        .warehouseCode("D-03-08")
                        .status("긴급")
                        .build()
        );
        
        PageDto pageInfo = PageDto.builder()
                .number(page)
                .size(size)
                .totalElements(3)
                .totalPages(1)
                .hasNext(false)
                .build();
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", items);
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(ApiResponse.success(response, "부족 재고 목록을 조회했습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/iv/stock-transfers/statistics/{period}")
    @Operation(
            summary = "기간별 재고이동 통계",
            description = "기간별(today, week, month) 재고이동 통계를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"재고 이력 통계를 조회했습니다.\",\n  \"data\": {\n    \"period\": \"WEEK\",\n    \"inbound\": {\n      \"total\": 695,\n      \"periodCount\": 120\n    },\n    \"outbound\": {\n      \"total\": 610,\n      \"periodCount\": 105\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<StockTransferStatisticDto>> getStockTransferStatistics(
            @Parameter(name = "period", description = "기간(today, week, month)")
            @PathVariable String period
    ) {
        StockTransferStatisticDto response = StockTransferStatisticDto.builder()
                .period(period.toUpperCase())
                .inbound(StockTransferStatisticDto.TransferCountDto.builder()
                        .total(695)
                        .periodCount(120)
                        .build())
                .outbound(StockTransferStatisticDto.TransferCountDto.builder()
                        .total(610)
                        .periodCount(105)
                        .build())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(response, "재고 이력 통계를 조회했습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/iv/stock-transfers/detail")
    @Operation(
            summary = "재고이동 상세 목록 조회",
            description = "재고이동 상세 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"재고 이력 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"type\": \"입고\",\n        \"quantity\": 50,\n        \"unit\": \"EA\",\n        \"itemName\": \"스테인리스 스틸 파이프\",\n        \"workTime\": \"2024-01-15T14:30:00\",\n        \"manager\": \"김구매\",\n        \"locationCode\": \"PO-2024-001\",\n        \"warehouseCode\": \"A-01-01\"\n      },\n      {\n        \"type\": \"출고\",\n        \"quantity\": 200,\n        \"unit\": \"EA\",\n        \"itemName\": \"볼트 M8x20\",\n        \"workTime\": \"2024-01-15T11:20:00\",\n        \"manager\": \"이생산\",\n        \"locationCode\": \"WO-2024-005\",\n        \"warehouseCode\": \"B-02-03\"\n      },\n      {\n        \"type\": \"입고\",\n        \"quantity\": 100,\n        \"unit\": \"M\",\n        \"itemName\": \"알루미늄 프로파일\",\n        \"workTime\": \"2024-01-14T16:45:00\",\n        \"manager\": \"김구매\",\n        \"locationCode\": \"PO-2024-002\",\n        \"warehouseCode\": \"A-02-01\"\n      },\n      {\n        \"type\": \"출고\",\n        \"quantity\": 10,\n        \"unit\": \"EA\",\n        \"itemName\": \"베어링 6205\",\n        \"workTime\": \"2024-01-13T13:30:00\",\n        \"manager\": \"이생산\",\n        \"locationCode\": \"WO-2024-004\",\n        \"warehouseCode\": \"B-01-05\"\n      },\n      {\n        \"type\": \"입고\",\n        \"quantity\": 500,\n        \"unit\": \"EA\",\n        \"itemName\": \"고무 패킹\",\n        \"workTime\": \"2024-01-13T10:15:00\",\n        \"manager\": \"김구매\",\n        \"locationCode\": \"PO-2024-003\",\n        \"warehouseCode\": \"D-01-01\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 6,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStockTransferDetailList(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<StockTransferDetailDto> items = Arrays.asList(
                StockTransferDetailDto.builder()
                        .type("입고")
                        .quantity(50)
                        .unit("EA")
                        .itemName("스테인리스 스틸 파이프")
                        .workTime(LocalDateTime.parse("2024-01-15T14:30:00"))
                        .manager("김구매")
                        .locationCode("PO-2024-001")
                        .warehouseCode("A-01-01")
                        .build(),
                StockTransferDetailDto.builder()
                        .type("출고")
                        .quantity(200)
                        .unit("EA")
                        .itemName("볼트 M8x20")
                        .workTime(LocalDateTime.parse("2024-01-15T11:20:00"))
                        .manager("이생산")
                        .locationCode("WO-2024-005")
                        .warehouseCode("B-02-03")
                        .build(),
                StockTransferDetailDto.builder()
                        .type("입고")
                        .quantity(100)
                        .unit("M")
                        .itemName("알루미늄 프로파일")
                        .workTime(LocalDateTime.parse("2024-01-14T16:45:00"))
                        .manager("김구매")
                        .locationCode("PO-2024-002")
                        .warehouseCode("A-02-01")
                        .build(),
                StockTransferDetailDto.builder()
                        .type("출고")
                        .quantity(10)
                        .unit("EA")
                        .itemName("베어링 6205")
                        .workTime(LocalDateTime.parse("2024-01-13T13:30:00"))
                        .manager("이생산")
                        .locationCode("WO-2024-004")
                        .warehouseCode("B-01-05")
                        .build(),
                StockTransferDetailDto.builder()
                        .type("입고")
                        .quantity(500)
                        .unit("EA")
                        .itemName("고무 패킹")
                        .workTime(LocalDateTime.parse("2024-01-13T10:15:00"))
                        .manager("김구매")
                        .locationCode("PO-2024-003")
                        .warehouseCode("D-01-01")
                        .build()
        );
        
        PageDto pageInfo = PageDto.builder()
                .number(page)
                .size(size)
                .totalElements(6)
                .totalPages(1)
                .hasNext(false)
                .build();
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", items);
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(ApiResponse.success(response, "재고 이력 목록을 조회했습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/iv/stock-transfers")
    @Operation(
            summary = "재고이동 목록 조회",
            description = "재고이동 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"재고 이력 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"type\": \"입고\",\n        \"quantity\": 50,\n        \"unit\": \"EA\",\n        \"itemName\": \"스테인리스 스틸 파이프\",\n        \"workTime\": \"2024-01-15T14:30:00\",\n        \"manager\": \"김구매\"\n      },\n      {\n        \"type\": \"출고\",\n        \"quantity\": 200,\n        \"unit\": \"EA\",\n        \"itemName\": \"볼트 M8x20\",\n        \"workTime\": \"2024-01-15T11:20:00\",\n        \"manager\": \"이생산\"\n      },\n      {\n        \"type\": \"입고\",\n        \"quantity\": 100,\n        \"unit\": \"M\",\n        \"itemName\": \"알루미늄 프로파일\",\n        \"workTime\": \"2024-01-14T16:45:00\",\n        \"manager\": \"김구매\"\n      },\n      {\n        \"type\": \"출고\",\n        \"quantity\": 10,\n        \"unit\": \"EA\",\n        \"itemName\": \"베어링 6205\",\n        \"workTime\": \"2024-01-13T13:30:00\",\n        \"manager\": \"이생산\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 5,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStockTransferList(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        List<StockTransferDto> items = Arrays.asList(
                StockTransferDto.builder()
                        .type("입고")
                        .quantity(50)
                        .unit("EA")
                        .itemName("스테인리스 스틸 파이프")
                        .workTime(LocalDateTime.parse("2024-01-15T14:30:00"))
                        .manager("김구매")
                        .build(),
                StockTransferDto.builder()
                        .type("출고")
                        .quantity(200)
                        .unit("EA")
                        .itemName("볼트 M8x20")
                        .workTime(LocalDateTime.parse("2024-01-15T11:20:00"))
                        .manager("이생산")
                        .build(),
                StockTransferDto.builder()
                        .type("입고")
                        .quantity(100)
                        .unit("M")
                        .itemName("알루미늄 프로파일")
                        .workTime(LocalDateTime.parse("2024-01-14T16:45:00"))
                        .manager("김구매")
                        .build(),
                StockTransferDto.builder()
                        .type("출고")
                        .quantity(10)
                        .unit("EA")
                        .itemName("베어링 6205")
                        .workTime(LocalDateTime.parse("2024-01-13T13:30:00"))
                        .manager("이생산")
                        .build()
        );
        
        PageDto pageInfo = PageDto.builder()
                .number(page)
                .size(size)
                .totalElements(5)
                .totalPages(1)
                .hasNext(false)
                .build();
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", items);
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(ApiResponse.success(response, "재고 이력 목록을 조회했습니다.", HttpStatus.OK));
    }
    
    @PostMapping("/iv/stock-transfers")
    @Operation(
            summary = "창고간 재고 이동 생성",
            description = "창고간 재고 이동을 생성합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"창고간 재고 이동이 완료되었습니다.\",\n  \"data\": null\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Void>> createStockTransfer(
            @RequestBody CreateStockTransferDto request
    ) {
        return ResponseEntity.ok(ApiResponse.success(null, "창고간 재고 이동이 완료되었습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/iv/warehouses/statistic")
    @Operation(
            summary = "창고 관리 통계",
            description = "창고 관리 통계를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"창고 현황을 조회했습니다.\",\n  \"data\": {\n    \"totalWarehouse\": {\n      \"value\": \"15\",\n      \"comparedPrev\": 1\n    },\n    \"inOperationWarehouse\": {\n      \"value\": 13,\n      \"comparedPrev\": 1\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<WarehouseStatisticDto>> getWarehouseStatistics(
            @Parameter(name = "period", description = "기간")
            @RequestParam(required = false) String period
    ) {
        WarehouseStatisticDto response = WarehouseStatisticDto.builder()
                .totalWarehouse(WarehouseStatisticDto.TotalWarehouseDto.builder()
                        .value("15")
                        .comparedPrev(1)
                        .build())
                .inOperationWarehouse(WarehouseStatisticDto.InOperationWarehouseDto.builder()
                        .value(13)
                        .comparedPrev(1)
                        .build())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(response, "창고 현황을 조회했습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/iv/warehouses/{warehouseId}")
    @Operation(
            summary = "창고 상세 조회",
            description = "창고 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"창고 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"warehouseInfo\": {\n      \"warehouseName\": \"제1창고\",\n      \"warehouseCode\": \"WH-A\",\n      \"warehouseType\": \"원자재\",\n      \"warehouseStatus\": \"운영중\",\n      \"location\": \"경기도 안산시 단원구 중앙대로 123\",\n      \"description\": \"원자재 전용 창고입니다.\"\n    },\n    \"manager\": {\n      \"name\": \"김창고\",\n      \"phoneNumber\": \"031-123-4567\",\n      \"email\": \"kim@example.com\"\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<WarehouseDetailDto>> getWarehouseDetail(
            @Parameter(name = "warehouseId", description = "창고 ID")
            @PathVariable Long warehouseId
    ) {
        WarehouseDetailDto response = WarehouseDetailDto.builder()
                .warehouseInfo(WarehouseDetailDto.WarehouseInfoDto.builder()
                        .warehouseName("제1창고")
                        .warehouseCode("WH-A")
                        .warehouseType("원자재")
                        .warehouseStatus("운영중")
                        .location("경기도 안산시 단원구 중앙대로 123")
                        .description("원자재 전용 창고입니다.")
                        .build())
                .manager(WarehouseDetailDto.WarehouseManagerDto.builder()
                        .name("김창고")
                        .phoneNumber("031-123-4567")
                        .email("kim@example.com")
                        .build())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(response, "창고 상세 정보를 조회했습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/iv/warehouses")
    @Operation(
            summary = "창고 목록 조회",
            description = "창고 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"창고 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"warehouseId\": 1,\n        \"warehouseCode\": \"WH-A\",\n        \"warehouseName\": \"제1창고\",\n        \"status\": \"운영중\",\n        \"warehouseType\": \"원자재\",\n        \"location\": \"경기도 안산시 단원구 중앙대로 123\",\n        \"manager\": \"김창고\",\n        \"phone\": \"031-123-4567\"\n      },\n      {\n        \"warehouseId\": 2,\n        \"warehouseCode\": \"WH-B\",\n        \"warehouseName\": \"제2창고\",\n        \"status\": \"운영중\",\n        \"warehouseType\": \"완제품\",\n        \"location\": \"경기도 안산시 상록구 산업로 456\",\n        \"manager\": \"이관리\",\n        \"phone\": \"031-234-5678\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 20,\n      \"totalElements\": 257,\n      \"totalPages\": 13,\n      \"hasNext\": true\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWarehouseList(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        List<WarehouseDto> items = Arrays.asList(
                WarehouseDto.builder()
                        .warehouseId(1L)
                        .warehouseCode("WH-A")
                        .warehouseName("제1창고")
                        .status("운영중")
                        .warehouseType("원자재")
                        .location("경기도 안산시 단원구 중앙대로 123")
                        .manager("김창고")
                        .phone("031-123-4567")
                        .build(),
                WarehouseDto.builder()
                        .warehouseId(2L)
                        .warehouseCode("WH-B")
                        .warehouseName("제2창고")
                        .status("운영중")
                        .warehouseType("완제품")
                        .location("경기도 안산시 상록구 산업로 456")
                        .manager("이관리")
                        .phone("031-234-5678")
                        .build()
        );
        
        PageDto pageInfo = PageDto.builder()
                .number(page)
                .size(size)
                .totalElements(257)
                .totalPages(13)
                .hasNext(true)
                .build();
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", items);
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(ApiResponse.success(response, "창고 목록을 조회했습니다.", HttpStatus.OK));
    }
    
    @PostMapping("/iv/warehouses")
    @Operation(
            summary = "창고 추가",
            description = "새로운 창고를 추가합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"창고가 추가되었습니다.\",\n  \"data\": null\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Void>> createWarehouse(
            @RequestBody CreateWarehouseDto request
    ) {
        return ResponseEntity.ok(ApiResponse.success(null, "창고가 추가되었습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/iv/items/{itemId}")
    @Operation(
            summary = "재고 상세 조회",
            description = "재고 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"재고 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"itemId\": 1,\n    \"itemCode\": \"SS-PIPE-001\",\n    \"itemName\": \"스테인리스 스틸 파이프\",\n    \"category\": \"원자재\",\n    \"supplier\": \"스테인리스코리아\",\n    \"status\": \"정상\",\n    \"currentStock\": 150,\n    \"unit\": \"EA\",\n    \"price\": 25000,\n    \"totalValue\": 3750000,\n    \"warehouseName\": \"제1창고\",\n    \"warehouseCode\": \"A-01-01\",\n    \"lastModified\": \"2024-01-15\",\n    \"description\": \"고품질 스테인리스 스틸 파이프, 내식성 우수\",\n    \"specification\": \"직경 50mm, 두께 3mm, 길이 6m\",\n    \"stockMovements\": [\n      {\n        \"type\": \"입고\",\n        \"quantity\": 50,\n        \"unit\": \"EA\",\n        \"from\": null,\n        \"to\": \"제1창고 (A-01-01)\",\n        \"date\": \"2024-01-15T14:30\",\n        \"manager\": \"김구매\",\n        \"locationCode\": \"TR-2024-001\",\n        \"note\": \"정기 구매입고\"\n      },\n      {\n        \"type\": \"이동\",\n        \"quantity\": 20,\n        \"unit\": \"EA\",\n        \"from\": \"제1창고 (A-01-01)\",\n        \"to\": \"제2창고 (C-02-05)\",\n        \"date\": \"2024-01-12T11:20\",\n        \"manager\": \"이관리\",\n        \"locationCode\": \"TR-2024-002\",\n        \"note\": \"생산 라인 공급을 위한 이동\"\n      },\n      {\n        \"type\": \"출고\",\n        \"quantity\": 30,\n        \"unit\": \"EA\",\n        \"from\": \"제1창고 (A-01-01)\",\n        \"to\": null,\n        \"date\": \"2024-01-10T09:15\",\n        \"manager\": \"박생산\",\n        \"locationCode\": \"WO-2024-001\",\n        \"note\": \"제품 생산을 위한 출고\"\n      }\n    ]\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<ItemDetailDto>> getItemDetail(
            @Parameter(name = "itemId", description = "품목 ID")
            @PathVariable Long itemId
    ) {
        List<ItemDetailDto.StockMovementDto> stockMovements = Arrays.asList(
                ItemDetailDto.StockMovementDto.builder()
                        .type("입고")
                        .quantity(50)
                        .unit("EA")
                        .from(null)
                        .to("제1창고 (A-01-01)")
                        .date("2024-01-15T14:30")
                        .manager("김구매")
                        .locationCode("TR-2024-001")
                        .note("정기 구매입고")
                        .build(),
                ItemDetailDto.StockMovementDto.builder()
                        .type("이동")
                        .quantity(20)
                        .unit("EA")
                        .from("제1창고 (A-01-01)")
                        .to("제2창고 (C-02-05)")
                        .date("2024-01-12T11:20")
                        .manager("이관리")
                        .locationCode("TR-2024-002")
                        .note("생산 라인 공급을 위한 이동")
                        .build(),
                ItemDetailDto.StockMovementDto.builder()
                        .type("출고")
                        .quantity(30)
                        .unit("EA")
                        .from("제1창고 (A-01-01)")
                        .to(null)
                        .date("2024-01-10T09:15")
                        .manager("박생산")
                        .locationCode("WO-2024-001")
                        .note("제품 생산을 위한 출고")
                        .build()
        );
        
        ItemDetailDto response = ItemDetailDto.builder()
                .itemId(itemId)
                .itemCode("SS-PIPE-001")
                .itemName("스테인리스 스틸 파이프")
                .category("원자재")
                .supplier("스테인리스코리아")
                .status("정상")
                .currentStock(150)
                .unit("EA")
                .price(25000)
                .totalValue(3750000)
                .warehouseName("제1창고")
                .warehouseCode("A-01-01")
                .lastModified("2024-01-15")
                .description("고품질 스테인리스 스틸 파이프, 내식성 우수")
                .specification("직경 50mm, 두께 3mm, 길이 6m")
                .stockMovements(stockMovements)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response, "재고 상세 정보를 조회했습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/iv/statistic")
    @Operation(
            summary = "재고관리 통계",
            description = "재고 및 입출고 현황 통계를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"재고 및 입출고 현황을 조회했습니다.\",\n  \"data\": {\n    \"totalStock\": {\n      \"value\": \"₩2.4억\",\n      \"comparedPrev\": 8.2\n    },\n    \"storeComplete\": {\n      \"value\": 156,\n      \"comparedPrev\": 12\n    },\n    \"storePending\": {\n      \"value\": 23,\n      \"comparedPrev\": 5\n    },\n    \"deliveryComplete\": {\n      \"value\": 89,\n      \"comparedPrev\": 7\n    },\n    \"deliveryPending\": {\n      \"value\": 14,\n      \"comparedPrev\": -3\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<InventoryStatisticDto>> getInventoryStatistic() {
        InventoryStatisticDto response = InventoryStatisticDto.builder()
                .totalStock(InventoryStatisticDto.TotalStockDto.builder()
                        .value("₩2.4억")
                        .comparedPrev(8.2)
                        .build())
                .storeComplete(InventoryStatisticDto.InventoryCountDto.builder()
                        .value(156)
                        .comparedPrev(12)
                        .build())
                .storePending(InventoryStatisticDto.InventoryCountDto.builder()
                        .value(23)
                        .comparedPrev(5)
                        .build())
                .deliveryComplete(InventoryStatisticDto.InventoryCountDto.builder()
                        .value(89)
                        .comparedPrev(7)
                        .build())
                .deliveryPending(InventoryStatisticDto.InventoryCountDto.builder()
                        .value(14)
                        .comparedPrev(-3)
                        .build())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response, "재고 및 입출고 현황을 조회했습니다.", HttpStatus.OK));
    }
    
    @GetMapping("/iv/items")
    @Operation(
            summary = "재고 목록 조회",
            description = "재고 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"재고 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"itemId\": 1,\n        \"itemCode\": \"SS-PIPE-001\",\n        \"itemName\": \"스테인리스 스틸 파이프\",\n        \"category\": \"원자재\",\n        \"currentStock\": 150,\n        \"safetyStock\": 50,\n        \"unit\": \"EA\",\n        \"price\": 25000,\n        \"totalValue\": 3750000,\n        \"warehouseName\": \"제1창고\",\n        \"warehouseType\": \"A-01-01\",\n        \"status\": \"정상\"\n      },\n      {\n        \"itemId\": 1,\n        \"itemCode\": \"BOLT-M8-20\",\n        \"itemName\": \"볼트 M8x20\",\n        \"category\": \"부품\",\n        \"currentStock\": 25,\n        \"safetyStock\": 100,\n        \"unit\": \"EA\",\n        \"price\": 500,\n        \"totalValue\": 12500,\n        \"warehouseName\": \"제3창고\",\n        \"warehouseType\": \"B-02-15\",\n        \"status\": \"부족\"\n      },\n      {\n        \"itemId\": \"MOTOR-5HP-001\",\n        \"itemName\": \"산업용 모터 5HP\",\n        \"category\": \"완제품\",\n        \"currentStock\": 8,\n        \"safetyStock\": 5,\n        \"unit\": \"EA\",\n        \"price\": 850000,\n        \"totalValue\": 6800000,\n        \"warehouseName\": \"제2창고\",\n        \"warehouseType\": \"C-01-05\",\n        \"status\": \"정상\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 6,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventoryItems(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size,
            @Parameter(name = "category", description = "카테고리")
            @RequestParam(required = false) String category,
            @Parameter(name = "status", description = "상태")
            @RequestParam(required = false) String status,
            @Parameter(name = "warehouse", description = "창고")
            @RequestParam(required = false) String warehouse,
            @Parameter(name = "itemName", description = "품목명")
            @RequestParam(required = false) String itemName
    ) {
        List<InventoryItemDto> items = Arrays.asList(
                InventoryItemDto.builder()
                        .itemId(1L)
                        .itemCode("SS-PIPE-001")
                        .itemName("스테인리스 스틸 파이프")
                        .category("원자재")
                        .currentStock(150)
                        .safetyStock(50)
                        .unit("EA")
                        .price(25000)
                        .totalValue(3750000)
                        .warehouseName("제1창고")
                        .warehouseType("A-01-01")
                        .status("정상")
                        .build(),
                InventoryItemDto.builder()
                        .itemId(2L)
                        .itemCode("BOLT-M8-20")
                        .itemName("볼트 M8x20")
                        .category("부품")
                        .currentStock(25)
                        .safetyStock(100)
                        .unit("EA")
                        .price(500)
                        .totalValue(12500)
                        .warehouseName("제3창고")
                        .warehouseType("B-02-15")
                        .status("부족")
                        .build(),
                InventoryItemDto.builder()
                        .itemId(3L)
                        .itemCode("MOTOR-5HP-001")
                        .itemName("산업용 모터 5HP")
                        .category("완제품")
                        .currentStock(8)
                        .safetyStock(5)
                        .unit("EA")
                        .price(850000)
                        .totalValue(6800000)
                        .warehouseName("제2창고")
                        .warehouseType("C-01-05")
                        .status("정상")
                        .build()
        );
        
        PageDto pageInfo = PageDto.builder()
                .number(page)
                .size(size)
                .totalElements(6)
                .totalPages(1)
                .hasNext(false)
                .build();
                
        Map<String, Object> response = new HashMap<>();
        response.put("content", items);
        response.put("page", pageInfo);
        
        return ResponseEntity.ok(ApiResponse.success(response, "재고 목록을 조회했습니다.", HttpStatus.OK));
    }

    @PatchMapping("/sales-orders/{salesOrderId}/status")
    @Operation(
            summary = "출고 준비 완료로 상태 변경",
            description = "주문 상태를 출고 준비 완료로 변경합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"주문 상태가 출고준비완료로 변경되었습니다.\",\n  \"data\": {\n    \"salesOrderId\": 1,\n    \"salesOrderCode\": \"SO-2024-001\",\n    \"status\": \"출고준비완료\"\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<SalesOrderStatusDto>> updateOrderStatus(
            @Parameter(name = "salesOrderId", description = "주문 ID")
            @PathVariable Long salesOrderId
    ) {
        SalesOrderStatusDto response = SalesOrderStatusDto.builder()
                .salesOrderId(salesOrderId)
                .salesOrderCode("SO-2024-001")
                .status("출고준비완료")
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "주문 상태가 출고준비완료로 변경되었습니다.", HttpStatus.OK));
    }

    @GetMapping("/sales-orders/ready-to-ship/{salesOrderId}")
    @Operation(
            summary = "출고 준비완료 상세보기",
            description = "출고 준비 완료된 주문의 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"출고 준비 완료 주문 상세를 조회했습니다.\",\n  \"data\": {\n    \"salesOrderId\": 1,\n    \"salesOrderCode\": \"SO-2024-002\",\n    \"customer\": \"현대건설\",\n    \"dueDate\": \"2024-01-22\",\n    \"status\": \"출고 준비완료\",\n    \"orderItems\": [\n      {\n        \"itemName\": \"볼트 M8x20\",\n        \"quantity\": 500,\n        \"unit\": \"EA\"\n      },\n      {\n        \"itemName\": \"베어링 6205\",\n        \"quantity\": 20,\n        \"unit\": \"EA\"\n      }\n    ]\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<ReadyToShipDetailDto>> getReadyToShipDetail(
            @Parameter(name = "salesOrderId", description = "주문 ID")
            @PathVariable Long salesOrderId
    ) {
        List<ReadyToShipDetailDto.OrderItemDto> orderItems = Arrays.asList(
                ReadyToShipDetailDto.OrderItemDto.builder()
                        .itemName("볼트 M8x20")
                        .quantity(500)
                        .unit("EA")
                        .build(),
                ReadyToShipDetailDto.OrderItemDto.builder()
                        .itemName("베어링 6205")
                        .quantity(20)
                        .unit("EA")
                        .build()
        );

        ReadyToShipDetailDto response = ReadyToShipDetailDto.builder()
                .salesOrderId(salesOrderId)
                .salesOrderCode("SO-2024-002")
                .customer("현대건설")
                .dueDate("2024-01-22")
                .status("출고 준비완료")
                .orderItems(orderItems)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "출고 준비 완료 주문 상세를 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/sales-orders/ready-to-ship")
    @Operation(
            summary = "출고 준비 완료 목록",
            description = "출고 준비 완료된 주문 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"출고 준비 완료 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"salesOrderId\": 1,\n        \"salesOrderCode\": \"SO-2024-001\",\n        \"customer\": \"대한제철\",\n        \"orderDate\": \"2024-01-10\",\n        \"dueDate\": \"2024-01-20\",\n        \"totalAmount\": 15750000,\n        \"status\": \"출고 준비완료\"\n      },\n      {\n        \"salesOrderId\": 2,\n        \"salesOrderCode\": \"SO-2024-002\",\n        \"customer\": \"신성기공\",\n        \"orderDate\": \"2024-01-12\",\n        \"dueDate\": \"2024-01-25\",\n        \"totalAmount\": 8700000,\n        \"status\": \"출고 준비완료\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 2,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReadyToShipList(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<ReadyToShipOrderDto> items = Arrays.asList(
                ReadyToShipOrderDto.builder()
                        .salesOrderId(1L)
                        .salesOrderCode("SO-2024-001")
                        .customer("대한제철")
                        .orderDate("2024-01-10")
                        .dueDate("2024-01-20")
                        .totalAmount(15750000)
                        .status("출고 준비완료")
                        .build(),
                ReadyToShipOrderDto.builder()
                        .salesOrderId(2L)
                        .salesOrderCode("SO-2024-002")
                        .customer("신성기공")
                        .orderDate("2024-01-12")
                        .dueDate("2024-01-25")
                        .totalAmount(8700000)
                        .status("출고 준비완료")
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

        return ResponseEntity.ok(ApiResponse.success(response, "출고 준비 완료 목록을 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/sales-orders/production/{salesOrderId}")
    @Operation(
            summary = "생산중 상세보기",
            description = "생산 중인 주문의 상세 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"주문 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"salesOrderId\": 1,\n    \"salesOrderCode\": \"SO-2024-001\",\n    \"customer\": \"대한제철\",\n    \"dueDate\": \"2024-01-20\",\n    \"status\": \"생산중\",\n    \"orderItems\": [\n      {\n        \"itemName\": \"스테인리스 파이프\",\n        \"quantity\": 100,\n        \"unit\": \"EA\"\n      },\n      {\n        \"itemName\": \"알루미늄 프로파일\",\n        \"quantity\": 50,\n        \"unit\": \"M\"\n      }\n    ]\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<ReadyToShipDetailDto>> getProductionOrderDetail(
            @Parameter(name = "salesOrderId", description = "주문 ID")
            @PathVariable Long salesOrderId
    ) {
        List<ReadyToShipDetailDto.OrderItemDto> orderItems = Arrays.asList(
                ReadyToShipDetailDto.OrderItemDto.builder()
                        .itemName("스테인리스 파이프")
                        .quantity(100)
                        .unit("EA")
                        .build(),
                ReadyToShipDetailDto.OrderItemDto.builder()
                        .itemName("알루미늄 프로파일")
                        .quantity(50)
                        .unit("M")
                        .build()
        );

        ReadyToShipDetailDto response = ReadyToShipDetailDto.builder()
                .salesOrderId(salesOrderId)
                .salesOrderCode("SO-2024-001")
                .customer("대한제철")
                .dueDate("2024-01-20")
                .status("생산중")
                .orderItems(orderItems)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response, "주문 상세 정보를 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/sales-orders/production")
    @Operation(
            summary = "생산중 목록조회",
            description = "생산 중인 주문 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"생산중 주문 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"salesOrderId\": 1,\n        \"salesOrderCode\": \"SO-2024-001\",\n        \"customer\": \"대한제철\",\n        \"orderDate\": \"2024-01-10\",\n        \"dueDate\": \"2024-01-20\",\n        \"orderAmount\": 15750000,\n        \"currency\": \"KRW\",\n        \"status\": \"생산중\"\n      },\n      {\n        \"salesOrderId\": 3,\n        \"salesOrderCode\": \"SO-2024-003\",\n        \"customer\": \"삼성물산\",\n        \"orderDate\": \"2024-01-12\",\n        \"dueDate\": \"2024-01-25\",\n        \"orderAmount\": 8900000,\n        \"currency\": \"KRW\",\n        \"status\": \"생산중\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 2,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductionOrderList(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<ProductionOrderDto> items = Arrays.asList(
                ProductionOrderDto.builder()
                        .salesOrderId(1L)
                        .salesOrderCode("SO-2024-001")
                        .customer("대한제철")
                        .orderDate("2024-01-10")
                        .dueDate("2024-01-20")
                        .orderAmount(15750000)
                        .currency("KRW")
                        .status("생산중")
                        .build(),
                ProductionOrderDto.builder()
                        .salesOrderId(3L)
                        .salesOrderCode("SO-2024-003")
                        .customer("삼성물산")
                        .orderDate("2024-01-12")
                        .dueDate("2024-01-25")
                        .orderAmount(8900000)
                        .currency("KRW")
                        .status("생산중")
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

        return ResponseEntity.ok(ApiResponse.success(response, "생산중 주문 목록을 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/purchase-orders/received")
    @Operation(
            summary = "입고완료 목록 조회",
            description = "입고 완료된 발주 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"입고 완료 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"purchaseOrderCode\": \"PO-2024-003\",\n        \"supplier\": \"부산금속상사\",\n        \"orderDate\": \"2024-01-08\",\n        \"receivedDate\": \"2024-01-13\",\n        \"totalAmount\": 3120000,\n        \"status\": \"입고 완료\"\n      },\n      {\n        \"purchaseOrderCode\": \"PO-2024-004\",\n        \"supplier\": \"강남기계\",\n        \"orderDate\": \"2024-01-05\",\n        \"receivedDate\": \"2024-01-10\",\n        \"totalAmount\": 975000,\n        \"status\": \"입고 완료\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 2,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReceivedPurchaseOrders(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<ReceivedPurchaseOrderDto> items = Arrays.asList(
                ReceivedPurchaseOrderDto.builder()
                        .purchaseOrderCode("PO-2024-003")
                        .supplier("부산금속상사")
                        .orderDate("2024-01-08")
                        .receivedDate("2024-01-13")
                        .totalAmount(3120000)
                        .status("입고 완료")
                        .build(),
                ReceivedPurchaseOrderDto.builder()
                        .purchaseOrderCode("PO-2024-004")
                        .supplier("강남기계")
                        .orderDate("2024-01-05")
                        .receivedDate("2024-01-10")
                        .totalAmount(975000)
                        .status("입고 완료")
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

        return ResponseEntity.ok(ApiResponse.success(response, "입고 완료 목록을 조회했습니다.", HttpStatus.OK));
    }

    @GetMapping("/purchase-orders/pending")
    @Operation(
            summary = "입고대기 목록 조회",
            description = "입고 대기 중인 발주 목록을 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"입고 대기 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"purchaseOrderCode\": \"PO-2024-001\",\n        \"supplier\": \"스테인리스코리아\",\n        \"orderDate\": \"2024-01-10\",\n        \"dueDate\": \"2024-01-16\",\n        \"totalAmount\": 4250000,\n        \"status\": \"입고 대기\"\n      },\n      {\n        \"purchaseOrderCode\": \"PO-2024-002\",\n        \"supplier\": \"금속유통\",\n        \"orderDate\": \"2024-01-11\",\n        \"dueDate\": \"2024-01-17\",\n        \"totalAmount\": 1860000,\n        \"status\": \"입고 대기\"\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 2,\n      \"totalPages\": 1,\n      \"hasNext\": false\n    }\n  }\n}")
                            )
                    )
            }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPendingPurchaseOrders(
            @Parameter(name = "page", description = "페이지 번호")
            @RequestParam(required = false, defaultValue = "0") int page,
            @Parameter(name = "size", description = "페이지 크기")
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        List<PendingPurchaseOrderDto> items = Arrays.asList(
                PendingPurchaseOrderDto.builder()
                        .purchaseOrderCode("PO-2024-001")
                        .supplier("스테인리스코리아")
                        .orderDate("2024-01-10")
                        .dueDate("2024-01-16")
                        .totalAmount(4250000)
                        .status("입고 대기")
                        .build(),
                PendingPurchaseOrderDto.builder()
                        .purchaseOrderCode("PO-2024-002")
                        .supplier("금속유통")
                        .orderDate("2024-01-11")
                        .dueDate("2024-01-17")
                        .totalAmount(1860000)
                        .status("입고 대기")
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

        return ResponseEntity.ok(ApiResponse.success(response, "입고 대기 목록을 조회했습니다.", HttpStatus.OK));
    }
}
