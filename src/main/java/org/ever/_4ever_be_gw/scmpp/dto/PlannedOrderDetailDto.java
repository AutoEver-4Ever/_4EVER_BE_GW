package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannedOrderDetailDto {
    private Long plannedId;
    private Long quotationId;
    private String quotationCode;
    private Long requestId;
    private String requester;
    private String department;
    private String requestDate;
    private String desiredDueDate;
    private String status;
    private List<OrderItemDto> orderItems;
    private int totalAmount;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private Long itemId;
        private String itemName;
        private int quantity;
        private String unit;
        private int unitPrice;
    }
}
