package org.ever._4ever_be_gw.domain.mm.service;

import org.ever._4ever_be_gw.domain.mm.dto.PeriodMetricsDto;
import org.ever._4ever_be_gw.domain.mm.dto.PeriodStatDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MmStatisticsServiceImpl implements MmStatisticsService {
    @Override
    public Map<String, PeriodMetricsDto> getStatistics(List<String> periods) {
        Map<String, PeriodMetricsDto> data = new LinkedHashMap<>();

        if (periods.contains("week")) {
            data.put("week", PeriodMetricsDto.builder()
                    .purchaseRequestCount(new PeriodStatDto(184L, new BigDecimal("0.0728")))
                    .purchaseApprovalPendingCount(new PeriodStatDto(39L, new BigDecimal("-0.0532")))
                    .purchaseOrderAmount(new PeriodStatDto(1_283_000_000L, new BigDecimal("0.1044")))
                    .purchaseOrderApprovalPendingCount(new PeriodStatDto(22L, new BigDecimal("0.1000")))
                    .build());
        }
        if (periods.contains("month")) {
            data.put("month", PeriodMetricsDto.builder()
                    .purchaseRequestCount(new PeriodStatDto(736L, new BigDecimal("0.0389")))
                    .purchaseApprovalPendingCount(new PeriodStatDto(161L, new BigDecimal("-0.0417")))
                    .purchaseOrderAmount(new PeriodStatDto(5_214_000_000L, new BigDecimal("0.0361")))
                    .purchaseOrderApprovalPendingCount(new PeriodStatDto(94L, new BigDecimal("0.0652")))
                    .build());
        }
        if (periods.contains("quarter")) {
            data.put("quarter", PeriodMetricsDto.builder()
                    .purchaseRequestCount(new PeriodStatDto(2_154L, new BigDecimal("0.0215")))
                    .purchaseApprovalPendingCount(new PeriodStatDto(472L, new BigDecimal("-0.0186")))
                    .purchaseOrderAmount(new PeriodStatDto(15_123_000_000L, new BigDecimal("0.0247")))
                    .purchaseOrderApprovalPendingCount(new PeriodStatDto(281L, new BigDecimal("0.0426")))
                    .build());
        }
        if (periods.contains("year")) {
            data.put("year", PeriodMetricsDto.builder()
                    .purchaseRequestCount(new PeriodStatDto(8_421L, new BigDecimal("0.0298")))
                    .purchaseApprovalPendingCount(new PeriodStatDto(1_813L, new BigDecimal("-0.0221")))
                    .purchaseOrderAmount(new PeriodStatDto(59_876_000_000L, new BigDecimal("0.0312")))
                    .purchaseOrderApprovalPendingCount(new PeriodStatDto(1_103L, new BigDecimal("0.0185")))
                    .build());
        }

        return data;
    }
}
