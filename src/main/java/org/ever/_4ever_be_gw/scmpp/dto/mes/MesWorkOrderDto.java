package org.ever._4ever_be_gw.scmpp.dto.mes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesWorkOrderDto {
    private Long workOrderId;
    private String workOrderCode;
    private Long productId;
    private String productName;
    private Integer quantity;
    private String unit;
    private Long quotationId;
    private String quotationCode;
    private String status;
    private String currentOperation;
    private String startDate;
    private String endDate;
    private Integer progressRate;
    private List<String> operationSequence;
}
