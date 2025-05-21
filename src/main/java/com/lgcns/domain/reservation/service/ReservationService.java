package com.lgcns.domain.reservation.service;

import com.lgcns.domain.reservation.dto.response.MonthlyReservationDto;

public interface ReservationService {

    MonthlyReservationDto findReservationByIdAndDate(Long popupId, String date);
}
