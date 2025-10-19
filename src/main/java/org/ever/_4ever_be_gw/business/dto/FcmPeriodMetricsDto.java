package org.ever._4ever_be_gw.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.ever._4ever_be_gw.scmpp.dto.PeriodStatDto;

@Getter
@Builder
@AllArgsConstructor
public class FcmPeriodMetricsDto {

    @JsonProperty("total_sales")
    private final PeriodStatDto totalSales;

    @JsonProperty("total_purchases")
    private final PeriodStatDto totalPurchases;

    @JsonProperty("net_profit")
    private final PeriodStatDto netProfit;

    @JsonProperty("accounts_receivable")
    private final PeriodStatDto accountsReceivable;
}
