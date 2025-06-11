package com.lgcns.domain.orderItem.service;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.orderItem.domian.OrderItem;
import com.lgcns.domain.orderItem.dto.request.OrderItemUpdateRequest;
import com.lgcns.domain.orderItem.dto.response.OrderItemResponse;
import com.lgcns.domain.orderItem.repository.OrderItemRepository;
import com.lgcns.global.common.response.SliceResponse;
import com.lgcns.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;

    @Override
    public void createOrderItem(Long itemId) {
        Item item =
                itemRepository
                        .findById(itemId)
                        .orElseThrow(() -> new CustomException(ItemErrorCode.ITEM_NOT_FOUND));

        OrderItem orderItem = OrderItem.createOrderItem(item);
        orderItemRepository.save(orderItem);
        item.updateIsAlarmed(true);
    }

    @Override
    public SliceResponse<OrderItemResponse> findOrderItemsByPopupId(
            Long popupId, Long lastOrderItemId, int size) {
        return SliceResponse.from(
                orderItemRepository.findOrderItemsByPopupIdWithPagination(
                        popupId, lastOrderItemId, size));
    }

    @Override
    public void updateOrderItem(Long orderItemId, OrderItemUpdateRequest orderItemUpdateRequest) {
        OrderItem orderItem =
                orderItemRepository
                        .findById(orderItemId)
                        .orElseThrow(() -> new CustomException(ItemErrorCode.ITEM_NOT_FOUND));

        orderItem.updateOrderItem(orderItemUpdateRequest.qty(), orderItemUpdateRequest.status());
        orderItem.getItem().updateStock(orderItemUpdateRequest.qty());
        orderItemRepository.save(orderItem);
    }
}
