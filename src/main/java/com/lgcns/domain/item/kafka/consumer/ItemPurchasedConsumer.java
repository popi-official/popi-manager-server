package com.lgcns.domain.item.kafka.consumer;

import com.lgcns.domain.item.kafka.message.ItemPurchasedMessage;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.notification.event.dto.NotificationEvent;
import com.lgcns.domain.orderItem.event.dto.OrderItemEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemPurchasedConsumer {

    private static final String TOPIC = "item-purchased-topic";

    private final ItemRepository itemRepository;
    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(
            topics = TOPIC,
            groupId = "item-purchased",
            containerFactory = "itemPurchasedKafkaListenerContainerFactory")
    public void decreaseStock(ItemPurchasedMessage message) {
        for (ItemPurchasedMessage.Item item : message.items()) {
            itemRepository
                    .findWithPopupAndManagerById(item.itemId())
                    .ifPresent(
                            foundItem -> {
                                foundItem.decreaseStockAndIncreaseSales(item.quantity());
                                itemRepository.save(foundItem);
                                if (foundItem.checkOutOfStockAndAlarmed()) {
                                    eventPublisher.publishEvent(
                                            OrderItemEvent.of(foundItem.getId()));
                                    eventPublisher.publishEvent(
                                            NotificationEvent.of(foundItem.getPopup(), foundItem));
                                }
                            });
        }
    }
}
