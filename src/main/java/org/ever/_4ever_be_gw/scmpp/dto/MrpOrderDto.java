package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MrpOrderDto {
    private Long itemId;
    private String itemName;
    private int requiredQty;
    private int currentStock;
    private int safetyStock;
    private int availableStock;
    private String availableStatus;
    private Integer shortageQty;
    private String itemType;
    private String procurementStartDate;
    private String expectedArrivalDate;
    private String supplier;
}
