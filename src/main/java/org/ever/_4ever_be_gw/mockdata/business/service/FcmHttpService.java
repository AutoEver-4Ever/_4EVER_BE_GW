//package org.ever._4ever_be_gw.mockdata.business.service;
//
//import org.ever._4ever_be_gw.common.response.ApiResponse;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Map;
//
//public interface FcmHttpService {
//
//    /**
//     * 재무관리 통계 조회
//     */
//    ResponseEntity<ApiResponse<Object>> getFcmStatistics(String periods);
//
//    /**
//     * 매입 전표 목록 조회 (AP)
//     */
//    ResponseEntity<ApiResponse<Object>> getApInvoices(
//            String company, String startDate, String endDate, Integer page, Integer size);
//
//    /**
//     * 매출 전표 목록 조회 (AR)
//     */
//    ResponseEntity<ApiResponse<Object>> getArInvoices(
//            String company, String startDate, String endDate, Integer page, Integer size);
//
//    /**
//     * 매입 전표 상세 조회 (AP)
//     */
//    ResponseEntity<ApiResponse<Object>> getApInvoiceDetail(String invoiceId);
//
//    /**
//     * 매출 전표 상세 조회 (AR)
//     */
//    ResponseEntity<ApiResponse<Object>> getArInvoiceDetail(String invoiceId);
//
//    /**
//     * 매입 전표 수정 (AP)
//     */
//    ResponseEntity<ApiResponse<Object>> patchApInvoice(String invoiceId, Map<String, Object> requestBody);
//
//    /**
//     * 매출 전표 수정 (AR)
//     */
//    ResponseEntity<ApiResponse<Object>> patchArInvoice(String invoiceId, Map<String, Object> requestBody);
//
//    /**
//     * 미수 처리 완료 (AR)
//     */
//    ResponseEntity<ApiResponse<Object>> completeReceivable(String invoiceId);
//
//    /**
//     * 매입 전표 미수 처리 요청 (AP)
//     */
//    ResponseEntity<ApiResponse<Object>> requestApReceivable(String invoiceId);
//}
