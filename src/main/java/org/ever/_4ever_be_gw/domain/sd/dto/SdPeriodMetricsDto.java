package org.ever._4ever_be_gw.domain.sd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.ever._4ever_be_gw.domain.mm.dto.PeriodStatDto;

@Getter
@Builder
@AllArgsConstructor
public class SdPeriodMetricsDto {
    @JsonProperty("sales_amount")
    private final PeriodStatDto salesAmount;

    @JsonProperty("new_orders_count")
    private final PeriodStatDto newOrdersCount;
}

