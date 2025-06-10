package com.lgcns.domain.notification.service;

import static com.lgcns.domain.notification.domain.Popularity.HOT;
import static com.lgcns.domain.notification.domain.Popularity.NORMAL;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.notification.domain.Notification;
import com.lgcns.domain.notification.dto.response.NotificationResponse;
import com.lgcns.domain.notification.repository.NotificationRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final ManagerUtil managerUtil;
    private final PopupRepository popupRepository;
    private final NotificationRepository notificationRepository;
    private final ItemRepository itemRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private static final int TOP_ITEMS_LIMIT = 3;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> findNotificationList(Long popupId) {
        Manager currentManager = managerUtil.getCurrentManager();
        Popup popup = findPopupById(popupId);
        validatePopupOwnership(currentManager, popup);

        return notificationRepository.findByManagerIdAndPopupId(currentManager.getId(), popupId);
    }

    @Override
    public void sendLowStockMessage(Popup popup, Item item) {
        final Long managerId = popup.getManager().getId();
        final Long popupId = popup.getId();

        List<Item> top3Items = itemRepository.findTopItemsByPopupId(popupId, TOP_ITEMS_LIMIT);

        boolean isHot =
                top3Items.stream().anyMatch(topItem -> topItem.getId().equals(item.getId()));

        Notification notification =
                Notification.createNotification(
                        managerId,
                        popupId,
                        item.getId(),
                        item.getName(),
                        isHot ? HOT : NORMAL,
                        item.getMinStock());

        notificationRepository.save(notification);

        NotificationResponse response =
                NotificationResponse.of(
                        notification.getId(),
                        notification.getPopularity(),
                        notification.getItemName(),
                        notification.getMinStock(),
                        notification.getCreatedAt());

        messagingTemplate.convertAndSend("/topic/" + managerId + "/popup/" + popupId, response);
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
