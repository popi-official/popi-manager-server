package com.lgcns.domain.orderItem.service;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.orderItem.domian.OrderItem;
import com.lgcns.domain.orderItem.repository.OrderItemRepository;
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
}
