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
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationStatsServiceImpl implements ReservationStatsService {

    private final PopupRepository popupRepository;
    private final DayOfWeekReservationCountRepository dayOfWeekReservationCountRepository;
    private final ManagerUtil managerUtil;
    private final ReservationServiceClient reservationServiceClient;
    private final EntityManager entityManager;

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
        Map<Long, DayOfWeekReservationStatsResponse> allStatsMap =
                getAllDayOfWeekStatsFromReservationService();

        if (allStatsMap.isEmpty()) {
            return;
        }

        bulkUpsertWithCaseWhen(allStatsMap);
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
            throw new CustomException(
                    ReservationStatsErrorCode.RESERVATION_SERVICE_CONNECTION_FAILED);
        } catch (Exception e) {
            throw new CustomException(ReservationStatsErrorCode.RESERVATION_SERVICE_ERROR);
        }
    }

    private void bulkUpsertWithCaseWhen(Map<Long, DayOfWeekReservationStatsResponse> allStatsMap) {
        List<Long> popupIds = new ArrayList<>(allStatsMap.keySet());
        List<Long> existingPopupIds =
                dayOfWeekReservationCountRepository.findExistingPopupIds(popupIds);

        if (!existingPopupIds.isEmpty()) {
            bulkUpdateExistingData(allStatsMap, existingPopupIds);
        }

        Set<Long> existingSet = new HashSet<>(existingPopupIds);
        List<DayOfWeekReservationCount> newEntities =
                allStatsMap.values().stream()
                        .filter(stats -> !existingSet.contains(stats.popupId()))
                        .map(this::createNewCount)
                        .toList();

        if (!newEntities.isEmpty()) {
            dayOfWeekReservationCountRepository.saveAll(newEntities);
        }
    }

    private void bulkUpdateExistingData(
            Map<Long, DayOfWeekReservationStatsResponse> allStatsMap, List<Long> existingPopupIds) {

        StringBuilder jpql = new StringBuilder();
        jpql.append("UPDATE DayOfWeekReservationCount d SET ");

        String[] fields = {
            "mondayCount",
            "tuesdayCount",
            "wednesdayCount",
            "thursdayCount",
            "fridayCount",
            "saturdayCount",
            "sundayCount"
        };

        for (int i = 0; i < fields.length; i++) {
            if (i > 0) jpql.append(", ");

            jpql.append("d.").append(fields[i]).append(" = CASE d.popupId ");

            for (Long popupId : existingPopupIds) {
                DayOfWeekReservationStatsResponse stats = allStatsMap.get(popupId);
                int value = getFieldValue(stats, fields[i]);
                jpql.append("WHEN :popupId")
                        .append(popupId)
                        .append(" THEN ")
                        .append(value)
                        .append(" ");
            }

            jpql.append("ELSE d.").append(fields[i]).append(" END");
        }

        jpql.append(", d.updatedAt = CURRENT_TIMESTAMP ");
        jpql.append("WHERE d.popupId IN :existingPopupIds");

        Query query = entityManager.createQuery(jpql.toString());

        for (Long popupId : existingPopupIds) {
            query.setParameter("popupId" + popupId, popupId);
        }
        query.setParameter("existingPopupIds", existingPopupIds);

        query.executeUpdate();
    }

    private int getFieldValue(DayOfWeekReservationStatsResponse stats, String fieldName) {
        return switch (fieldName) {
            case "mondayCount" -> stats.mondayCount();
            case "tuesdayCount" -> stats.tuesdayCount();
            case "wednesdayCount" -> stats.wednesdayCount();
            case "thursdayCount" -> stats.thursdayCount();
            case "fridayCount" -> stats.fridayCount();
            case "saturdayCount" -> stats.saturdayCount();
            case "sundayCount" -> stats.sundayCount();
            default -> throw new IllegalArgumentException("Unknown field: " + fieldName);
        };
    }

    private DayOfWeekReservationCount createNewCount(DayOfWeekReservationStatsResponse stats) {
        return DayOfWeekReservationCount.createDayOfWeekReservationCount(
                stats.popupId(),
                stats.mondayCount(),
                stats.tuesdayCount(),
                stats.wednesdayCount(),
                stats.thursdayCount(),
                stats.fridayCount(),
                stats.saturdayCount(),
                stats.sundayCount());
    }
}
