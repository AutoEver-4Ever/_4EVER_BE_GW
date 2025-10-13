package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDetailDto {
    private WarehouseInfoDto warehouseInfo;
    private WarehouseManagerDto manager;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseInfoDto {
        private String warehouseName;
        private String warehouseCode;
        private String warehouseType;
        private String warehouseStatus;
        private String location;
        private String description;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseManagerDto {
        private String name;
        private String phoneNumber;
        private String email;
    }
}
