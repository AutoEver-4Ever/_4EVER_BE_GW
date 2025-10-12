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
public class ItemDetailDto {
    private Long itemId;
    private String itemCode;
    private String itemName;
    private String category;
    private String supplier;
    private String status;
    private int currentStock;
    private String unit;
    private int price;
    private int totalValue;
    private String warehouseName;
    private String warehouseCode;
    private String lastModified;
    private String description;
    private String specification;
    private List<StockMovementDto> stockMovements;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockMovementDto {
        private String type;
        private int quantity;
        private String unit;
        private String from;
        private String to;
        private String date;
        private String manager;
        private String locationCode;
        private String note;
    }
}
