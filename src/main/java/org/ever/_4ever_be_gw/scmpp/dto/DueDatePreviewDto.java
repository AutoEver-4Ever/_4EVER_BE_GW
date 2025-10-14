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
public class DueDatePreviewDto {
    private String quotationCode;
    private String customerName;
    private String productName;
    private String confirmedDueDate;
    private List<WeekPlanDto> weeks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeekPlanDto {
        private String week;
        private Integer demand;
        private Integer requiredStock;
        private Integer productionQty;
        private Integer mps;
    }
}
