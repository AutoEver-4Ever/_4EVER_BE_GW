package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortageItemPreviewDto {
    private String itemName;
    private int currentStock;
    private String currentUnit;
    private int safetyStock;
    private String safetyUnit;
    private String status;
}
