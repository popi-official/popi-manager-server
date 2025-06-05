package com.lgcns.domain.entrance.kafka;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.entrance.domain.Entrance;
import com.lgcns.domain.entrance.domain.MemberAge;
import com.lgcns.domain.entrance.domain.MemberGender;
import com.lgcns.domain.entrance.kafka.message.MemberEnteredMessage;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
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
        brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"},
        topics = "member-entered-topic")
public class MemberEnteredConsumerTest extends IntegrationTest {

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired private EntranceRepository entranceRepository;

    @Test
    void 카프카_메세지를_받으면_Entrance를_저장한다() throws Exception {
        // given
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, MemberEnteredMessage> producerFactory =
                new DefaultKafkaProducerFactory<>(
                        producerProps, new StringSerializer(), new JsonSerializer<>());
        KafkaTemplate<String, MemberEnteredMessage> kafkaTemplate =
                new KafkaTemplate<>(producerFactory);

        MemberEnteredMessage message =
                new MemberEnteredMessage(
                        1L,
                        MemberGender.MALE,
                        MemberAge.TWENTIES,
                        LocalDate.now(),
                        LocalTime.now().truncatedTo(ChronoUnit.SECONDS));

        // when
        kafkaTemplate.send("member-entered-topic", message);

        // then
        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(
                        () -> {
                            List<Entrance> entrances = entranceRepository.findAll();
                            Assertions.assertThat(entrances)
                                    .as("Entrance 가 저장되어야 합니다.")
                                    .isNotEmpty();

                            Entrance entrance = entrances.get(0);
                            org.junit.jupiter.api.Assertions.assertAll(
                                    () ->
                                            Assertions.assertThat(entrance.getPopupId())
                                                    .isEqualTo(message.popupId()),
                                    () ->
                                            Assertions.assertThat(entrance.getGender())
                                                    .isEqualTo(message.gender()),
                                    () ->
                                            Assertions.assertThat(entrance.getAge())
                                                    .isEqualTo(message.age()),
                                    () ->
                                            Assertions.assertThat(entrance.getReservationDate())
                                                    .isEqualTo(message.reservationDate()),
                                    () ->
                                            Assertions.assertThat(entrance.getReservationTime())
                                                    .isEqualTo(message.reservationTime()));
                        });
    }
}
