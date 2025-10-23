package org.ever._4ever_be_gw.scmpp.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockTransferDto {
    private Long fromWarehouseId;
    private Long toWarehouseId;
    private Long stockId;
    private Integer stockQuantity;
    private String uomName;
    private String reason;
}
