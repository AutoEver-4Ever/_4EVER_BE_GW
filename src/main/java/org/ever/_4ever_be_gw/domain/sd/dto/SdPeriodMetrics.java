package org.ever._4ever_be_gw.domain.sd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.ever._4ever_be_gw.domain.mm.dto.PeriodStat;

@Getter
@Builder
@AllArgsConstructor
public class SdPeriodMetrics {
    @JsonProperty("sales_amount")
    private final PeriodStat salesAmount;

    @JsonProperty("new_orders_count")
    private final PeriodStat newOrdersCount;
}

