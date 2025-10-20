package org.ever._4ever_be_gw.business.dto.invoice;

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
public class SalesInvoiceDetailDto {
    private Long invoiceId;           // 매출 전표 ID (미발생 시 null)
    private String invoiceCode;       // 매출 전표 코드 (미발생 시 null)
    private String statusCode;        // 매출 전표 상태 코드
    private LocalDate issueDate;      // 매출 전표 발행일
    private LocalDate dueDate;        // 매출 전표 납기일
    private String customerName;      // 고객사 이름
    private String ceoName;           // 고객사 대표 이름
    private String ownerName;         // 영업 담당자 이름
    private ReferenceInfo reference;  // 참조 정보 (견적)
    private List<SalesInvoiceItemDto> items; // 품목 리스트
    private long totalAmount;         // 합계 금액
    private String note;              // 비고/메모

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferenceInfo {
        private String type;              // 예: QUOTATION
        private Long quotationId;         // 참조 견적 ID
        private String quotationCode;     // 참조 견적 코드
        private LocalDate quotationDate;  // 견적일
        private LocalDate dueDate;        // 견적 납기일
    }
}
