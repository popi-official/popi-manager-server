package com.lgcns.domain.reservation.service;

import com.lgcns.domain.reservation.dto.response.MonthlyReservationResponse;

public interface ReservationService {

    MonthlyReservationResponse findReservationByIdAndDate(Long popupId, String date);
}
