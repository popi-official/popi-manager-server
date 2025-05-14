package com.lgcns.domain.survey.dto.response;

public record SurveyResultResponse(
        int surveyNumber,
        String choiceContent,
        int choiceNumber,
        int memberAnswerCount,
        double ratio) {
    public static SurveyResultResponse of(
            int surveyNumber,
            String choiceContent,
            int choiceNumber,
            int memberAnswerCount,
            double ratio) {
        return new SurveyResultResponse(
                surveyNumber, choiceContent, choiceNumber, memberAnswerCount, ratio);
    }
}
