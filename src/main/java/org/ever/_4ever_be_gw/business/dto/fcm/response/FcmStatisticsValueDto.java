package org.ever._4ever_be_gw.business.dto.fcm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FcmStatisticsValueDto {
    private BigDecimal value;
    private Double deltaRate;
}
