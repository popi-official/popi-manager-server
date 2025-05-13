package com.lgcns.domain.survey.repository;

import static com.lgcns.domain.survey.domain.QChoice.choice;
import static com.lgcns.domain.survey.domain.QMemberAnswer.memberAnswer;
import static com.lgcns.domain.survey.domain.QSurvey.survey;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Tuple> getSurveyResults(Long popupId) {
        return queryFactory
                .select(
                        survey.number,
                        choice.content,
                        choice.number,
                        memberAnswer.answerNumber.count())
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
    public int countMemberAnswerByPopup(Long popupId) {
        return queryFactory
                .select(memberAnswer.count())
                .from(memberAnswer)
                .join(memberAnswer.survey, survey)
                .where(survey.popup.id.eq(popupId))
                .fetchOne()
                .intValue();
    }
}
