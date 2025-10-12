package org.ever._4ever_be_gw.scmpp.service;

import org.ever._4ever_be_gw.scmpp.dto.PeriodMetricsDto;

import java.util.List;
import java.util.Map;

public interface MmStatisticsService {
    Map<String, PeriodMetricsDto> getStatistics(List<String> periods);
}
