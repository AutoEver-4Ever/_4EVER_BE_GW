package org.ever._4ever_be_gw.business.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderListItemDto {
    private Long soId;              // soId (목록용)
    private String soNumber;        // SO-2024-001
    private String customerName;    // 회사 이름
    private ManagerDto manager;     // { managerName, managerPhone, managerEmail }
    private String orderDate;       // 주문일(yyyy-MM-dd)
    private String deliveryDate;    // 납기일(yyyy-MM-dd)
    private Long totalAmount;       // 주문 금액
    private String statusCode;      // MATERIAL_PREPARATION, PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED
}

