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
    private Long id;              // soId (목록용)
    private String soNumber;      // SO-2024-001
    private Long customerId;
    private String customerName;
    private ManagerDto manager;   // { name, mobile }

    // 하위 호환 필드 (목록 응답에 존재)
    private String contactName;
    private String contactPhone;

    private String orderDate;     // yyyy-MM-dd
    private String deliveryDate;  // yyyy-MM-dd
    private Long totalAmount;
    private String statusCode;    // MATERIAL_PREPARATION, PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED
    private List<String> actions; // 예: ["view"]
}

