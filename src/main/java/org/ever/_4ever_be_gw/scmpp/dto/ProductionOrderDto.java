package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrderDto {
    private Long salesOrderId;
    private String salesOrderCode;
    private String customer;
    private String orderDate;
    private String dueDate;
    private int orderAmount;
    private String currency;
    private String status;
}
