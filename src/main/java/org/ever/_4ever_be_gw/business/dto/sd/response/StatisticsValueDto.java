package org.ever._4ever_be_gw.business.dto.sd.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class StatisticsValueDto {
    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("delta_rate")
    private Double deltaRate;
}
