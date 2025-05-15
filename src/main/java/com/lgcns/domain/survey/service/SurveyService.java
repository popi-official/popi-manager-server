package com.lgcns.domain.survey.service;

import com.lgcns.domain.survey.dto.response.SurveyAnalysisResponse;

public interface SurveyService {

    SurveyAnalysisResponse getSurveyAnalysis(Long popupId);
}
