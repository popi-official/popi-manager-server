package com.lgcns.domain.itemAnalysis.kafka;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.item.kafka.message.ItemPurchasedMessage;
import com.lgcns.domain.itemAnalysis.domain.ItemSalesStats;
import com.lgcns.domain.itemAnalysis.repository.ItemSalesStatsRepository;
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
import java.util.Optional;
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
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"},
        topics = "item-purchased-topic")
public class ItemSalesStatsConsumerTest extends IntegrationTest {

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
    private KafkaTemplate<String, ItemPurchasedMessage> kafkaTemplate;

    @Autowired private ManagerRepository managerRepository;
    @Autowired private PopupRepository popupRepository;
    @Autowired private ItemSalesStatsRepository itemSalesStatsRepository;

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
                                LocalDate.now().plusDays(5),
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
    }

    @Test
    void Kafka_메시지_수신_후_판매량_통계가_생성된다() {
        // given
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, ItemPurchasedMessage> producerFactory =
                new DefaultKafkaProducerFactory<>(
                        producerProps, new StringSerializer(), new JsonSerializer<>());

        KafkaTemplate<String, ItemPurchasedMessage> kafkaTemplate =
                new KafkaTemplate<>(producerFactory);

        List<ItemPurchasedMessage.Item> items =
                List.of(new ItemPurchasedMessage.Item(1L, 2), new ItemPurchasedMessage.Item(2L, 3));
        int amount = 50000;
        LocalDateTime purchasedAt = LocalDateTime.of(2025, 6, 5, 0, 0);

        ItemPurchasedMessage message = new ItemPurchasedMessage(1L, items, amount, purchasedAt);

        // when
        kafkaTemplate.send("item-purchased-topic", message);

        // then
        await().atMost(Duration.ofSeconds(15))
                .untilAsserted(
                        () -> {
                            Optional<ItemSalesStats> stats1 =
                                    itemSalesStatsRepository.findByPopupIdAndItemId(1L, 1L);
                            Optional<ItemSalesStats> stats2 =
                                    itemSalesStatsRepository.findByPopupIdAndItemId(1L, 2L);

                            assertThat(stats1.isPresent()).isTrue();
                            assertThat(stats1.get().getSalesVolume()).isEqualTo(2);

                            assertThat(stats2.isPresent()).isTrue();
                            assertThat(stats2.get().getSalesVolume()).isEqualTo(3);
                        });
    }

    @Test
    void Kafka_메시지_수신_후_기존_판매량_통계_테이블이_업데이트된다() {
        // given
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, ItemPurchasedMessage> producerFactory =
                new DefaultKafkaProducerFactory<>(
                        producerProps, new StringSerializer(), new JsonSerializer<>());

        KafkaTemplate<String, ItemPurchasedMessage> kafkaTemplate =
                new KafkaTemplate<>(producerFactory);

        ItemSalesStats existingStats = ItemSalesStats.createItemSalesStats(1L, 1L, 5);
        itemSalesStatsRepository.save(existingStats);

        List<ItemPurchasedMessage.Item> items = List.of(new ItemPurchasedMessage.Item(1L, 3));
        int amount = 30000;
        LocalDateTime purchasedAt = LocalDateTime.of(2025, 6, 5, 0, 0);

        ItemPurchasedMessage message = new ItemPurchasedMessage(1L, items, amount, purchasedAt);

        // when
        kafkaTemplate.send("item-purchased-topic", message);

        // then
        await().atMost(Duration.ofSeconds(15))
                .untilAsserted(
                        () -> {
                            Optional<ItemSalesStats> updatedStats =
                                    itemSalesStatsRepository.findByPopupIdAndItemId(1L, 1L);

                            assertThat(updatedStats.isPresent()).isTrue();
                            assertThat(updatedStats.get().getSalesVolume()).isEqualTo(8);
                        });
    }
}
