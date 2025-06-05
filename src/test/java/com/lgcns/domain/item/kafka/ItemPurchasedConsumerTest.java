package com.lgcns.domain.item.kafka;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.kafka.message.ItemPurchasedMessage;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.repository.PopupRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

@Order(0)
@EmbeddedKafka(partitions = 1, topics = "item-purchased-topic")
class ItemPurchasedConsumerTest extends IntegrationTest {

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
    private KafkaTemplate<String, ItemPurchasedMessage> kafkaTemplate;

    @Autowired private ManagerRepository managerRepository;
    @Autowired private PopupRepository popupRepository;
    @Autowired private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        Manager manager =
                managerRepository.save(Manager.createManager("testManager", "testPassword"));

        Popup popup =
                popupRepository.save(
                        Popup.createPopup(
                                manager,
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
                                127.123456));

        itemRepository.saveAll(
                List.of(
                        new Item(popup, "testName1", "testImageUrl1", 10000, 50, 10, "a1"),
                        new Item(popup, "testName2", "testImageUrl2", 20000, 100, 20, "b1")));
    }

    @Test
    void Kafka_메시지_수신_후_재고가_감소한다() {
        // given
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, ItemPurchasedMessage> producerFactory =
                new DefaultKafkaProducerFactory<>(
                        producerProps, new StringSerializer(), new JsonSerializer<>());

        kafkaTemplate = new KafkaTemplate<>(producerFactory);

        List<ItemPurchasedMessage.Item> items =
                List.of(new ItemPurchasedMessage.Item(1L, 2), new ItemPurchasedMessage.Item(2L, 3));
        int amount = 39000;
        LocalDateTime purchasedAt = LocalDateTime.of(2026, 6, 6, 0, 0);

        ItemPurchasedMessage message = new ItemPurchasedMessage(1L, items, amount, purchasedAt);

        // when
        kafkaTemplate.send("item-purchased-topic", message);

        // then
        await().atMost(Duration.ofSeconds(30))
                .untilAsserted(
                        () -> {
                            assertThat(itemRepository.findById(1L).orElseThrow().getStock())
                                    .isEqualTo(48);
                            assertThat(itemRepository.findById(2L).orElseThrow().getStock())
                                    .isEqualTo(97);
                        });
    }
}
