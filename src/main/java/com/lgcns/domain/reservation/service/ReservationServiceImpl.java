package com.lgcns.domain.reservation.service;

import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservation.domain.Reservation;
import com.lgcns.domain.reservation.dto.response.DailyReservation;
import com.lgcns.domain.reservation.dto.response.MonthlyReservationResponse;
import com.lgcns.domain.reservation.dto.response.ReservationInfoResponse;
import com.lgcns.domain.reservation.exception.ReservationErrorCode;
import com.lgcns.domain.reservation.repository.ReservationRepository;
import com.lgcns.global.error.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PopupRepository popupRepository;

    @Override
    @Transactional(readOnly = true)
    public MonthlyReservationResponse findReservationByIdAndDate(Long popupId, String date) {
        Popup popup =
                popupRepository
                        .findById(popupId)
                        .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));

        List<DailyReservation> dailyReservationList =
                reservationRepository.findReservationByIdAndDate(popupId, date);

        return MonthlyReservationResponse.of(
                popup.getPopupStartDate(),
                popup.getPopupEndDate(),
                popup.getTimeCapacity(),
                dailyReservationList);
    }

    @Override
    public ReservationInfoResponse findReservationById(Long reservationId) {
        Reservation reservation =
                reservationRepository
                        .findById(reservationId)
                        .orElseThrow(
                                () ->
                                        new CustomException(
                                                ReservationErrorCode.RESERVATION_NOT_FOUND));
        return ReservationInfoResponse.of(
                reservation.getPopup().getId(),
                reservation.getReservationDate(),
                reservation.getReservationTime());
    }
}
