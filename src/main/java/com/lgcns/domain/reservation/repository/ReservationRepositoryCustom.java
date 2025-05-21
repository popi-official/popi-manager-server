package com.lgcns.domain.reservation.repository;

import com.lgcns.domain.reservation.domain.Reservation;
import java.util.List;

public interface ReservationRepositoryCustom {

    void bulkInsertReservations(List<Reservation> reservations);
}
