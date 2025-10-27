package org.ever._4ever_be_gw.scmpp.dto.warehouse;

import lombok.Data;

@Data
public class UpdateWarehouseRequestDto {
    private String warehouseName;
    private String warehouseType;
    private String location;
    private String managerId;
    private String warehouseStatusCode;
}