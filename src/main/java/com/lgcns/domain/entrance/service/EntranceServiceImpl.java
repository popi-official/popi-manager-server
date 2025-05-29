package com.lgcns.domain.entrance.service;

import com.lgcns.domain.entrance.domain.Entrance;
import com.lgcns.domain.entrance.dto.request.EntranceCreateRequest;
import com.lgcns.domain.entrance.dto.response.DailyEntrantCountResponse;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import com.lgcns.domain.manager.domain.Manager;
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
public class EntranceServiceImpl implements EntranceService {

    private final EntranceRepository entranceRepository;
    private final PopupRepository popupRepository;
    private final ManagerUtil managerUtil;

    @Override
    public void createEntrance(EntranceCreateRequest request) {
        Entrance entrance =
                Entrance.createPopupEnter(
                        request.popupId(),
                        request.gender(),
                        request.ageGroup(),
                        request.reservationDate(),
                        request.reservationTime());

        entranceRepository.save(entrance);
    }

    @Override
    public DailyEntrantCountResponse findDailyEntrantCount(Long popupId) {
        Manager manager = managerUtil.getCurrentManager();
        Popup popup = findPopupById(popupId);
        validatePopupOwnership(manager, popup);

        LocalDate today = LocalDate.now();
        return entranceRepository.findDailyEntrantCount(popupId, today);
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
