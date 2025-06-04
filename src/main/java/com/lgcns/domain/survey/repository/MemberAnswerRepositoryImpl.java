package com.lgcns.domain.survey.repository;

import com.lgcns.domain.survey.domain.MemberAnswer;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MemberAnswerRepositoryImpl implements MemberAnswerRepositoryCustom {

    private final EntityManager em;

    @Override
    @Transactional
    public void bulkInsertMemberAnswer(List<MemberAnswer> memberAnswers) {
        StringBuilder sb = new StringBuilder();
        String now = LocalDateTime.now().toString();

        sb.append(
                "INSERT INTO member_answer (survey_id, choice_id, member_id, created_at, updated_at) VALUES ");

        for (int i = 0; i < memberAnswers.size(); i++) {
            MemberAnswer memberAnswer = memberAnswers.get(i);

            sb.append("(")
                    .append(memberAnswer.getSurvey().getId())
                    .append(", ")
                    .append(memberAnswer.getChoiceId())
                    .append(", ")
                    .append(memberAnswer.getMemberId())
                    .append(", ")
                    .append("'")
                    .append(now)
                    .append("', ")
                    .append("'")
                    .append(now)
                    .append("'")
                    .append(")");

            if (i < memberAnswers.size() - 1) {
                sb.append(", ");
            }
        }

        em.createNativeQuery(sb.toString()).executeUpdate();
    }
}
