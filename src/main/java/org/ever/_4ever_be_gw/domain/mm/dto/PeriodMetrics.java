package org.ever._4ever_be_gw.domain.mm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PeriodMetrics {
    @JsonProperty("purchase_request_count")
    private final PeriodStat purchaseRequestCount;

    @JsonProperty("purchase_approval_pending_count")
    private final PeriodStat purchaseApprovalPendingCount;

    @JsonProperty("purchase_order_amount")
    private final PeriodStat purchaseOrderAmount;

    @JsonProperty("purchase_order_approval_pending_count")
    private final PeriodStat purchaseOrderApprovalPendingCount;
}

