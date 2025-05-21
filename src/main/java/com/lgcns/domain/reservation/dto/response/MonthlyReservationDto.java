package com.lgcns.domain.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record MonthlyReservationDto(
        @Schema(description = "팝업스토어 예약 시작일", example = "2025-10-01") LocalDate popupOpenDate,
        @Schema(description = "팝업스토어 예약 종료일", example = "2025-10-31") LocalDate popupCloseDate,
        @Schema(description = "시간당 예약 가능 인원", example = "10") Integer timeCapacity,
        @Schema(description = "예약 가능 날짜 및 시간 슬롯") List<DailyReservation> dailyReservations) {

    public static MonthlyReservationDto of(
            LocalDate popupOpenDate,
            LocalDate popupCloseDate,
            Integer timeCapacity,
            List<DailyReservation> dailyReservations) {
        return new MonthlyReservationDto(
                popupOpenDate, popupCloseDate, timeCapacity, dailyReservations);
    }
}
