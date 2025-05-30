package com.lgcns.domain.visitorStats.service;

import com.lgcns.domain.visitorStats.domain.VisitorStats;
import com.lgcns.domain.visitorStats.dto.response.VisitorAnalysisResponse;
import java.util.List;

public interface VisitorStatsService {
    VisitorAnalysisResponse getVisitorAnalysis(Long popupId);

    List<Long> findTargetPopupIds();

    VisitorStats convertVisitorStats(Long popupId);

    void createVisitorStats(List<VisitorStats> visitorStatsList);
}
