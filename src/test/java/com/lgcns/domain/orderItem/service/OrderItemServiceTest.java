package com.lgcns.domain.orderItem.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.orderItem.domian.OrderItem;
import com.lgcns.domain.orderItem.domian.OrderItemStatus;
import com.lgcns.domain.orderItem.dto.request.OrderItemUpdateRequest;
import com.lgcns.domain.orderItem.dto.response.OrderItemResponse;
import com.lgcns.domain.orderItem.repository.OrderItemRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.global.common.response.SliceResponse;
import com.lgcns.global.error.exception.CustomException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class OrderItemServiceTest extends IntegrationTest {

    @Autowired private OrderItemService orderItemService;

    @Autowired private OrderItemRepository orderItemRepository;
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

            Assertions.assertAll(
                    () -> assertThat(orderItemRepository.count()).isEqualTo(1),
                    () -> assertThat(orderItem.getItem()).isEqualTo(item),
                    () -> assertThat(orderItem.getRealCount()).isEqualTo(-1),
                    () -> assertThat(orderItem.getStatus()).isEqualTo(OrderItemStatus.PENDING),
                    () -> assertThat(orderItem.getItem().getIsAlarmed()).isTrue());
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

    @Nested
    class 발주_목록을_조회할_때 {

        @Test
        void 팝업_ID로_발주_목록을_조회한다() {
            // given
            Long popupId = popup.getId();
            for (int i = 0; i < 10; i++) {
                Item testItem =
                        itemRepository.save(
                                Item.createItem(
                                        popup,
                                        "testItem" + i,
                                        "https://bucket/이미지" + i + ".jpg",
                                        1000 + i,
                                        5 + i,
                                        10 + i,
                                        "a2"));
                OrderItem orderItem = OrderItem.createOrderItem(testItem);
                orderItemRepository.save(orderItem);
            }

            // when
            SliceResponse<OrderItemResponse> result1 =
                    orderItemService.findOrderItemsByPopupId(popupId, null, 8);
            List<OrderItemResponse> result1ResponseList = result1.content();
            Long lastOrderItemId1 =
                    result1ResponseList.get(result1ResponseList.size() - 1).orderItemId();
            SliceResponse<OrderItemResponse> result2 =
                    orderItemService.findOrderItemsByPopupId(popupId, lastOrderItemId1, 8);
            List<OrderItemResponse> result2ResponseList = result2.content();

            // then
            Assertions.assertAll(
                    () -> assertThat(result1).isNotNull(),
                    () -> org.assertj.core.api.Assertions.assertThat(result1.content()).hasSize(8),
                    () -> assertThat(result1.isLast()).isFalse(),
                    () -> {
                        for (int i = 0; i < result1ResponseList.size(); i++) {
                            OrderItemResponse response = result1ResponseList.get(i);

                            assertThat(response.itemName()).isEqualTo("testItem" + (9 - i));
                            assertThat(response.recommendCount()).isEqualTo(5 + (9 - i));
                            assertThat(response.realCount()).isEqualTo(-1);
                            assertThat(response.status()).isEqualTo(OrderItemStatus.PENDING);
                        }
                    },
                    () -> assertThat(result2).isNotNull(),
                    () -> org.assertj.core.api.Assertions.assertThat(result2.content()).hasSize(2),
                    () -> assertThat(result2.isLast()).isTrue(),
                    () -> {
                        for (int i = 0; i < result2ResponseList.size(); i++) {
                            OrderItemResponse response = result2ResponseList.get(i);

                            assertThat(response.itemName()).isEqualTo("testItem" + (1 - i));
                            assertThat(response.recommendCount()).isEqualTo(5 + (1 - i));
                            assertThat(response.realCount()).isEqualTo(-1);
                            assertThat(response.status()).isEqualTo(OrderItemStatus.PENDING);
                        }
                    });
        }
    }

    @Nested
    class 발주_상태를_변경할_때 {

        @Test
        void 발주_상태를_변경한다() {
            // given
            OrderItem orderItem = OrderItem.createOrderItem(item);
            orderItemRepository.save(orderItem);
            Long orderItemId = orderItem.getId();

            // when
            orderItemService.updateOrderItem(
                    orderItemId, new OrderItemUpdateRequest(10, OrderItemStatus.COMPLETED));

            // then
            OrderItem updatedOrderItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedOrderItem.getRealCount()).isEqualTo(10);
            assertThat(updatedOrderItem.getStatus()).isEqualTo(OrderItemStatus.COMPLETED);
            assertThat(updatedOrderItem.getItem().getIsAlarmed()).isFalse();
            assertThat(updatedOrderItem.getItem().getStock()).isEqualTo(15);
        }

        @Test
        void 존재하지_않는_발주_ID로_상태를_변경하면_예외가_발생한다() {
            // given
            Long nonExistentOrderItemId = -999L;

            // when, then
            Assertions.assertThrows(
                    CustomException.class,
                    () ->
                            orderItemService.updateOrderItem(
                                    nonExistentOrderItemId,
                                    new OrderItemUpdateRequest(10, OrderItemStatus.COMPLETED)),
                    ItemErrorCode.ITEM_NOT_FOUND.getMessage());
        }

        @Test
        void 발주_상태가_COMPLETED일_때_실제_발주_수량이_음수이면_예외가_발생한다() {
            // given
            OrderItem orderItem = OrderItem.createOrderItem(item);
            orderItemRepository.save(orderItem);
            Long orderItemId = orderItem.getId();

            // when, then
            Assertions.assertThrows(
                    CustomException.class,
                    () ->
                            orderItemService.updateOrderItem(
                                    orderItemId,
                                    new OrderItemUpdateRequest(-5, OrderItemStatus.COMPLETED)),
                    ItemErrorCode.INVALID_RESTOCK.getMessage());
        }
    }
}
