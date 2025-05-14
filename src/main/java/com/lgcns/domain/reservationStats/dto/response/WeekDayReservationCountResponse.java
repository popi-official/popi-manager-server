package com.lgcns.domain.reservationStats.dto.response;

public record WeekDayReservationCountResponse(DayOfWeek day, Integer reservedCount) {

    public static WeekDayReservationCountResponse of(DayOfWeek day, Integer reservedCount) {
        return new WeekDayReservationCountResponse(day, reservedCount);
    }
}
