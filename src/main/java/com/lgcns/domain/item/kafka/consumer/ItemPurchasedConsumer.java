package com.lgcns.domain.item.kafka.consumer;

import com.lgcns.domain.item.kafka.message.ItemPurchasedMessage;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemPurchasedConsumer {

    private static final String TOPIC = "item-purchased-topic";

    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

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
                                foundItem.decreaseStock(item.quantity());
                                itemRepository.save(foundItem);

                                if (foundItem.getStock() <= foundItem.getMinStock()) {
                                    notificationService.sendLowStockMessage(
                                            foundItem.getPopup(), foundItem);
                                }
                            });
        }
    }
}
