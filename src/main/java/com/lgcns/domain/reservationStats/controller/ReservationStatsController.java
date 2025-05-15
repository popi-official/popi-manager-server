package com.lgcns.domain.reservationStats.controller;

import com.lgcns.domain.reservationStats.dto.response.ReservationStatsResponse;
import com.lgcns.domain.reservationStats.service.ReservationStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/popups/{popupId}/dashboard/reservations")
@RequiredArgsConstructor
@Tag(name = "9. 예약 분석 API", description = "예약 분석 관련 API 입니다.")
public class ReservationStatsController {

    private final ReservationStatsService reservationStatsService;

    @GetMapping
    @Operation(
            summary = "예약 통계 조회",
            description = "예약 통계 조회 API 입니다. 예약 통계는 금일 예약 수와 총 예약 수에 따른 차트 데이터를 포함합니다.")
    public ReservationStatsResponse reservationStatsGet(@PathVariable Long popupId) {
        return reservationStatsService.getReservationStats(popupId);
    }
}
