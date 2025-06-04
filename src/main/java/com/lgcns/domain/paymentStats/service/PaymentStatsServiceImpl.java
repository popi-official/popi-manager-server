package com.lgcns.domain.paymentStats.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import com.lgcns.domain.paymentStats.repository.PaymentStatsRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentStatsServiceImpl implements PaymentStatsService {

    private final ManagerUtil managerUtil;
    private final PopupRepository popupRepository;
    private final PaymentStatsRepository paymentStatsRepository;

    @Override
    public AverageAmountResponse findLatestAverageAmount(Long popupId) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        return paymentStatsRepository.findLatestAverageAmountByPopupId(popupId);
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
