package com.lgcns.domain.notification.service.NotificationServiceTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.notification.domain.Notification;
import com.lgcns.domain.notification.domain.Popularity;
import com.lgcns.domain.notification.dto.NotificationResponseDTO;
import com.lgcns.domain.notification.repository.NotificationRepository;
import com.lgcns.domain.notification.service.NotificationService;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class NotificationServiceTest extends IntegrationTest {

    @Autowired private NotificationService notificationService;

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private ManagerRepository managerRepository;
    @Autowired private PopupRepository popupRepository;
    @Autowired private ItemRepository itemRepository;

    private Manager ownerManager;
    private Manager otherManager;
    private Popup popup;

    @BeforeEach
    void setUp() {
        ownerManager =
                managerRepository.save(Manager.createManager("testUsername", "testPassword"));
        otherManager =
                managerRepository.save(Manager.createManager("otherManager", "testPassword"));

        setAuthentication(ownerManager);

        popup =
                Popup.createPopup(
                        ownerManager,
                        "testPopup",
                        "https://bucket/이미지.jpg",
                        LocalDate.parse("2025-01-01"),
                        LocalDate.parse("2025-01-31"),
                        LocalDateTime.parse("2025-01-01T10:00:00"),
                        LocalDateTime.parse("2025-01-31T20:00:00"),
                        LocalTime.parse("10:00:00"),
                        LocalTime.parse("20:00:00"),
                        100,
                        20,
                        "서울특별시 강남구 테헤란로 123",
                        "3층 A호",
                        37.123456,
                        127.123456);
        popup = popupRepository.save(popup);
    }

    @Nested
    class 알림_조회 {

        @Test
        @Transactional
        void 알림이_있는_경우_알림_조회에_성공한다() {
            // given
            Long managerId = ownerManager.getId();
            Long popupId = popup.getId();
            Long itemId =
                    itemRepository
                            .save(
                                    Item.createItem(
                                            popup,
                                            "testItem",
                                            "https://bucket/1234.jpg",
                                            5000,
                                            50,
                                            100,
                                            "a1"))
                            .getId();

            Notification notification =
                    notificationRepository.save(
                            Notification.createNotification(
                                    managerId, popupId, itemId, "testItem", Popularity.HOT, 100));

            // when
            List<NotificationResponseDTO> notificationList =
                    notificationService.findNotificationList(popupId);

            // then
            assertAll(
                    () -> assertThat(notificationList.size()).isEqualTo(1),
                    () ->
                            assertThat(notificationList.get(0).notificationId())
                                    .isEqualTo(notification.getId()),
                    () ->
                            assertThat(notificationList.get(0).itemName())
                                    .isEqualTo(notification.getItemName()),
                    () ->
                            assertThat(notificationList.get(0).popularity())
                                    .isEqualTo(notification.getPopularity()),
                    () ->
                            assertThat(notificationList.get(0).minStock())
                                    .isEqualTo(notification.getMinStock()),
                    () ->
                            assertThat(notificationList.get(0).notifiedAt())
                                    .isEqualTo(notification.getCreatedAt()));
        }

        @Test
        @Transactional
        void 알림이_없는_경우_알림_조회에_성공한다() {
            // given
            Long popupId = popup.getId();

            // when
            List<NotificationResponseDTO> notificationList =
                    notificationService.findNotificationList(popupId);

            // then
            assertEquals(0, notificationList.size());
        }

        @Test
        @Transactional
        void 다른_관리자가_소유한_팝업에_대한_알림_조회는_실패한다() {
            // given
            Long popupId = popup.getId();
            setAuthentication(otherManager);

            // when & then
            assertThrows(
                    CustomException.class,
                    () -> notificationService.findNotificationList(popupId),
                    PopupErrorCode.POPUP_UNAUTHORIZED.getMessage());
        }

        @Test
        @Transactional
        void 존재하지_않는_팝업에_대한_알림_조회는_실패한다() {
            // given
            Long popupId = -1L;

            // when & then
            assertThrows(
                    CustomException.class,
                    () -> notificationService.findNotificationList(popupId),
                    PopupErrorCode.POPUP_NOT_FOUND.getMessage());
        }
    }
}
