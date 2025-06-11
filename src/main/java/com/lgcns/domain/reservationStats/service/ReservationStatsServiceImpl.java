package com.lgcns.domain.reservationStats.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.reservationStats.client.ReservationServiceClient;
import com.lgcns.domain.reservationStats.client.dto.DailyMemberReservationCountResponse;
import com.lgcns.domain.reservationStats.client.dto.DayOfWeekReservationStatsResponse;
import com.lgcns.domain.reservationStats.domain.DayOfWeekReservationCount;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeek;
import com.lgcns.domain.reservationStats.dto.response.DayOfWeekReservationCountResponse;
import com.lgcns.domain.reservationStats.dto.response.ReservationStatsResponse;
import com.lgcns.domain.reservationStats.exception.ReservationStatsErrorCode;
import com.lgcns.domain.reservationStats.repository.DayOfWeekReservationCountRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReservationStatsServiceImpl implements ReservationStatsService {

    private final PopupRepository popupRepository;
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

    @Override
    public void updateAllDayOfWeekReservationStats() {
        List<Popup> allPopups = popupRepository.findAll();
        Map<Long, DayOfWeekReservationStatsResponse> allStatsMap =
                getAllDayOfWeekStatsFromReservationService();
        processPopupStatsUpdate(allPopups, allStatsMap);
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

    private Map<Long, DayOfWeekReservationStatsResponse>
            getAllDayOfWeekStatsFromReservationService() {
        try {
            return reservationServiceClient.getAllDayOfWeekReservationStats();
        } catch (feign.RetryableException e) {
            log.error("예약 서비스 연결 실패: {}", e.getMessage());
            throw new CustomException(
                    ReservationStatsErrorCode.RESERVATION_SERVICE_CONNECTION_FAILED);
        } catch (Exception e) {
            log.error("예약 서비스 오류: {}", e.getMessage());
            throw new CustomException(ReservationStatsErrorCode.RESERVATION_SERVICE_ERROR);
        }
    }

    private void processPopupStatsUpdate(
            List<Popup> allPopups, Map<Long, DayOfWeekReservationStatsResponse> allStatsMap) {

        for (Popup popup : allPopups) {
            try {
                DayOfWeekReservationStatsResponse stats = allStatsMap.get(popup.getId());
                updateSinglePopupStats(popup.getId(), stats);
            } catch (Exception e) {
                log.error("팝업 ID {}의 통계 업데이트 실패: {}", popup.getId(), e.getMessage());
            }
        }
    }

    private void updateSinglePopupStats(Long popupId, DayOfWeekReservationStatsResponse stats) {
        if (stats == null) {
            return;
        }

        if (dayOfWeekReservationCountRepository.existsByPopupId(popupId)) {
            updateExistingRecordDirectly(popupId, stats);
        } else {
            createNewRecord(stats);
        }
    }

    private void updateExistingRecordDirectly(
            Long popupId, DayOfWeekReservationStatsResponse stats) {
        dayOfWeekReservationCountRepository.updateDayOfWeekCounts(
                popupId,
                stats.mondayCount(),
                stats.tuesdayCount(),
                stats.wednesdayCount(),
                stats.thursdayCount(),
                stats.fridayCount(),
                stats.saturdayCount(),
                stats.sundayCount());
    }

    private void createNewRecord(DayOfWeekReservationStatsResponse stats) {
        DayOfWeekReservationCount newRecord =
                DayOfWeekReservationCount.createDayOfWeekReservationCount(
                        stats.popupId(),
                        stats.mondayCount(),
                        stats.tuesdayCount(),
                        stats.wednesdayCount(),
                        stats.thursdayCount(),
                        stats.fridayCount(),
                        stats.saturdayCount(),
                        stats.sundayCount());

        dayOfWeekReservationCountRepository.save(newRecord);
    }
}
