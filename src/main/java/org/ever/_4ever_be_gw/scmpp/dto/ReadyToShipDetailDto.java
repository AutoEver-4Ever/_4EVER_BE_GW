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
public class ReadyToShipDetailDto {
    private Long salesOrderId;
    private String salesOrderCode;
    private String customer;
    private String dueDate;
    private String status;
    private List<OrderItemDto> orderItems;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private String itemName;
        private int quantity;
        private String unit;
    }
}
