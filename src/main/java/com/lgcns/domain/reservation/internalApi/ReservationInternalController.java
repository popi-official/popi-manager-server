package com.lgcns.domain.reservation.internalApi;

import com.lgcns.domain.reservation.dto.response.MonthlyReservationResponse;
import com.lgcns.domain.reservation.dto.response.ReservationInfoResponse;
import com.lgcns.domain.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("internal/reservations")
@RequiredArgsConstructor
@Tag(name = "15. 예약 Internal API", description = "예약 관련 Internal API입니다.")
public class ReservationInternalController {

    private final ReservationService reservationService;

    @GetMapping("/{popupId}")
    @Operation(summary = "해당 달에 대한 예약 목록 조회", description = "팝업스토어 ID와 달을 통해 예약 정보를 조회합니다.")
    public MonthlyReservationResponse findReservationByIdAndDate(
            @PathVariable Long popupId, @RequestParam String date) {
        return reservationService.findReservationByIdAndDate(popupId, date);
    }

    @GetMapping("/{reservationId}")
    @Operation(summary = "예약 상세 정보 조회", description = "예약 ID를 통해 예약의 상세 정보를 조회합니다.")
    public ReservationInfoResponse findReservationById(@PathVariable Long reservationId) {
        return reservationService.findReservationById(reservationId);
    }
}
