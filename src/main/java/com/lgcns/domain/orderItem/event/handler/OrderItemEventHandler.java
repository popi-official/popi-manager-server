package com.lgcns.domain.orderItem.event.handler;

import com.lgcns.domain.orderItem.event.dto.OrderItemEvent;
import com.lgcns.domain.orderItem.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class OrderItemEventHandler {

    private final OrderItemService orderItemService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderItemCreatedEvent(OrderItemEvent event) {
        orderItemService.createOrderItem(event.itemId());
    }
}
