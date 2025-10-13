package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatisticDto {
    private TotalStockDto totalStock;
    private InventoryCountDto storeComplete;
    private InventoryCountDto storePending;
    private InventoryCountDto deliveryComplete;
    private InventoryCountDto deliveryPending;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalStockDto {
        private String value;
        private double comparedPrev;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryCountDto {
        private int value;
        private int comparedPrev;
    }
}
