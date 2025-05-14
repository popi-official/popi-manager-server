package com.lgcns.domain.survey.dto.response;

public record SurveyResultResponse(
        Integer surveyNumber,
        String choiceContent,
        Integer choiceNumber,
        Long memberAnswerCount,
        Double ratio) {
    public static SurveyResultResponse of(
            Integer surveyNumber,
            String choiceContent,
            Integer choiceNumber,
            Long memberAnswerCount,
            Double ratio) {
        return new SurveyResultResponse(
                surveyNumber, choiceContent, choiceNumber, memberAnswerCount, ratio);
    }
}
