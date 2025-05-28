package com.lgcns.domain.reservationStats.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ReservationStatsResponse(
        @Schema(description = "금일 예약 수", example = "100") Long reservedCount,
        @Schema(description = "요일별 예약 수", example = "[{'day': '월', 'reservedCount': 10},...]")
                List<DayOfWeekReservationCountResponse> chart) {

    public static ReservationStatsResponse of(
            Long reservedCount, List<DayOfWeekReservationCountResponse> chart) {
        return new ReservationStatsResponse(reservedCount, chart);
    }
}
