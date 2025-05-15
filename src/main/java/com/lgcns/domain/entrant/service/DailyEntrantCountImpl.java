package com.lgcns.domain.entrant.service;

import com.lgcns.domain.entrant.domain.DailyEntrantCount;
import com.lgcns.domain.entrant.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrant.repository.DailyEntrantCountRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyEntrantCountImpl implements DailyEntrantCountService {

    private final DailyEntrantCountRepository dailyEntrantCountRepository;
    private final PopupRepository popupRepository;

    private final ManagerUtil managerUtil;

    @Override
    public DailyEntrantCountResponse findDailyEntrantCount(Long popupId) {
        Manager manager = managerUtil.getCurrentManager();
        Popup popup = findPopupById(popupId);
        validatePopupOwnership(manager, popup);

        Optional<DailyEntrantCount> dailyEntrantCount =
                dailyEntrantCountRepository.findByPopupId(popupId);

        return dailyEntrantCount
                .map(entrantCount -> DailyEntrantCountResponse.of(entrantCount.getEntrantCount()))
                .orElseGet(() -> DailyEntrantCountResponse.of(0));
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
}
