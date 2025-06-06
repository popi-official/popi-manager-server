package com.lgcns.domain.congestionStats.repository;

import com.lgcns.domain.congestionStats.dto.response.CongestionStatsResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CongestionStatsRepositoryCustom {

    CongestionStatsResponse findDailyCongestionStats(
            Long popupId, LocalTime startTime, LocalTime endTime);

    List<Long> findPopupIdsWithoutCongestionStats(
            List<Long> popupIds, LocalDate nowDate, LocalTime nowTime);
}
