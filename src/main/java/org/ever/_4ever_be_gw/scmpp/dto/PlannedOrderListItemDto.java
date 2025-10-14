package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannedOrderListItemDto {
    private Long plannedId;
    private Long quotationId;
    private String quotationCode;
    private Long itemId;
    private String itemName;
    private int quantity;
    private String procurementStartDate;
    private String status;
}
