package com.lgcns.domain.congestionStats.service;

import com.lgcns.domain.congestionStats.dto.response.CongestionStatsResponse;
import com.lgcns.domain.congestionStats.repository.CongestionStatsRepository;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CongestionStatsServiceImpl implements CongestionStatsService {

    private final CongestionStatsRepository congestionStatsRepository;
    private final PopupRepository popupRepository;
    private final EntranceRepository entranceRepository;
    private final ManagerUtil managerUtil;

    @Override
    public CongestionStatsResponse getCongestionStats(Long popupId) {

        Manager manager = managerUtil.getCurrentManager();
        Popup popup = findPopupById(popupId);

        validatePopupOwnership(manager, popup);

        return congestionStatsRepository.findDailyCongestionStats(
                popupId, popup.getRunOpenTime(), popup.getRunCloseTime());
    }

    @Override
    public List<Long> findTargetPopupIds() {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        // 운영 중인 팝업 필터링
        List<Long> popupIds = popupRepository.findAllPopupIdsAfterPopupStartTime(nowDate, nowTime);
        // 입장 내역이 존재하는 팝업 필터링
        List<Long> popupIdsWithEntrances = entranceRepository.findPopupIdsWithEntrances(popupIds);
        // 중복된 혼잡도 분석이 존재하지 않는 팝업 필터링
        return new ArrayList<>(
                congestionStatsRepository.findPopupIdsWithoutCongestionStats(
                        popupIdsWithEntrances, nowDate, nowTime));
    }

    private void validatePopupOwnership(Manager manager, Popup popup) {
        if (!popup.getManager().equals(manager)) {
            throw new CustomException(PopupErrorCode.POPUP_UNAUTHORIZED);
        }
    }

    private Popup findPopupById(Long popupId) {
        return popupRepository
                .findById(popupId)
                .orElseThrow(() -> new CustomException(PopupErrorCode.POPUP_NOT_FOUND));
    }
}
