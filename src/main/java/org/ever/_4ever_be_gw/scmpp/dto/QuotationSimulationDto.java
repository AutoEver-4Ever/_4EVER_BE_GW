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
public class QuotationSimulationDto {
    private Long quotationId;
    private String quotationCode;
    private Long customerId;
    private String customerName;
    private Long productId;
    private String productName;
    private Integer requestQty;
    private String requestDueDate;
    private SimulationResultDto simulation;
    private List<ShortageItemDto> shortages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimulationResultDto {
        private String status; // FAIL, PENDING, PASS
        private Integer availableQty;
        private String suggestedDueDate;
        private String generatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShortageItemDto {
        private Long itemId;
        private String itemName;
        private Integer requiredQty;
        private Integer stockQty;
        private Integer shortQty;
    }
}
