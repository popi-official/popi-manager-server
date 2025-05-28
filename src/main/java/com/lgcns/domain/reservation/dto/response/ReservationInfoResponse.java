package com.lgcns.domain.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationInfoResponse(
        Long popupId, LocalDate reservationDate, LocalTime reservationTime) {
    public static ReservationInfoResponse of(
            Long popupId, LocalDate reservationDate, LocalTime reservationTime) {
        return new ReservationInfoResponse(popupId, reservationDate, reservationTime);
    }
}
