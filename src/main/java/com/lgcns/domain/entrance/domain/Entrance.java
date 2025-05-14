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

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    @Builder
    private Entrance(
            Long popupId,
            UserGender gender,
            int ageGroup,
            LocalDate reservationDate,
            LocalTime reservationTime) {
        this.popupId = popupId;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
    }

    public static Entrance createPopupEnter(
            Long popupId,
            UserGender gender,
            int ageGroup,
            LocalDate reservationDate,
            LocalTime reservationTime) {
        return Entrance.builder()
                .popupId(popupId)
                .gender(gender)
                .ageGroup(ageGroup)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .build();
    }
}
