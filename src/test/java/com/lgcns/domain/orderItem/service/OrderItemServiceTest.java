package com.lgcns.domain.orderItem.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.notification.domain.Notification;
import com.lgcns.domain.notification.repository.NotificationRepository;
import com.lgcns.domain.orderItem.domian.OrderItem;
import com.lgcns.domain.orderItem.domian.OrderItemStatus;
import com.lgcns.domain.orderItem.repository.OrderItemRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class OrderItemServiceTest extends IntegrationTest {

    @Autowired private OrderItemService orderItemService;

    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private ManagerRepository managerRepository;
    @Autowired private PopupRepository popupRepository;
    @Autowired private ItemRepository itemRepository;

    private Manager ownerManager;
    private Popup popup;
    private Item item;

    @BeforeEach
    void setUp() {
        ownerManager =
                managerRepository.save(Manager.createManager("testUsername", "testPassword"));

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
        popupRepository.save(popup);

        item = Item.createItem(popup, "testItem", "https://bucket/이미지.jpg", 1000, 5, 10, "a2");
        itemRepository.save(item);
    }

    @Nested
    class 상품이_재고가_떨어졌을_때 {

        @Test
        @Transactional
        void 상품_발주와_알림을_생성한다() {
            // given
            Long itemId = item.getId();

            // when
            orderItemService.createOrderItem(itemId);

            // then
            OrderItem orderItem = orderItemRepository.findAll().get(0);
            Notification notification = notificationRepository.findAll().get(0);

            Assertions.assertAll(
                    () -> assertThat(orderItemRepository.count()).isEqualTo(1),
                    () -> assertThat(orderItem.getItem()).isEqualTo(item),
                    () -> assertThat(orderItem.getRealCount()).isEqualTo(-1),
                    () -> assertThat(orderItem.getStatus()).isEqualTo(OrderItemStatus.PENDING),
                    () -> assertThat(orderItem.getItem().getIsAlarmed()).isTrue(),
                    () -> assertThat(notificationRepository.count()).isEqualTo(1),
                    () -> assertThat(notification.getItemId()).isEqualTo(itemId),
                    () -> assertThat(notification.getPopupId()).isEqualTo(popup.getId()),
                    () -> assertThat(notification.getManagerId()).isEqualTo(ownerManager.getId()),
                    () -> assertThat(notification.getItemName()).isEqualTo(item.getName()),
                    () ->
                            assertThat(notification.getMinStock())
                                    .isEqualTo(item.getMinStock() + item.getAverageSales()));
        }

        @Test
        void 존재하지_않는_상품에_대한_발주를_시도하면_예외가_발생한다() {
            // given
            Long nonExistentItemId = -999L;

            // when, then
            Assertions.assertThrows(
                    CustomException.class,
                    () -> orderItemService.createOrderItem(nonExistentItemId),
                    ItemErrorCode.ITEM_NOT_FOUND.getMessage());
        }
    }
}
