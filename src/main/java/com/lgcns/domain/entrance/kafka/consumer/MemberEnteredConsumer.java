package com.lgcns.domain.entrance.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgcns.domain.entrance.domain.Entrance;
import com.lgcns.domain.entrance.kafka.message.MemberEnteredMessage;
import com.lgcns.domain.entrance.repository.EntranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberEnteredConsumer {

    private final EntranceRepository entranceRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "member-entered-topic", groupId = "entrance-group")
    public void createEntrance(String message) {
        try {
            MemberEnteredMessage kafkaMessage =
                    objectMapper.readValue(message, MemberEnteredMessage.class);

            Entrance entrance =
                    Entrance.createPopupEnter(
                            kafkaMessage.popupId(),
                            kafkaMessage.gender(),
                            kafkaMessage.age(),
                            kafkaMessage.reservationDate(),
                            kafkaMessage.reservationTime());

            entranceRepository.save(entrance);

        } catch (Exception e) {
            // 로그만 남기거나 DLQ 처리 등
            log.error("[Kafka] 메시지 파싱 실패: {}", message, e);
        }
    }
}
