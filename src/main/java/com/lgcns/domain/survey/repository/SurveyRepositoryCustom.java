package com.lgcns.domain.survey.repository;

import com.lgcns.domain.survey.dto.response.SurveyResultResponse;
import java.util.List;

public interface SurveyRepositoryCustom {

    List<SurveyResultResponse> getSurveyResults(Long popupId);

    Long countMemberAnswerByPopup(Long popupId);
}
