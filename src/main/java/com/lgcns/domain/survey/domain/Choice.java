package com.lgcns.domain.survey.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    private String content;

    private int number;

    @Builder
    private Choice(Survey survey, String content, int number) {
        this.survey = survey;
        this.content = content;
        this.number = number;
    }

    public static Choice createChoice(Survey survey, String content, int number) {
        return Choice.builder().survey(survey).content(content).number(number).build();
    }
}
