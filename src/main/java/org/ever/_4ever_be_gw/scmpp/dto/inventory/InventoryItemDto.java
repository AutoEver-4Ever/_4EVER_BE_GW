package org.ever._4ever_be_gw.scmpp.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDto {
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String category;
    private int currentStock;
    private int safetyStock;
    private String unit;
    private int price;
    private int totalValue;
    private String warehouseName;
    private String warehouseType;
    private String status;
}
