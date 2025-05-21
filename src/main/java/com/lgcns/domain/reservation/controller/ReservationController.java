package com.lgcns.domain.reservation.controller;

import com.lgcns.domain.reservation.dto.MonthlyReservationResponse;
import com.lgcns.domain.reservation.service.ReservationService;
import com.lgcns.global.common.annotation.RawResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "15. 예약 API", description = "예약 관련 API입니다.")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/popups/{popupId}")
    @RawResponse
    @Operation(summary = "예약 조회", description = "팝업스토어 ID와 달을 통해 예약 정보를 조회합니다.")
    public MonthlyReservationResponse reservationListFind(
            @PathVariable Long popupId, @RequestParam String date) {
        return reservationService.findReservationByIdAndDate(popupId, date);
    }
}
