package com.lgcns.domain.survey.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.domain.popup.repository.PopupRepository;
import com.lgcns.domain.survey.domain.MemberAnswer;
import com.lgcns.domain.survey.domain.Survey;
import com.lgcns.domain.survey.kafka.message.MemberAnswerMessage;
import com.lgcns.domain.survey.kafka.message.dto.SurveyChoiceDto;
import com.lgcns.domain.survey.repository.MemberAnswerRepository;
import com.lgcns.domain.survey.repository.SurveyRepository;
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
@EmbeddedKafka(partitions = 1, topics = MemberAnswerConsumerTest.TOPIC)
public class MemberAnswerConsumerTest extends IntegrationTest {

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired private ManagerRepository managerRepository;

    @Autowired private PopupRepository popupRepository;

    @Autowired private SurveyRepository surveyRepository;

    @Autowired private MemberAnswerRepository memberAnswerRepository;

    static final String TOPIC = "member-answer-topic";

    private static final int MAX_SURVEY = 4;

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

        for (int i = 1; i <= MAX_SURVEY; i++) {
            Survey survey = surveyRepository.save(Survey.createSurvey(popup, i));
        }
    }

    @Test
    void 설문_응답_메시지를_수신_후_MemberAnswer를_저장한다() {
        // given
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        ProducerFactory<String, MemberAnswerMessage> producerFactory =
                new DefaultKafkaProducerFactory<>(
                        producerProps, new StringSerializer(), new JsonSerializer<>());

        KafkaTemplate<String, MemberAnswerMessage> kafkaTemplate =
                new KafkaTemplate<>(producerFactory);

        List<SurveyChoiceDto> message =
                List.of(
                        new SurveyChoiceDto(1L, 1L),
                        new SurveyChoiceDto(2L, 5L),
                        new SurveyChoiceDto(3L, 9L),
                        new SurveyChoiceDto(4L, 13L));

        // when
        kafkaTemplate.send(TOPIC, new MemberAnswerMessage(1L, message));

        // then
        await().atMost(Duration.ofSeconds(20))
                .untilAsserted(
                        () -> {
                            List<MemberAnswer> memberAnswers = memberAnswerRepository.findAll();

                            assertAll(
                                    () -> assertThat(memberAnswers).hasSize(message.size()),
                                    () ->
                                            assertThat(memberAnswers)
                                                    .extracting(MemberAnswer::getMemberId)
                                                    .containsOnly(1L),
                                    () -> {
                                        for (SurveyChoiceDto surveyChoice : message) {
                                            boolean matched =
                                                    memberAnswers.stream()
                                                            .anyMatch(
                                                                    answer ->
                                                                            answer.getSurvey()
                                                                                            .getId()
                                                                                            .equals(
                                                                                                    surveyChoice
                                                                                                            .surveyId())
                                                                                    && answer.getChoiceId()
                                                                                            .equals(
                                                                                                    surveyChoice
                                                                                                            .choiceId()));
                                            assertThat(matched).isTrue();
                                        }
                                    });
                        });
    }
}
