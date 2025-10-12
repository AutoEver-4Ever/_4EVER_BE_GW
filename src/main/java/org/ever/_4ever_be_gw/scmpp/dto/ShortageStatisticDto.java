package org.ever._4ever_be_gw.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortageStatisticDto {
    private TotalItemDto totalEmergency;
    private TotalItemDto totalWarning;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalItemDto {
        private String value;
        private int comparedPrev;
    }
}
