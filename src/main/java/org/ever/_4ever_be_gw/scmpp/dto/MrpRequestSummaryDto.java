package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MrpRequestSummaryDto {
    private int selectedOrderCount;
    private int totalExpectedAmount;
    private String requestDate;
    private List<MrpItemDto> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MrpItemDto {
        private Long plannedId;
        private String quotationCode;
        private String itemName;
        private int quantity;
        private int unitPrice;
        private int totalAmount;
        private String supplier;
        private String dueDate;
        private String status;
    }
}
