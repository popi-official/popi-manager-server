package com.lgcns.domain.reservation.service;

import com.lgcns.domain.reservation.dto.response.MonthlyReservationResponse;
import com.lgcns.domain.reservation.dto.response.ReservationInfoResponse;

public interface ReservationService {

    MonthlyReservationResponse findReservationByIdAndDate(Long popupId, String date);

    ReservationInfoResponse findReservationById(Long reservationId);
}
