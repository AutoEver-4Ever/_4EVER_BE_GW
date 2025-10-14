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
public class MpsProductPlanDto {
    private Long productId;
    private String productName;
    private String periodType;
    private List<String> periods;
    private List<Integer> demand;
    private List<Integer> requiredInventory;
    private List<Integer> productionNeeded;
    private List<Integer> plannedProduction;
    private Integer totalPlannedProduction;
    private Integer totalDemand;
    private Integer productionWeeks;
    private Integer averageWeeklyProduction;
}
