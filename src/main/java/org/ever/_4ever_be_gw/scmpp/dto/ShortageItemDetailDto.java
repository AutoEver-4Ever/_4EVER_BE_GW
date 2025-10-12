package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortageItemDetailDto {
    private String itemName;
    private String itemCode;
    private String category;
    private int currentStock;
    private String currentUnit;
    private int safetyStock;
    private String safetyUnit;
    private int unitPrice;
    private int totalValue;
    private String warehouseName;
    private String warehouseCode;
    private String status;
}
