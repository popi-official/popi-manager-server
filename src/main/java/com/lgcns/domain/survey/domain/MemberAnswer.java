package com.lgcns.domain.survey.domain;

import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAnswer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    private Long choiceId;

    private Long memberId;

    @Builder
    private MemberAnswer(Survey survey, Long choiceId, Long memberId) {
        this.survey = survey;
        this.choiceId = choiceId;
        this.memberId = memberId;
    }

    public static MemberAnswer createMemberAnswer(Survey survey, Long choiceId, Long memberId) {
        return MemberAnswer.builder().survey(survey).choiceId(choiceId).memberId(memberId).build();
    }
}
