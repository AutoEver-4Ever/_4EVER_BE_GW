package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceivedPurchaseOrderDto {
    private String purchaseOrderCode;
    private String supplier;
    private String orderDate;
    private String receivedDate;
    private int totalAmount;
    private String status;
}
