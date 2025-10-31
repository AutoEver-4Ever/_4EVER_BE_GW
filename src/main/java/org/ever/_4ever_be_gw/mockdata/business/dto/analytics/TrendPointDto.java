package org.ever._4ever_be_gw.mockdata.business.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendPointDto {
    private Integer year;
    private Integer month;
    private Integer week;
    private Long sale;
    private Integer orderCount;
}

