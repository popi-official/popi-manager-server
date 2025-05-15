package com.lgcns.domain.visitorStats.repository;

import com.lgcns.domain.visitorStats.dto.response.VisitorStatsResponse;

public interface VisitorStatsRepositoryCustom {
    VisitorStatsResponse getVisitorStatsByPopupId(Long popupId);
}
