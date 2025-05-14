package com.lgcns.domain.reservationStats.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservationStats.domain.DailyReservationCount;
import com.lgcns.domain.reservationStats.domain.WeekDayReservationCount;
import com.lgcns.domain.reservationStats.dto.response.ReservationStatsResponse;
import com.lgcns.domain.reservationStats.dto.response.WeekDayReservationCountResponse;
import com.lgcns.domain.reservationStats.repository.DailyReservationCountRepository;
import com.lgcns.domain.reservationStats.repository.WeekDayReservationCountRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationStatsServiceImpl implements ReservationStatsService {

    private final PopupRepository popupRepository;
    private final DailyReservationCountRepository dailyReservationCountRepository;
    private final WeekDayReservationCountRepository weekDayReservationCountRepository;
    private final ManagerUtil managerUtil;

    @Override
    public ReservationStatsResponse getReservationStats(Long popupId) {
        Manager currentManager = managerUtil.getCurrentManager();
        Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        Optional<DailyReservationCount> dailyReservationCount =
                dailyReservationCountRepository.findByPopupId(popupId);
        int dailyCount =
                dailyReservationCount.map(DailyReservationCount::getReservationCount).orElse(0);

        Optional<WeekDayReservationCount> weekDayReservationCountList =
                weekDayReservationCountRepository.findByPopupId(popupId);
        List<WeekDayReservationCountResponse> chart = convertToChart(weekDayReservationCountList);

        return ReservationStatsResponse.of(dailyCount, chart);
    }

    private Popup findPopupById(Long popupId) {
        return popupRepository
                .findById(popupId)
                .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));
    }

    private void validatePopupOwnership(Manager manager, Popup popup) {
        if (!popup.getManager().equals(manager)) {
            throw new CustomException(PopupErrorCode.POPUP_UNAUTHORIZED);
        }
    }

    public List<WeekDayReservationCountResponse> convertToChart(
            Optional<WeekDayReservationCount> count) {
        List<WeekDayReservationCountResponse> chart = new ArrayList<>();
        if (count.isEmpty()) {
            chart.add(WeekDayReservationCountResponse.of("월요일", 0));
            chart.add(WeekDayReservationCountResponse.of("화요일", 0));
            chart.add(WeekDayReservationCountResponse.of("수요일", 0));
            chart.add(WeekDayReservationCountResponse.of("목요일", 0));
            chart.add(WeekDayReservationCountResponse.of("금요일", 0));
            chart.add(WeekDayReservationCountResponse.of("토요일", 0));
            chart.add(WeekDayReservationCountResponse.of("일요일", 0));
            return chart;
        }
        chart.add(WeekDayReservationCountResponse.of("월요일", count.get().getMondayCount()));
        chart.add(WeekDayReservationCountResponse.of("화요일", count.get().getTuesdayCount()));
        chart.add(WeekDayReservationCountResponse.of("수요일", count.get().getWednesdayCount()));
        chart.add(WeekDayReservationCountResponse.of("목요일", count.get().getThursdayCount()));
        chart.add(WeekDayReservationCountResponse.of("금요일", count.get().getFridayCount()));
        chart.add(WeekDayReservationCountResponse.of("토요일", count.get().getSaturdayCount()));
        chart.add(WeekDayReservationCountResponse.of("일요일", count.get().getSundayCount()));
        return chart;
    }
}
