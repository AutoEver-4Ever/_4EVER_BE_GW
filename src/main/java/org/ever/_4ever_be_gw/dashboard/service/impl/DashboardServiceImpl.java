package org.ever._4ever_be_gw.dashboard.service.impl;

import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.service.FcmHttpService;
import org.ever._4ever_be_gw.business.service.HrmHttpService;
import org.ever._4ever_be_gw.business.service.SdHttpService;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.dashboard.service.DashboardService;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SdHttpService sdHttpService;          // 영업관리
    private final FcmHttpService fcmHttpService;        // 재무관리
    private final MmHttpService mmHttpService;          // 구매관리
    private final HrmHttpService hrmHttpService;        // 인적자원관리
    private final ImHttpService imHttpService;          // 재고관리
    private final PpHttpService ppHttpService;          // 생산관리


    private static final int DEFAULT_SIZE = 5;

    @Override
    public DashboardWorkflowResponseDto getDashboardWorkflow(
            @AuthenticationPrincipal  EverUserPrincipal principal,
            @RequestParam(defaultValue = "5", required = false) Integer size
    ) {
        if (principal == null) throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);

        // 사용자 정보 추출
        String userId = principal.getUserId();
        String userType = principal.getUserType();
        String userRole = principal.getUserRole();

        // 공급사 워크 플로우
        if (userType.equalsIgnoreCase("SUPPLIER")) {
            // 발주서 목록 요청 (대시보드 전용)
            ResponseEntity<ApiResponse<Object>> supplierQuotationResponse = sdHttpService.getDashboardSupplierQuotationList(userId, size);

            // 매출 전표 요청(공급사 입장에서 매입 전표는 매출 전표)
            ResponseEntity<ApiResponse<Object>> supplierInvoiceResponse = fcmHttpService.getDashboardSupplierInvoiceList(userId, size);
        } else if (userType.equalsIgnoreCase("CUSTOMER")) {
            // 견적 목록 요청
            ResponseEntity<ApiResponse<Object>> quotationResponse = sdHttpService.getDashboardCustomerQuotationList(userId, size);

            // 매입 전표 요청(고객사 입장에서 매출 전표는 매입 전표)
            ResponseEntity<ApiResponse<Object>> customerInvoiceResponse = fcmHttpService.getDashboardCustomerInvoiceList(userId, size);
        } else {
            if (userRole.startsWith("MM")) {        // 구매 관리 대시보드
                // TODO: MM 전용 HttpService 연동 예정
                ResponseEntity<ApiResponse<Object>> mmPurchaseRequestResponse = null;   // mmHttpService.getDashboardPurchaseRequestList(userId, size)
                ResponseEntity<ApiResponse<Object>> mmPurchaseOrderResponse   = null;   // mmHttpService.getDashboardPurchaseOrderList(userId, size)

            } else if (userRole.startsWith("SD")) { // 영업관리 대시보드
                // TODO: SD 전용 HttpService 연동 예정
                ResponseEntity<ApiResponse<Object>> sdCustomerQuotationResponse = null; // sdHttpService.getDashboardCustomerQuotationList(userId, size)
                ResponseEntity<ApiResponse<Object>> sdSupplierQuotationResponse = null; // sdHttpService.getDashboardSupplierQuotationList(userId, size)

            } else if (userRole.startsWith("FCM")) {   // 재무관리 대시보드
                // TODO: FCM 전용 HttpService 연동 예정(자사 관점)
                ResponseEntity<ApiResponse<Object>> fcmArListResponse = null;          // fcmHttpService.getDashboardCompanyArList(userId, size)
                ResponseEntity<ApiResponse<Object>> fcmApListResponse = null;          // fcmHttpService.getDashboardCompanyApList(userId, size)

            } else if (userRole.startsWith("IM")) {     // 재고관리 대시보드
                // TODO: IM 전용 HttpService 연동 예정
                ResponseEntity<ApiResponse<Object>> imInboundListResponse  = null;     // imHttpService.getDashboardInboundList(userId, size)
                ResponseEntity<ApiResponse<Object>> imOutboundListResponse = null;     // imHttpService.getDashboardOutboundList(userId, size)

            } else if (userRole.startsWith("HRM")) {    // 인적자원관리 대시보드
                // TODO: HRM 전용 HttpService 연동 예정
                ResponseEntity<ApiResponse<Object>> hrmAttendanceListResponse  = null; // hrmHttpService.getDashboardAttendanceList(userId, size)
                ResponseEntity<ApiResponse<Object>> hrmLeaveRequestListResponse = null;// hrmHttpService.getDashboardLeaveRequestList(userId, size)

            } else {        // 생산관리 대시보드
                // TODO: PP 전용 HttpService 연동 예정
                ResponseEntity<ApiResponse<Object>> ppToProductionQuotationResponse = null; // ppHttpService.getDashboardQuotationsToProduction(userId, size)
                ResponseEntity<ApiResponse<Object>> ppInProgressResponse            = null; // ppHttpService.getDashboardProductionInProgress(userId, size)
            }
        }

        return null;
    }
}
