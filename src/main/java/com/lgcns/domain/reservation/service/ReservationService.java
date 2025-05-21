package com.lgcns.domain.reservation.service;

import com.lgcns.domain.reservation.dto.MonthlyReservationResponse;

public interface ReservationService {

    MonthlyReservationResponse findReservationByIdAndDate(Long popupId, String date);
}
