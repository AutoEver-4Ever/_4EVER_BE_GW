package org.ever._4ever_be_gw.scmpp.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferDto {
    private String type;
    private int quantity;
    private String unit;
    private String itemName;
    private LocalDate workDate;
    private String manager;
}
