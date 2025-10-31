//package org.ever._4ever_be_gw.mockdata.business.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.ever._4ever_be_gw.mockdata.business.dto.InvoiceUpdateRequestDto;
//import org.ever._4ever_be_gw.mockdata.business.service.FcmHttpService;
//import org.ever._4ever_be_gw.common.response.ApiResponse;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/business/fcm")
//@RequiredArgsConstructor
//@Slf4j
//@Tag(name = "재무관리(FCM)", description = "재무 관리 API")
//public class FcmController {
//
//	private final FcmHttpService fcmHttpService;
//
//	// ==================== 재무 관리 통계 ====================
//
//	@GetMapping("/statictics")
//	@Operation(
//		summary = "FCM 통계 조회",
//		description = "기간별 재무 관리 통계를 조회합니다."
//	)
//	public ResponseEntity<ApiResponse<Object>> getStatistics(
//		@Parameter(description = "조회 기간 목록(콤마 구분)")
//		@RequestParam(name = "periods", required = false) String periods
//	) {
//		log.info("FCM 통계 조회 API 호출 - periods: {}", periods);
//		return fcmHttpService.getFcmStatistics(periods);
//	}
//
//	// ==================== 전표 목록 조회 (AP: 매입, AR: 매출) ====================
//
//	@GetMapping("/invoice/ap")
//	@Operation(
//		summary = "매입 전표 목록 조회",
//		description = "매입(AP) 전표 목록을 조회합니다."
//	)
//	public ResponseEntity<ApiResponse<Object>> getApInvoices(
//		@Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
//		@Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
//		@Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
//		@Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
//		@Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
//	) {
//		log.info("매입 전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
//				company, startDate, endDate, page, size);
//		return fcmHttpService.getApInvoices(company, startDate, endDate, page, size);
//	}
//
//    @GetMapping("/invoice/ar")
//	@Operation(
//		summary = "매출 전표 목록 조회",
//		description = "매출(AR) 전표 목록을 조회합니다."
//	)
//	public ResponseEntity<ApiResponse<Object>> getArInvoices(
//		@Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
//		@Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
//		@Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
//		@Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
//		@Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
//	) {
//		log.info("매출 전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
//				company, startDate, endDate, page, size);
//		return fcmHttpService.getArInvoices(company, startDate, endDate, page, size);
//	}
//
//	// ==================== 전표 상세 조회 ====================
//
//    @GetMapping("/invoice/ap/{invoiceId}")
//	@Operation(
//		summary = "매입 전표 상세 조회",
//		description = "매입(AP) 전표 상세 정보를 조회합니다."
//	)
//	public ResponseEntity<ApiResponse<Object>> getApInvoiceDetail(
//		@Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId
//	) {
//		log.info("매입 전표 상세 조회 API 호출 - invoiceId: {}", invoiceId);
//		return fcmHttpService.getApInvoiceDetail(invoiceId);
//	}
//
//    @GetMapping("/invoice/ar/{invoiceId}")
//	@Operation(
//		summary = "매출 전표 상세 조회",
//		description = "매출(AR) 전표 상세 정보를 조회합니다."
//	)
//	public ResponseEntity<ApiResponse<Object>> getArInvoiceDetail(
//		@Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId
//	) {
//		log.info("매출 전표 상세 조회 API 호출 - invoiceId: {}", invoiceId);
//		return fcmHttpService.getArInvoiceDetail(invoiceId);
//	}
//
//	// ==================== 전표 수정 ====================
//
//    @PatchMapping("/invoice/ap/{invoiceId}")
//    @Operation(summary = "매입 전표 수정", description = "매입(AP) 전표를 수정합니다.")
//    public ResponseEntity<ApiResponse<Object>> patchApInvoice(
//        @Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId,
//        @Valid @RequestBody InvoiceUpdateRequestDto request
//    ) {
//        log.info("매입 전표 수정 API 호출 - invoiceId: {}, request: {}", invoiceId, request);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        if (request.getStatus() != null) requestBody.put("status", request.getStatus());
//        if (request.getDueDate() != null) requestBody.put("dueDate", request.getDueDate());
//        if (request.getMemo() != null) requestBody.put("memo", request.getMemo());
//
//        return fcmHttpService.patchApInvoice(invoiceId, requestBody);
//    }
//
//    @PatchMapping("/invoice/ar/{invoiceId}")
//    @Operation(summary = "매출 전표 수정", description = "매출(AR) 전표를 수정합니다.")
//    public ResponseEntity<ApiResponse<Object>> patchArInvoice(
//        @Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId,
//        @Valid @RequestBody InvoiceUpdateRequestDto request
//    ) {
//        log.info("매출 전표 수정 API 호출 - invoiceId: {}, request: {}", invoiceId, request);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        if (request.getStatus() != null) requestBody.put("status", request.getStatus());
//        if (request.getDueDate() != null) requestBody.put("dueDate", request.getDueDate());
//        if (request.getMemo() != null) requestBody.put("memo", request.getMemo());
//
//        return fcmHttpService.patchArInvoice(invoiceId, requestBody);
//    }
//
//    // ==================== 미수 처리 ====================
//
//    @PostMapping("/invoice/ar/{invoiceId}/receivable/complete")
//    @Operation(
//        summary = "매출 전표 미수 처리 완료",
//        description = "미납/확인요청 상태의 매출(AR) 전표에 대해 미수 처리를 완료합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> completeReceivable(
//        @Parameter(description = "매출 전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId
//    ) {
//        log.info("미수 처리 완료 API 호출 - invoiceId: {}", invoiceId);
//        return fcmHttpService.completeReceivable(invoiceId);
//    }
//
//    // ==================== 매입 전표 미수 처리 요청 ====================
//
//    @PostMapping("/invoice/ap/receivable/request")
//    @Operation(
//        summary = "매입 전표 미수 처리 요청",
//        description = "매입(AP) 전표에 대해 공급사에 미수 처리 요청을 발송합니다."
//    )
//    public ResponseEntity<ApiResponse<Object>> requestApReceivable(
//        @Parameter(description = "매입 전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab", required = true) @RequestParam("invoiceId") String invoiceId
//    ) {
//        log.info("매입 전표 미수 처리 요청 API 호출 - invoiceId: {}", invoiceId);
//        return fcmHttpService.requestApReceivable(invoiceId);
//    }
//}
