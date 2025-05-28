package com.lgcns.domain.reservationStats.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservationStats.client.ReservationServiceClient;
import com.lgcns.domain.reservationStats.client.dto.DailyMemberReservationCountResponse;
import com.lgcns.domain.reservationStats.domain.DayOfWeekReservationCount;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeek;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeekReservationCountResponse;
import com.lgcns.domain.reservationStats.dto.response.ReservationStatsResponse;
import com.lgcns.domain.reservationStats.repository.DailyReservationCountRepository;
import com.lgcns.domain.reservationStats.repository.DayOfWeekReservationCountRepository;
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
    private final DayOfWeekReservationCountRepository dayOfWeekReservationCountRepository;
    private final ManagerUtil managerUtil;
    private final ReservationServiceClient reservationServiceClient;

    @Override
    @Transactional(readOnly = true)
    public ReservationStatsResponse getReservationStats(Long popupId) {
        Manager currentManager = managerUtil.getCurrentManager();
        Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        DailyMemberReservationCountResponse dailyReservationCount =
                reservationServiceClient.findDailyMemberReservationCount(popupId);
        Long dailyCount = dailyReservationCount.reservationCount();

        Optional<DayOfWeekReservationCount> dayOfWeekReservationCountList =
                dayOfWeekReservationCountRepository.findByPopupId(popupId);
        List<DayOfWeekReservationCountResponse> chart =
                convertToChart(dayOfWeekReservationCountList);

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

    private List<DayOfWeekReservationCountResponse> convertToChart(
            Optional<DayOfWeekReservationCount> countOpt) {
        DayOfWeekReservationCount count = countOpt.orElse(null);
        List<DayOfWeekReservationCountResponse> chart = new ArrayList<>();

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

            chart.add(DayOfWeekReservationCountResponse.of(day, reservedCount));
        }

        return chart;
    }
}
