package org.ever._4ever_be_gw.dashboard.service.impl;

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
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowTabDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.ever._4ever_be_gw.scm.mm.service.MmHttpService;
import org.ever._4ever_be_gw.scm.pp.PpHttpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

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
            @AuthenticationPrincipal EverUserPrincipal principal,
            @RequestParam(defaultValue = "5", required = false) Integer size
    ) {
        if (principal == null) throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);

        // 사용자 정보 추출
        final String userId = principal.getUserId();
        final String userRole = principal.getUserRole();
        final int limit = Optional.ofNullable(size).orElse(DEFAULT_SIZE);

        // 탭코드는 DashboardWorkflowTabDto 참고
        switch (userRole.split("_")[0]) {
            case "SUPPLIER": {
                // 공급사 워크 플로우
                // [SCM-PP] 공급사에게 발행된 발주서 목록 조회(PO)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> supplierPurchaseOrderResponse =
                        mmHttpService.getDashboardPurchaseOrderList(userId, limit);
                // [비즈니스] 공급사의 매출 전표 조회(AR): 기업의 매입 전표는 공급사 입장에서 매출 전표
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> supplierInvoiceResponse =
                        fcmHttpService.getDashboardSupplierInvoiceList(userId, limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("PO")
                                        .items(safeItems(supplierPurchaseOrderResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("AR")
                                        .items(safeItems(supplierInvoiceResponse))
                                        .build()
                        ))
                        .build();
            }

            case "CUSTOMER": {
                // 고객사 워크 플로우
                // [비즈니스] 견적 목록 조회(QT): 고객사가 제품 구매를 위해 작성한 견적서
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> quotationResponse =
                        sdHttpService.getDashboardCustomerQuotationList(userId, limit);
                // [비즈니스] 매입 전표 요청(AP): 기업의 매출 전표는 고객사 입장에서 매입 전표
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> customerInvoiceResponse =
                        fcmHttpService.getDashboardCustomerInvoiceList(userId, limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("QT")
                                        .items(safeItems(quotationResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("AP")
                                        .items(safeItems(customerInvoiceResponse))
                                        .build()
                        ))
                        .build();
            }

            case "MM": {
                // 구매 관리 부서의 대시보드 워크 플로우
                // [비즈니스] 기업의 발주서 목록 조회(PO) -> 하는 중
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> mmPurchaseOrderResponse =
                        mmHttpService.getDashboardPurchaseOrderList(userId, limit);
                // [비즈니스] 주문서 목록 조회(SO) -> 다음에 이거
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> mmPurchaseRequestResponse =
                        mmHttpService.getDashboardPurchaseRequestList(userId, limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("PO")
                                        .items(safeItems(mmPurchaseRequestResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("SO")
                                        .items(safeItems(mmPurchaseOrderResponse))
                                        .build()
                        ))
                        .build();
            }

            case "SD": {
                // 영업 관리 부서의 대시보드 워크 플로우
                // [비즈니스] 견적서 목록 조회(QT)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> sdCustomerQuotationResponse =
                        sdHttpService.getDashboardCustomerQuotationList(userId, limit);
                // [비즈니스] 주문서 목록 조회(SO) -> MM의 주문서 목록 조회랑 동일
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> sdSupplierQuotationResponse =
                        sdHttpService.getDashboardSupplierOrderList(userId, limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("QT")
                                        .items(safeItems(sdCustomerQuotationResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("SO")
                                        .items(safeItems(sdSupplierQuotationResponse))
                                        .build()
                        ))
                        .build();
            }

            case "FCM": {
                // 재무 관리 부서의 대시보드 워크 플로우
                // [비즈니스] 기업의 매출 전표 목록(AR)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> fcmArListResponse =
                        fcmHttpService.getDashboardCompanyArList(userId, limit);
                // [비즈니스] 기업의 매입 전표 목록(AP)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> fcmApListResponse =
                        fcmHttpService.getDashboardCompanyApList(userId, limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("AR")
                                        .items(safeItems(fcmArListResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("AP")
                                        .items(safeItems(fcmApListResponse))
                                        .build()
                        ))
                        .build();
            }

            case "IM": {
                // 재고 관리 부터의 대시보드 워크 플로우
                // [SCM-PP] 입고 목록 조회
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> imInboundListResponse =
                        imHttpService.getDashboardInboundList(userId, limit);
                // [SCM-PP] 출고 목록 조회
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> imOutboundListResponse =
                        imHttpService.getDashboardOutboundList(userId, limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("IN")
                                        .items(safeItems(imInboundListResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("OUT")
                                        .items(safeItems(imOutboundListResponse))
                                        .build()
                        ))
                        .build();
            }

            case "HRM": {
                // 인적 자원 관리 부서의 대시보드 워크 플로우
                // [비즈니스] 근태 목록 조회(ATT)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> hrmAttendanceListResponse =
                        hrmHttpService.getDashboardAttendanceList(userId, limit);
                // [비즈니스] 휴가 신청 목록 조회(LV)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> hrmLeaveRequestListResponse =
                        hrmHttpService.getDashboardLeaveRequestList(userId, limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("ATT")
                                        .items(safeItems(hrmAttendanceListResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("LV")
                                        .items(safeItems(hrmLeaveRequestListResponse))
                                        .build()
                        ))
                        .build();
            }

            case "PP": {
                // 생산 관리 부서의 대시보드 워크 플로우
                // [SCM-PP] 생산관리로 전환된 견적서 목록 조회(QT)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> ppToProductionQuotationResponse =
                        ppHttpService.getDashboardQuotationsToProduction(userId, limit);
                // [SCM-PP] 생산 목록 조회(MES)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> ppInProgressResponse =
                        ppHttpService.getDashboardProductionInProgress(userId, limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("QT")
                                        .items(safeItems(ppToProductionQuotationResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("MES")
                                        .items(safeItems(ppInProgressResponse))
                                        .build()
                        ))
                        .build();
            }

            default: {
                // 관리자의 대시보드 워크 플로우
                // [비즈니스] 기업의 매출 전표 목록(AR)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> fcmArListResponse =
                        fcmHttpService.getDashboardCompanyArList(userId, limit);
                // [비즈니스] 기업의 매입 전표 목록(AP)
                ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> fcmApListResponse =
                        fcmHttpService.getDashboardCompanyApList(userId, limit);

                return DashboardWorkflowResponseDto.builder()
                        .tabs(List.of(
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("AR")
                                        .items(safeItems(fcmArListResponse))
                                        .build(),
                                DashboardWorkflowTabDto.builder()
                                        .tabCode("AP")
                                        .items(safeItems(fcmApListResponse))
                                        .build()
                        ))
                        .build();
            }
        }
    }

    /**
     * null-safe로 items 뽑아오기
     */
    private static List<DashboardWorkflowItemDto> safeItems(
            ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> resp
    ) {
        if (resp == null || resp.getBody() == null || resp.getBody().getData() == null) return List.of();
        return resp.getBody().getData();
    }
}
