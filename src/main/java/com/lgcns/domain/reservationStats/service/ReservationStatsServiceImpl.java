package com.lgcns.domain.reservationStats.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservationStats.domain.DailyReservationCount;
import com.lgcns.domain.reservationStats.domain.WeekDayReservationCount;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeek;
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
    @Transactional(readOnly = true)
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

    private List<WeekDayReservationCountResponse> convertToChart(
            Optional<WeekDayReservationCount> countOpt) {
        WeekDayReservationCount count = countOpt.orElse(null);
        List<WeekDayReservationCountResponse> chart = new ArrayList<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            int reservedCount = 0;
            if (count != null) {
                reservedCount =
                        switch (day) {
                            case MONDAY -> count.getMondayCount();
                            case TUESDAY -> count.getTuesdayCount();
                            case WEDNESDAY -> count.getWednesdayCount();
                            case THURSDAY -> count.getThursdayCount();
                            case FRIDAY -> count.getFridayCount();
                            case SATURDAY -> count.getSaturdayCount();
                            case SUNDAY -> count.getSundayCount();
                        };
            }

            chart.add(WeekDayReservationCountResponse.of(day, reservedCount));
        }

        return chart;
    }
}
