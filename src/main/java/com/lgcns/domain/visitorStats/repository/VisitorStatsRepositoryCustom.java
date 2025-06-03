package com.lgcns.domain.visitorStats.repository;

import com.lgcns.domain.visitorStats.domain.VisitorStats;
import com.lgcns.domain.visitorStats.dto.response.VisitorStatsResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface VisitorStatsRepositoryCustom {
    VisitorStatsResponse getVisitorStatsByPopupId(Long popupId);

    List<Long> findPopupIdsWithoutVisitorStats(
            List<Long> popupIds, LocalDate nowDate, LocalTime nowTime);

    void bulkInsertVisitorStats(List<VisitorStats> visitorStatsList);
}
