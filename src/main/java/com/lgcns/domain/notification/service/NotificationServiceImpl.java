package com.lgcns.domain.notification.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.notification.domain.Notification;
import com.lgcns.domain.notification.dto.NotificationResponseDTO;
import com.lgcns.domain.notification.repository.NotificationRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final PopupRepository popupRepository;
    private final ManagerUtil managerUtil;

    @Override
    public List<NotificationResponseDTO> findNotificationList(Long popupId) {
        Manager currentManager = managerUtil.getCurrentManager();
        Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        List<Notification> notificationList =
                notificationRepository.findByUserIdAndPopupId(currentManager.getId(), popupId);

        return notificationList.stream()
                .map(
                        notification ->
                                NotificationResponseDTO.of(
                                        notification.getId(),
                                        notification.getPopularity(),
                                        notification.getItemName(),
                                        notification.getMinStock(),
                                        notification.getCreatedAt()))
                .toList();
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
