package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStatisticDto {
    private TotalWarehouseDto totalWarehouse;
    private InOperationWarehouseDto inOperationWarehouse;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalWarehouseDto {
        private String value;
        private int comparedPrev;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InOperationWarehouseDto {
        private int value;
        private int comparedPrev;
    }
}
