package com.lgcns.domain.entrance.kafka.consumer;

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

    private static final String TOPIC = "member-entered-topic";

    private final EntranceRepository entranceRepository;

    @KafkaListener(
            topics = TOPIC,
            groupId = "member-entered",
            containerFactory = "memberEnteredConcurrentKafkaListenerContainerFactory")
    public void createEntrance(MemberEnteredMessage message) {

        Entrance entrance =
                Entrance.createPopupEnter(
                        message.popupId(),
                        message.gender(),
                        message.age(),
                        message.reservationDate(),
                        message.reservationTime());

        entranceRepository.save(entrance);
    }
}
