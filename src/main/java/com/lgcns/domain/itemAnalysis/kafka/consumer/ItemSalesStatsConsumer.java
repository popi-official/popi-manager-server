package com.lgcns.domain.itemAnalysis.kafka.consumer;

import com.lgcns.domain.item.kafka.message.ItemPurchasedMessage;
import com.lgcns.domain.itemAnalysis.domain.ItemSalesStats;
import com.lgcns.domain.itemAnalysis.repository.ItemSalesStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemSalesStatsConsumer {

    private static final String TOPIC = "item-purchased-topic";
    private final ItemSalesStatsRepository itemSalesStatsRepository;

    @KafkaListener(
            topics = TOPIC,
            groupId = "sales-stats",
            containerFactory = "itemPurchasedKafkaListenerContainerFactory")
    public void updateSalesStats(ItemPurchasedMessage message) {
        for (ItemPurchasedMessage.Item item : message.items()) {
            ItemSalesStats stats =
                    itemSalesStatsRepository
                            .findByPopupIdAndItemId(message.popupId(), item.itemId())
                            .orElseGet(
                                    () ->
                                            ItemSalesStats.createItemSalesStats(
                                                    message.popupId(), item.itemId(), 0));

            stats.addSalesVolume(item.quantity());
            itemSalesStatsRepository.save(stats);
        }
    }
}
