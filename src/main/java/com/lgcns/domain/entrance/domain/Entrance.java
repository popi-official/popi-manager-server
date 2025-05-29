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
    private MemberGender gender;

    @Enumerated(EnumType.STRING)
    private MemberAge age;

    private LocalDate reservationDate;

    private LocalTime reservationTime;

    @Builder
    private Entrance(
            Long popupId,
            MemberGender gender,
            MemberAge age,
            LocalDate reservationDate,
            LocalTime reservationTime) {
        this.popupId = popupId;
        this.gender = gender;
        this.age = age;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
    }

    public static Entrance createPopupEnter(
            Long popupId,
            MemberGender gender,
            MemberAge age,
            LocalDate reservationDate,
            LocalTime reservationTime) {
        return Entrance.builder()
                .popupId(popupId)
                .gender(gender)
                .age(age)
                .reservationDate(reservationDate)
                .reservationTime(reservationTime)
                .build();
    }
}
