package com.lgcns.domain.entrance.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Entrance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entrance_id")
    private Long id;

    private Long popupId;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    private int ageGroup;

    private LocalDate date;

    private LocalTime time;

    @Builder
    private Entrance(
            Long popupId, UserGender gender, int ageGroup, LocalDate date, LocalTime time) {
        this.popupId = popupId;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.date = date;
        this.time = time;
    }

    public static Entrance createPopupEnter(
            Long popupId, UserGender gender, int ageGroup, LocalDate date, LocalTime time) {
        return Entrance.builder()
                .popupId(popupId)
                .gender(gender)
                .ageGroup(ageGroup)
                .date(date)
                .time(time)
                .build();
    }
}
