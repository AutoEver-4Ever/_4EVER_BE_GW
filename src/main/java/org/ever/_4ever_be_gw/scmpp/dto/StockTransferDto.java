package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferDto {
    private String type;
    private int quantity;
    private String unit;
    private String itemName;
    private LocalDateTime workTime;
    private String manager;
}
