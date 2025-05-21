package com.lgcns.domain.reservation.repository;

import com.lgcns.domain.reservation.domain.Reservation;
import com.lgcns.domain.reservation.dto.response.DailyReservation;
import java.util.List;

public interface ReservationRepositoryCustom {

    void bulkInsertReservations(List<Reservation> reservations);

    List<DailyReservation> findReservationByIdAndDate(Long popupId, String date);
}
