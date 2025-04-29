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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    private int answerNumber;

    private String memberGender;
    private int memberAge;

    @Builder
    private MemberAnswer(Survey survey, int answerNumber, String memberGender, int memberAge) {
        this.survey = survey;
        this.answerNumber = answerNumber;
        this.memberGender = memberGender;
        this.memberAge = memberAge;
    }
}
