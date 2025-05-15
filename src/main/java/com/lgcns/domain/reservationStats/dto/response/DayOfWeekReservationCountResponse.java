package com.lgcns.domain.reservationStats.dto.response;

public record DayOfWeekReservationCountResponse(DayOfWeek day, Integer reservedCount) {

    public static DayOfWeekReservationCountResponse of(DayOfWeek day, Integer reservedCount) {
        return new DayOfWeekReservationCountResponse(day, reservedCount);
    }
}
