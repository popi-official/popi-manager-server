package com.lgcns.domain.reservation.service;

import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservation.dto.response.DailyReservation;
import com.lgcns.domain.reservation.dto.response.MonthlyReservationDto;
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
    public MonthlyReservationDto findReservationByIdAndDate(Long popupId, String date) {
        Popup popup =
                popupRepository
                        .findById(popupId)
                        .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));

        List<DailyReservation> dailyReservationList =
                reservationRepository.findReservationByIdAndDate(popupId, date);

        return MonthlyReservationDto.of(
                popup.getPopupStartDate(),
                popup.getPopupEndDate(),
                popup.getTimeCapacity(),
                dailyReservationList);
    }
}
