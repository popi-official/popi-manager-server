package com.lgcns.domain.survey.repository;

import static com.lgcns.domain.survey.domain.QChoice.choice;
import static com.lgcns.domain.survey.domain.QMemberAnswer.memberAnswer;
import static com.lgcns.domain.survey.domain.QSurvey.survey;

import com.lgcns.domain.survey.dto.response.SurveyResultResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SurveyResultResponse> getSurveyResults(Long popupId) {
        Long totalCount = countMemberAnswerByPopup(popupId);

        NumberExpression<Double> ratioExpression =
                new CaseBuilder()
                        .when(Expressions.asNumber(totalCount).eq(0L))
                        .then(0.0)
                        .otherwise(
                                Expressions.numberTemplate(
                                        Double.class,
                                        "round((count({0}) * 100.0) / {1}, 0)",
                                        memberAnswer.id,
                                        totalCount));

        return queryFactory
                .select(
                        Projections.constructor(
                                SurveyResultResponse.class,
                                survey.number.as("surveyNumber"),
                                choice.content.as("choiceContent"),
                                choice.number.as("choiceNumber"),
                                memberAnswer.answerNumber.count().as("memberAnswerCount"),
                                ratioExpression.as("ratio")))
                .from(survey)
                .join(choice)
                .on(choice.survey.eq(survey))
                .leftJoin(memberAnswer)
                .on(memberAnswer.survey.eq(survey), memberAnswer.answerNumber.eq(choice.number))
                .where(survey.popup.id.eq(popupId))
                .groupBy(survey.number, choice.content, choice.number)
                .orderBy(survey.number.asc(), choice.number.asc())
                .fetch();
    }

    @Override
    public Long countMemberAnswerByPopup(Long popupId) {
        Long totalAnswers =
                queryFactory
                        .select(memberAnswer.count())
                        .from(memberAnswer)
                        .join(memberAnswer.survey, survey)
                        .where(survey.popup.id.eq(popupId))
                        .fetchOne();

        return totalAnswers != null ? totalAnswers / 4 : 0L;
    }
}
