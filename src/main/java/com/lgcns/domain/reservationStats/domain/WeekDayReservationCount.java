package com.lgcns.domain.reservationStats.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class WeekDayReservationCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long popupId;

    private int mondayCount;
    private int tuesdayCount;
    private int wednesdayCount;
    private int thursdayCount;
    private int fridayCount;
    private int saturdayCount;
    private int sundayCount;

    @Builder
    private WeekDayReservationCount(
            Long popupId,
            int mondayCount,
            int tuesdayCount,
            int wednesdayCount,
            int thursdayCount,
            int fridayCount,
            int saturdayCount,
            int sundayCount) {
        this.popupId = popupId;
        this.mondayCount = mondayCount;
        this.tuesdayCount = tuesdayCount;
        this.wednesdayCount = wednesdayCount;
        this.thursdayCount = thursdayCount;
        this.fridayCount = fridayCount;
        this.saturdayCount = saturdayCount;
        this.sundayCount = sundayCount;
    }

    public static WeekDayReservationCount createWeekDayReservationCount(
            Long popupId,
            int mondayCount,
            int tuesdayCount,
            int wednesdayCount,
            int thursdayCount,
            int fridayCount,
            int saturdayCount,
            int sundayCount) {
        return WeekDayReservationCount.builder()
                .popupId(popupId)
                .mondayCount(mondayCount)
                .tuesdayCount(tuesdayCount)
                .wednesdayCount(wednesdayCount)
                .thursdayCount(thursdayCount)
                .fridayCount(fridayCount)
                .saturdayCount(saturdayCount)
                .sundayCount(sundayCount)
                .build();
    }
}
