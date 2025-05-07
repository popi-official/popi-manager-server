package com.lgcns.domain.survey.domain;

import com.lgcns.domain.popup.domain.Popup;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popup;

    private int number;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Choice> choiceList = new ArrayList<>();

    @Builder
    private Survey(Long id, Popup popup, int number) {
        this.id = id;
        this.popup = popup;
        this.number = number;
    }

    public static Survey createSurvey(Popup popup, int number) {
        return Survey.builder().popup(popup).number(number).build();
    }
}
