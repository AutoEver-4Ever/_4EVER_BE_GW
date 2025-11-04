package org.ever._4ever_be_gw.dashboard.service.impl;

import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.service.FcmHttpService;
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
            // 주문서 목록 요청
            ResponseEntity<ApiResponse<Object>> orderResponse = sdHttpService.getDashboardSupplierOrderList(userId, size);

            // 매출 전표 요청(공급사 입장에서 매입 전표는 매출 전표)
            ResponseEntity<ApiResponse<Object>> supplierInvoiceResponse = fcmHttpService.getDashboardSupplierInvoiceList(userId, size);
        } else if (userType.equalsIgnoreCase("CUSTOMER")) {
            // 견적 목록 요청
            ResponseEntity<ApiResponse<Object>> quotationResponse = sdHttpService.getDashboardCustomerQuotationList(userId, userType);

            // 매입 전표 요청(고객사 입장에서 매출 전표는 매입 전표)
            ResponseEntity<ApiResponse<Object>> customerInvoiceResponse = fcmHttpService.getDashboardCustomerInvoiceList(userId, userType);
        } else {
            // 구매 관리
            if (userRole.startsWith("MM")) {

                // 영업관리
            } else if (userRole.startsWith("SD")) {

                // 재무관리
            } else if (userRole.startsWith("FCM")) {

                // 재고관리
            } else if (userRole.startsWith("IM")) {

                // 인적자원관리
            } else if (userRole.startsWith("HRM")) {

                // 생산관리
            } else {

            }
        }

        return null;
    }
}
