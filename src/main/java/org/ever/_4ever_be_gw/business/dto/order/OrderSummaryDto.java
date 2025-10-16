package org.ever._4ever_be_gw.business.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {
    private Long soId;          // 상세용 ID
    private String soNumber;    // 예: SO-2024-001
    private String orderDate;   // yyyy-MM-dd
    private String deliveryDate;// yyyy-MM-dd
    private String statusCode;  // MATERIAL_PREPARATION, PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED, ALL
    private Long totalAmount;   // 총액
}

