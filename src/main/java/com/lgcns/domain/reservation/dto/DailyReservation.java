package com.lgcns.domain.reservation.dto;

import java.time.LocalDate;
import java.util.List;

public record DailyReservation(LocalDate reservationDate, List<TimeSlot> timeSlots) {

    public static DailyReservation of(LocalDate reservationDate, List<TimeSlot> timeSlots) {
        return new DailyReservation(reservationDate, timeSlots);
    }
}
