package com.lgcns.domain.survey.kafka.message;

import com.lgcns.domain.survey.kafka.message.dto.SurveyChoiceDto;
import java.util.List;

public record MemberAnswerMessage(Long memberId, List<SurveyChoiceDto> surveyChoices) {
    public static MemberAnswerMessage of(Long memberId, List<SurveyChoiceDto> surveyChoices) {
        return new MemberAnswerMessage(memberId, surveyChoices);
    }
}
