package com.lgcns.domain.visitorStats.repository;

import com.lgcns.domain.visitorStats.dto.response.VisitorStatsResponse;
import java.time.LocalDate;
import java.time.LocalTime;

public interface VisitorStatsRepositoryCustom {
    VisitorStatsResponse getVisitorStatsByPopupId(Long popupId);

    boolean existByPopupIdAndAnalyzedDateTime(Long popupId, LocalDate nowDate, LocalTime nowTime);
}
