package com.lgcns.domain.visitorStats.service;

import com.lgcns.domain.visitorStats.dto.response.VisitorAnalysisResponse;

public interface VisitorStatsService {
    VisitorAnalysisResponse getVisitorAnalysis(Long popupId);
}
