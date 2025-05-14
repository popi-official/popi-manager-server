package com.lgcns.domain.reservationStats.dto.response;

import java.util.List;

public record ReservationStatsResponse(
        Integer reservedCount, List<WeekDayReservationCountResponse> chart) {

    public static ReservationStatsResponse of(
            Integer reservedCount, List<WeekDayReservationCountResponse> chart) {
        return new ReservationStatsResponse(reservedCount, chart);
    }
}
