package com.lgcns.domain.visitorStats.service;

import com.lgcns.domain.entrance.dto.response.HourlyEntranceResponse;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.visitorStats.domain.VisitorStats;
import com.lgcns.domain.visitorStats.dto.response.*;
import com.lgcns.domain.visitorStats.repository.VisitorStatsRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorStatsServiceImpl implements VisitorStatsService {

    private final VisitorStatsRepository visitorStatsRepository;
    private final PopupRepository popupRepository;
    private final EntranceRepository entranceRepository;
    private final ManagerUtil managerUtil;

    @Override
    @Transactional(readOnly = true)
    public VisitorAnalysisResponse getVisitorAnalysis(Long popupId) {
        Manager manager = managerUtil.getCurrentManager();

        Popup popup = findPopupById(popupId);

        validatePopupOwnership(manager, popup);

        VisitorStatsResponse response = visitorStatsRepository.getVisitorStatsByPopupId(popupId);

        if (isStatsEmpty(response)) {
            return VisitorAnalysisResponse.of(List.of(), List.of());
        }

        int total = response.maleCount() + response.femaleCount();

        List<CountAndRatioResponse> gender = new ArrayList<>();
        for (GenderType genderType : GenderType.values()) {
            int count = genderType.extractCount(response);
            int ratio = calculateRatio(count, total);
            gender.add(CountAndRatioResponse.of(genderType.getGender(), count, ratio));
        }

        List<CountAndRatioResponse> age = new ArrayList<>();
        for (AgeGroupType ageGroup : AgeGroupType.values()) {
            int count = ageGroup.extractCount(response);
            int ratio = calculateRatio(count, total);
            age.add(CountAndRatioResponse.of(ageGroup.getAgeGroup(), count, ratio));
        }

        return VisitorAnalysisResponse.of(gender, age);
    }

    @Override
    public List<Long> findTargetPopupIds() {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        // 운영 중인 팝업 필터링
        List<Long> popupIds = popupRepository.findAllPopupIdsAfterPopupStartTime(nowDate, nowTime);
        // 입장 내역이 존재하는 팝업 필터링
        Set<Long> popupIdsWithEntrances = entranceRepository.findPopupIdsWithEntrances(popupIds);
        // 중복된 방문자 분석이 존재하지 않는 팝업 필터링
        return new ArrayList<>(
                visitorStatsRepository.findPopupIdsWithoutVisitorStats(
                        popupIdsWithEntrances, nowDate, nowTime));
    }

    @Override
    public VisitorStats convertVisitorStats(Long popupId) {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        HourlyEntranceResponse hourlyEntranceResponse =
                entranceRepository.findHourlyEntrance(popupId, nowDate, nowTime);

        return fromHourlyEntranceResponse(popupId, hourlyEntranceResponse, nowDate, nowTime);
    }

    @Override
    public void createVisitorStats(List<VisitorStats> visitorStatsList) {
        visitorStatsRepository.bulkInsertVisitorStats(visitorStatsList);
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

    private boolean isStatsEmpty(VisitorStatsResponse response) {
        return response == null
                || Stream.of(
                                response.maleCount(),
                                response.femaleCount(),
                                response.teenCount(),
                                response.twentyCount(),
                                response.thirtyCount(),
                                response.fortyCount())
                        .allMatch(Objects::isNull);
    }

    private int calculateRatio(int count, int total) {
        return (int) Math.round((double) count * 100 / total);
    }

    private VisitorStats fromHourlyEntranceResponse(
            Long popupId,
            HourlyEntranceResponse hourlyEntranceResponse,
            LocalDate nowDate,
            LocalTime nowTime) {
        return VisitorStats.createVisitorStats(
                popupId,
                hourlyEntranceResponse.maleCount(),
                hourlyEntranceResponse.femaleCount(),
                hourlyEntranceResponse.teenCount(),
                hourlyEntranceResponse.twentyCount(),
                hourlyEntranceResponse.thirtyCount(),
                hourlyEntranceResponse.fortyCount(),
                nowDate,
                nowTime);
    }
}
