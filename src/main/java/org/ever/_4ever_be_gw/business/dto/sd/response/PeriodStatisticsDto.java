package org.ever._4ever_be_gw.business.dto.sd.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodStatisticsDto {
    @JsonProperty("sales_amount")
    private StatisticsValueDto salesAmount;

    @JsonProperty("new_orders_count")
    private StatisticsValueDto newOrdersCount;
}
