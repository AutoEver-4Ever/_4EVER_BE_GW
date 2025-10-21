package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationCreateResponseDto {
    private String qoId;       // 생성된 견적 ID (UUID v7)
    private String qoDate;     // 생성일(YYYY-MM-DD)
    private String dueDate;    // 요청 납기일(YYYY-MM-DD)
    private Long totalAmount;  // 총액
    private String statusCode; // PENDING 등
}

