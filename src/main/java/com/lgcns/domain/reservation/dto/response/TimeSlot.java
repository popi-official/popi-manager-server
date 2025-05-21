package com.lgcns.domain.reservation.dto.response;

import java.time.LocalTime;

public record TimeSlot(Long reservationId, LocalTime time) {
    public static TimeSlot of(Long reservationId, LocalTime time) {
        return new TimeSlot(reservationId, time);
    }
}
