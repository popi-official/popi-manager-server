package com.lgcns.domain.paymentStats.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.paymentStats.dto.response.PaymentAverageResponse;
import com.lgcns.domain.paymentStats.repository.PaymentStatsRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentStatsServiceImpl implements PaymentStatsService {

    private final PaymentStatsRepository paymentStatsRepository;
    private final PopupRepository popupRepository;
    private final ManagerUtil managerUtil;

    @Override
    @Transactional(readOnly = true)
    public PaymentAverageResponse getPaymentAverages(Long popupId) {
        Manager currentManager = managerUtil.getCurrentManager();
        Popup popup = findPopupById(popupId);

        validatePopupOwnership(currentManager, popup);

        final LocalDate today = LocalDate.now();

        return paymentStatsRepository.getPaymentAverages(popupId, today);
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
