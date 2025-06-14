package com.lgcns.domain.reservationStats.service;

import com.lgcns.domain.reservationStats.dto.response.ReservationStatsResponse;

public interface ReservationStatsService {

    ReservationStatsResponse getReservationStats(Long popupId);

    void updateAllDayOfWeekReservationStats();
}
