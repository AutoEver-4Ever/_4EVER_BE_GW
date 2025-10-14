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
public class MesWorkOrderDetailDto {
    private Long workOrderId;
    private String workOrderCode;
    private Long productId;
    private String productName;
    private Integer quantity;
    private String unit;
    private Integer progressPercent;
    private StatusInfo status;
    private PlanInfo plan;
    private String currentOperation;
    private List<OperationDto> operations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusInfo {
        private String code;
        private String label;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanInfo {
        private String startDate;
        private String dueDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationDto {
        private String operationCode;
        private String operationName;
        private Integer sequence;
        private StatusInfo status;
        private String startedAt;
        private String finishedAt;
        private Double durationHours;
        private AssigneeDto assignee;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssigneeDto {
        private Long id;
        private String name;
    }
}
