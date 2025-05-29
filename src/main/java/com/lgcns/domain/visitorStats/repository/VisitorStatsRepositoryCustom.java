package com.lgcns.domain.visitorStats.repository;

import com.lgcns.domain.visitorStats.dto.response.VisitorStatsResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public interface VisitorStatsRepositoryCustom {
    VisitorStatsResponse getVisitorStatsByPopupId(Long popupId);

    Set<Long> findPopupIdsWithoutVisitorStats(
            Set<Long> popupIds, LocalDate nowDate, LocalTime nowTime);
}
