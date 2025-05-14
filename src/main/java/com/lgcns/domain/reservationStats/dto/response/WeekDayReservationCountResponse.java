package com.lgcns.domain.reservationStats.dto.response;

public record WeekDayReservationCountResponse(String day, Integer reservedCount) {

    public static WeekDayReservationCountResponse of(String day, Integer reservedCount) {
        return new WeekDayReservationCountResponse(day, reservedCount);
    }
}
