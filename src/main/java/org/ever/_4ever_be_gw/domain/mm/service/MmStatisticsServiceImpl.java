package org.ever._4ever_be_gw.domain.mm.service;

import org.ever._4ever_be_gw.domain.mm.dto.PeriodMetrics;
import org.ever._4ever_be_gw.domain.mm.dto.PeriodStat;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MmStatisticsServiceImpl implements MmStatisticsService {
    @Override
    public Map<String, PeriodMetrics> getStatistics(List<String> periods) {
        Map<String, PeriodMetrics> data = new LinkedHashMap<>();

        if (periods.contains("week")) {
            data.put("week", PeriodMetrics.builder()
                    .purchaseRequestCount(new PeriodStat(184L, new BigDecimal("0.0728")))
                    .purchaseApprovalPendingCount(new PeriodStat(39L, new BigDecimal("-0.0532")))
                    .purchaseOrderAmount(new PeriodStat(1_283_000_000L, new BigDecimal("0.1044")))
                    .purchaseOrderApprovalPendingCount(new PeriodStat(22L, new BigDecimal("0.1000")))
                    .build());
        }
        if (periods.contains("month")) {
            data.put("month", PeriodMetrics.builder()
                    .purchaseRequestCount(new PeriodStat(736L, new BigDecimal("0.0389")))
                    .purchaseApprovalPendingCount(new PeriodStat(161L, new BigDecimal("-0.0417")))
                    .purchaseOrderAmount(new PeriodStat(5_214_000_000L, new BigDecimal("0.0361")))
                    .purchaseOrderApprovalPendingCount(new PeriodStat(94L, new BigDecimal("0.0652")))
                    .build());
        }
        if (periods.contains("quarter")) {
            data.put("quarter", PeriodMetrics.builder()
                    .purchaseRequestCount(new PeriodStat(2_154L, new BigDecimal("0.0215")))
                    .purchaseApprovalPendingCount(new PeriodStat(472L, new BigDecimal("-0.0186")))
                    .purchaseOrderAmount(new PeriodStat(15_123_000_000L, new BigDecimal("0.0247")))
                    .purchaseOrderApprovalPendingCount(new PeriodStat(281L, new BigDecimal("0.0426")))
                    .build());
        }
        if (periods.contains("year")) {
            data.put("year", PeriodMetrics.builder()
                    .purchaseRequestCount(new PeriodStat(8_421L, new BigDecimal("0.0298")))
                    .purchaseApprovalPendingCount(new PeriodStat(1_813L, new BigDecimal("-0.0221")))
                    .purchaseOrderAmount(new PeriodStat(59_876_000_000L, new BigDecimal("0.0312")))
                    .purchaseOrderApprovalPendingCount(new PeriodStat(1_103L, new BigDecimal("0.0185")))
                    .build());
        }

        return data;
    }
}

