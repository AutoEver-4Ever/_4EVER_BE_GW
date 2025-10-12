package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDto {
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String status;
    private String warehouseType;
    private String location;
    private String manager;
    private String phone;
}
