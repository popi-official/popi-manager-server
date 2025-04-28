package com.lgcns.domain.survey.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    private Long memberId;

    private Long choiceId;

    private int answerNumber;

    @Builder
    private MemberAnswer(Survey survey, Long memberId, Long choiceId, int answerNumber) {
        this.survey = survey;
        this.memberId = memberId;
        this.choiceId = choiceId;
        this.answerNumber = answerNumber;
    }
}
