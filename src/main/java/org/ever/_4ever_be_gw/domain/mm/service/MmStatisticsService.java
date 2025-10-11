package org.ever._4ever_be_gw.domain.mm.service;

import org.ever._4ever_be_gw.domain.mm.dto.PeriodMetrics;

import java.util.List;
import java.util.Map;

public interface MmStatisticsService {
    Map<String, PeriodMetrics> getStatistics(List<String> periods);
}

