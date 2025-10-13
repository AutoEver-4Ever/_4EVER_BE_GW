package org.ever._4ever_be_gw.scmpp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PeriodMetricsDto {
    @JsonProperty("purchase_request_count")
    private final PeriodStatDto purchaseRequestCount;

    @JsonProperty("purchase_approval_pending_count")
    private final PeriodStatDto purchaseApprovalPendingCount;

    @JsonProperty("purchase_order_amount")
    private final PeriodStatDto purchaseOrderAmount;

    @JsonProperty("purchase_order_approval_pending_count")
    private final PeriodStatDto purchaseOrderApprovalPendingCount;
}

