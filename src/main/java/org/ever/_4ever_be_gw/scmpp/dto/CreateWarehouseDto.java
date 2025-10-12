package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarehouseDto {
    private String warehouseName;
    private String warehouseCode;
    private String warehouseType;
    private String location;
    private String manager;
    private String phone;
}
