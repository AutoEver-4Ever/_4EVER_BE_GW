//package org.ever._4ever_be_gw.mockdata.business.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import java.util.Map;
//import org.ever._4ever_be_gw.mockdata.business.dto.customer.CustomerCreateRequestDto;
//import org.ever._4ever_be_gw.mockdata.business.dto.hrm.CreateAuthUserResultDto;
//import org.ever._4ever_be_gw.mockdata.business.service.SdHttpService;
//import org.ever._4ever_be_gw.mockdata.business.service.SdService;
//import org.ever._4ever_be_gw.common.exception.RemoteApiException;
//import org.ever._4ever_be_gw.common.response.ApiResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/business/sd")
//@Tag(name = "영업관리(SD)", description = "영업관리(SD) API")
//public class SdController {
//
//    private final SdHttpService sdHttpService;
//    private final SdService sdService;
//
//    public SdController(SdHttpService sdHttpService, SdService sdService) {
//        this.sdHttpService = sdHttpService;
//        this.sdService = sdService;
//    }
//
//    // SD 통계 조회
//    @GetMapping("/statistics")
//    @Operation(
//            summary = "SD 통계 조회",
//            description = "주간/월간/분기/연간 영업 통계를 조회합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> getStatistics(
//            @Parameter(name = "periods", description = "조회 기간 목록(콤마 구분)")
//            @RequestParam(name = "periods", required = false) String periods
//    ) {
//        return sdHttpService.getDashboardStatistics();
//    }
//
//    // 매출 분석 통계 조회
//    @GetMapping("/analytics/sales")
//    @Operation(
//            summary = "매출 분석 통계 조회",
//            description = "기간별 매출 분석 통계를 조회합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> getSalesAnalytics(
//            @Parameter(description = "시작일(YYYY-MM-DD)")
//            @RequestParam(required = false) String startDate,
//            @Parameter(description = "종료일(YYYY-MM-DD)")
//            @RequestParam(required = false) String endDate
//    ) {
//        return sdHttpService.getSalesAnalytics(startDate, endDate);
//    }
//
//    // 견적 품목 재고 확인
//    @PostMapping("/quotations/inventory/check")
//    @Operation(
//            summary = "견적 품목 재고 확인",
//            description = "요청한 품목들의 현재 재고를 확인하고 부족 여부를 반환합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> checkInventory(
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "request", value = "{\n  \"items\": [\n    { \"itemId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\", \"requiredQuantity\": 10 },\n    { \"itemId\": \"018f2c1a-3bfb-7e21-9b3c-1a2b3c4d5e6f\", \"requiredQuantity\": 5 }\n  ]\n}"))
//            )
//            @RequestBody Map<String, Object> requestBody
//    ) {
//        return sdHttpService.checkInventory(requestBody);
//    }
//
//    @GetMapping("/quotations")
//    @Operation(
//            summary = "견적 목록 조회",
//            description = "견적을 페이지네이션으로 조회합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> getQuotations(
//            @Parameter(description = "시작일(YYYY-MM-DD)")
//            @RequestParam(name = "startDate", required = false) String startDate,
//            @Parameter(description = "종료일(YYYY-MM-DD)")
//            @RequestParam(name = "endDate", required = false) String endDate,
//            @Parameter(description = "상태: PENDING, REVIEW, APPROVAL, REJECTED, ALL")
//            @RequestParam(name = "status", required = false) String status,
//            @Parameter(description = "검색 타입: quotationNumber(견적번호), customerName(고객사명), managerName(담당자명)", example = "quotationNumber")
//            @RequestParam(name = "type", required = false) String type,
//            @Parameter(description = "검색어")
//            @RequestParam(name = "search", required = false) String search,
//            @Parameter(description = "정렬 필드,정렬방향")
//            @RequestParam(name = "sort", required = false) String sort,
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false) Integer page,
//            @Parameter(description = "페이지 크기(최대 200)")
//            @RequestParam(name = "size", required = false) Integer size
//    ) {
//        return sdHttpService.getQuotationList(startDate, endDate, status, type, search, sort, page, size);
//    }
//
//    @GetMapping("/quotations/{quotationId}")
//    @Operation(
//            summary = "견적 상세 조회",
//            description = "견적 단건 상세 정보를 조회합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> getQuotationDetail(
//            @Parameter(description = "견적 ID(quotationId)")
//            @org.springframework.web.bind.annotation.PathVariable("quotationId") String quotationId
//    ) {
//        return sdHttpService.getQuotationDetail(quotationId);
//    }
//
//    @PostMapping("/quotations")
//    @Operation(
//            summary = "신규 견적서 생성",
//            description = "요청 양식만 유효하면 200 OK를 반환합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> createQuotation(
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "request", value = "{\n  \"dueDate\": \"2025-11-01\",\n  \"items\": [\n    {\n      \"itemId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\",\n      \"quantity\": 10,\n      \"unitPrice\": 500000\n    },\n    {\n      \"itemId\": \"018f2c1a-3bfb-7e21-9b3c-1a2b3c4d5e6f\",\n      \"quantity\": 5,\n      \"unitPrice\": 200000\n    }\n  ],\n  \"note\": \"긴급 납품 요청\"\n}"))
//            )
//            @RequestBody Map<String, Object> requestBody
//    ) {
//        return sdHttpService.createQuotation(requestBody);
//    }
//
//    @PostMapping("/quotations/confirm")
//    @Operation(
//            summary = "견적 검토 요청",
//            description = "선택한 견적들에 대해 검토 요청을 수행합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> confirmQuotations(
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "request", value = "{\n  \"quotationId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\"\n}"))
//            )
//            @RequestBody Map<String, Object> requestBody
//    ) {
//        return sdHttpService.confirmQuotation(requestBody);
//    }
//
//    @PostMapping("/quotations/{quotationId}/rejected")
//    @Operation(
//            summary = "견적 거부",
//            description = "견적서를 거부하고 거부 사유를 기록합니다. 견적 상태가 REJECTED로 변경됩니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> rejectQuotation(
//            @Parameter(description = "거부할 견적서 ID (UUID)")
//            @PathVariable("quotationId") String quotationId,
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "request", value = "{\n  \"reason\": \"가격 조건 불일치\"\n}"))
//            )
//            @RequestBody Map<String, Object> requestBody
//    ) {
//        return sdHttpService.rejectQuotation(quotationId, requestBody);
//    }
//
//    @PostMapping("/customers")
//    @Operation(
//            summary = "고객사 등록",
//            description = "고객사 정보를 신규 등록하며, 고객사의 담당자 정보를 통해 담당자(사용자)가 생성됩니다."
//    )
//    public Mono<ResponseEntity<ApiResponse<CreateAuthUserResultDto>>> createCustomer(
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "request",
//                                    value = "{\n  \"companyName\": \"삼성전자\"," +
//                                            "\n  \"businessNumber\": \"123-45-67890\"," +
//                                            "\n  \"ceoName\": \"이재용\"," +
//                                            "\n  \"contactPhone\": \"02-1234-5678\"," +
//                                            "\n  \"contactEmail\": \"contact@samsung.com\"," +
//                                            "\n  \"zipCode\": \"06236\"," +
//                                            "\n  \"address\": \"서울시 강남구 테헤란로 123\"," +
//                                            "\n  \"detailAddress\": \"4층\"," +
//                                            "\n  \"manager\":" +
//                                            " {\n    \"name\": \"김철수\"," +
//                                              "\n    \"mobile\": \"010-1234-5678\"," +
//                                              "\n    \"email\": \"kim@samsung.com\"\n  }," +
//                                                "\n  \"note\": \"주요 고객사, 정기 거래처\"\n}"))
//            )
//            @Valid @RequestBody CustomerCreateRequestDto requestDto
//    ) {
//        return sdService.createCustomer(requestDto)
//            .map(remoteResponse -> {
//                HttpStatus httpStatus = HttpStatus.resolve(remoteResponse.getStatus());
//                if (httpStatus == null) {
//                    httpStatus = HttpStatus.OK;
//                }
//                String message = remoteResponse.getMessage() != null
//                    ? remoteResponse.getMessage()
//                    : "고객사 등록이 완료되었습니다.";
//
//                return ResponseEntity.status(httpStatus)
//                    .body(ApiResponse.success(
//                        remoteResponse.getData(),
//                        message,
//                        httpStatus
//                    ));
//            })
//            .onErrorResume(RemoteApiException.class, ex -> {
//                HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
//                ApiResponse<CreateAuthUserResultDto> failResponse = ApiResponse.fail(
//                    ex.getMessage() != null ? ex.getMessage() : "고객사 등록 중 오류가 발생했습니다.",
//                    status,
//                    ex.getErrors()
//                );
//                return Mono.just(ResponseEntity.status(status).body(failResponse));
//            })
//            .onErrorResume(error -> {
//                ApiResponse<CreateAuthUserResultDto> failResponse = ApiResponse.fail(
//                    "고객사 등록 중 오류가 발생했습니다.",
//                    HttpStatus.INTERNAL_SERVER_ERROR,
//                    error.getMessage()
//                );
//                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failResponse));
//            });
//    }
//
//    @GetMapping("/customers")
//    @Operation(
//            summary = "고객사 목록 조회",
//            description = "고객사를 페이지네이션으로 조회합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> getCustomers(
//            @Parameter(description = "상태: ALL, ACTIVE, DEACTIVE")
//            @RequestParam(name = "status", required = false) String status,
//            @Parameter(description = "검색 타입: customerNumber, customerName, managerName", example = "customerName")
//            @RequestParam(name = "type", required = false) String type,
//            @Parameter(description = "검색어")
//            @RequestParam(name = "search", required = false) String search,
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false) Integer page,
//            @Parameter(description = "페이지 크기(최대 200)")
//            @RequestParam(name = "size", required = false) Integer size
//    ) {
//        return sdHttpService.getCustomerList(status, type, search, page, size);
//    }
//
//    @GetMapping("/customers/{customerId}")
//    @Operation(
//            summary = "고객사 상세 조회",
//            description = "고객사 상세 정보를 조회합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> getCustomerDetail(
//            @Parameter(description = "고객사 ID (UUID)")
//            @org.springframework.web.bind.annotation.PathVariable("customerId") String customerId
//    ) {
//        return sdHttpService.getCustomerDetail(customerId);
//    }
//
//    @org.springframework.web.bind.annotation.PatchMapping("/customers/{customerId}")
//    @Operation(
//            summary = "고객사 정보 수정",
//            description = "고객사 기본/연락/담당자 정보를 수정합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> updateCustomer(
//            @org.springframework.web.bind.annotation.PathVariable("customerId") String customerId,
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "request", value = "{\n  \"customerName\": \"삼성전자\",\n  \"ceoName\": \"이재용\",\n  \"businessNumber\": \"123-45-67890\",\n  \"customerPhone\": \"02-1234-5678\",\n  \"customerEmail\": \"info@samsung.com\",\n  \"baseAddress\": \"서울시 강남구 테헤란로 123\",\n  \"detailAddress\": \"4층\",\n  \"statusCode\": \"ACTIVE\",\n  \"manager\": {\n    \"managerName\": \"김철수\",\n    \"managerPhone\": \"010-1234-5678\",\n    \"managerEmail\": \"manager@samsung.com\"\n  },\n  \"note\": \"주요 거래처\"\n}"))
//            )
//            @RequestBody Map<String, Object> requestBody
//    ) {
//        return sdHttpService.updateCustomer(customerId, requestBody);
//    }
//
//    @org.springframework.web.bind.annotation.DeleteMapping("/customers/{customerId}")
//    @Operation(
//            summary = "고객사 삭제",
//            description = "고객사 정보를 삭제합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> deleteCustomer(
//            @org.springframework.web.bind.annotation.PathVariable("customerId") String customerId
//    ) {
//        return sdHttpService.deleteCustomer(customerId);
//    }
//
//    // -------- Sales Orders (R) --------
//    @GetMapping("/orders")
//    @Operation(
//            summary = "주문서 목록 조회",
//            description = "견적서 승인에 따라 자동 생성된 주문서 목록을 조회합니다. 기간/상태/키워드(주문번호, 고객사명, 고객명) 필터를 지원합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> getSalesOrders(
//            @Parameter(description = "검색 시작일(YYYY-MM-DD)")
//            @RequestParam(name = "startDate", required = false) String startDate,
//            @Parameter(description = "검색 종료일(YYYY-MM-DD)")
//            @RequestParam(name = "endDate", required = false) String endDate,
//            @Parameter(description = "검색어")
//            @RequestParam(name = "search", required = false) String search,
//            @Parameter(description = "검색 타입: salesOrderNumber, customerName, managerName", example = "salesOrderNumber")
//            @RequestParam(name = "type", required = false) String type,
//            @Parameter(description = "상태: ALL, MATERIAL_PREPARATION, IN_PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED")
//            @RequestParam(name = "status", required = false) String status,
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false) Integer page,
//            @Parameter(description = "페이지 크기(최대 200)")
//            @RequestParam(name = "size", required = false) Integer size
//    ) {
//        return sdHttpService.getOrderList(startDate, endDate, search, type, status, page, size);
//    }
//
//    // -------- Sales Order Detail (R) --------
//    @GetMapping("/orders/{salesOrderId}")
//    @Operation(
//            summary = "주문서 상세 조회",
//            description = "주문서 상세 정보를 조회합니다. 주문 정보, 고객 정보, 품목, 총액, 메모를 포함합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> getSalesOrderDetail(
//            @Parameter(description = "주문서 ID (UUID)")
//            @org.springframework.web.bind.annotation.PathVariable("salesOrderId") String salesOrderId
//    ) {
//        return sdHttpService.getOrderDetail(salesOrderId);
//    }
//
//
//    @PostMapping("/quotations/{quotationId}/approve-order")
//    @Operation(
//            summary = "견적 승인 및 주문 전환",
//            description = "재고가 충분한 경우 견적서를 승인(APPROVED)하고 주문서를 출고 준비 완료(READY_FOR_SHIPMENT) 상태로 생성합니다. Request body는 비워두시면 됩니다 (employeeId는 자동으로 주입됩니다)."
//    )
//    public ResponseEntity<ApiResponse<Object>> approveQuotationAndCreateOrder(
//            @Parameter(description = "전환 대상 견적 ID (UUID)")
//            @PathVariable("quotationId") String quotationId,
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = false,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "request", value = "{}"))
//            )
//            @org.springframework.web.bind.annotation.RequestBody(required = false) Map<String, Object> requestBody
//    ) {
//        return sdHttpService.approveQuotation(quotationId, requestBody);
//    }
//}
