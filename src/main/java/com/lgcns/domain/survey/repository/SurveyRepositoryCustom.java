package com.lgcns.domain.survey.repository;

import com.querydsl.core.Tuple;
import java.util.List;

public interface SurveyRepositoryCustom {

    List<Tuple> getSurveyResults(Long popupId);

    int countMemberAnswerByPopup(Long popupId);
}
