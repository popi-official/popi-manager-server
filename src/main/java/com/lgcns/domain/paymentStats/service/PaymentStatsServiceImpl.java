package com.lgcns.domain.paymentStats.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.paymentStats.client.PaymentServiceClient;
import com.lgcns.domain.paymentStats.domain.AveragePeriod;
import com.lgcns.domain.paymentStats.domain.PaymentStats;
import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import com.lgcns.domain.paymentStats.repository.PaymentStatsRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
    private final PaymentServiceClient paymentServiceClient;

    @Override
    @Transactional(readOnly = true)
    public AverageAmountResponse findLatestAverageAmount(Long popupId) {
        final Manager currentManager = managerUtil.getCurrentManager();
        final Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        return paymentStatsRepository.findLatestAverageAmountByPopupId(popupId);
    }

    @Override
    public void createPaymentStats() {
        List<Long> popupIds = popupRepository.findAllPopupIds();

        List<PaymentStats> statsList = new ArrayList<>();

        for (Long popupId : popupIds) {
            try {
                AverageAmountResponse response = paymentServiceClient.findAverageAmount(popupId);

                PaymentStats totalStats =
                        PaymentStats.createPaymentStats(
                                popupId,
                                response.totalAverageAmount(),
                                AveragePeriod.TOTAL,
                                LocalDate.now(),
                                LocalTime.now());

                PaymentStats todayStats =
                        PaymentStats.createPaymentStats(
                                popupId,
                                response.todayAverageAmount(),
                                AveragePeriod.TODAY,
                                LocalDate.now(),
                                LocalTime.now());

                statsList.add(totalStats);
                statsList.add(todayStats);

            } catch (Exception e) {
                log.warn("Failed to prepare stats for popupId: {}", popupId, e);
            }
        }

        if (!statsList.isEmpty()) {
            try {
                paymentStatsRepository.bulkInsertPaymentStats(statsList);
            } catch (Exception e) {
                log.error("Failed to insert payment stats batch", e);
            }
        }
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
