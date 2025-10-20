package org.ever._4ever_be_gw.business.dto.invoice;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApInvoiceDetailDto {
    private Long invoiceId;              // 매입 전표 ID (미발생 시 null)
    private String invoiceCode;          // 매입 전표 코드 (미발생 시 null)
    private String statusCode;           // 매입 전표 상태 코드 (예: UNPAID, PENDING, PAID)
    private LocalDate issueDate;         // 매입 전표 발행일
    private LocalDate dueDate;           // 매입 전표 납기일

    // 공급사 정보
    private Long supplierId;             // 공급사 ID
    private String supplierCode;         // 공급사 코드
    private String supplierName;         // 공급사명
    private String managerPhone;         // 담당자 연락처
    private String managerEmail;         // 담당자 이메일
    private String deliveryAddress;      // 납품지 주소

    // 참조 정보 및 품목
    private ReferenceInfo reference;     // 참조 정보 (구매 주문서)
    private List<PurchaseInvoiceItemDto> items; // 품목 리스트

    // 합계 및 비고
    private long totalAmount;            // 합계 금액
    private String note;                 // 비고/메모

    // 생성/수정 일시
    private Instant createdAt;           // 생성 일시
    private Instant updatedAt;           // 수정 일시

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferenceInfo {
        private String type;                 // 예: PURCHASE_ORDER
        private Long purchaseOrderId;        // 참조 구매주문 ID
        private String purchaseOrderCode;    // 참조 구매주문 코드
        private LocalDate orderDate;         // 주문일자
        private LocalDate requestedDeliveryDate; // 요청 납기일
    }
}
