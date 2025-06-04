package com.lgcns.domain.survey.repository;

import com.lgcns.domain.survey.domain.MemberAnswer;
import java.util.List;

public interface MemberAnswerRepositoryCustom {
    void bulkInsertMemberAnswer(List<MemberAnswer> memberAnswers);
}
