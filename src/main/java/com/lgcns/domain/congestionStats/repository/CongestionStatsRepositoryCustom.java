package com.lgcns.domain.congestionStats.repository;

import com.lgcns.domain.congestionStats.dto.response.CongestionStatsResponse;
import java.time.LocalTime;

public interface CongestionStatsRepositoryCustom {

    CongestionStatsResponse findDailyCongestionStats(
            Long popupId, LocalTime startTime, LocalTime endTime);
}
