package com.lgcns.domain.entrance.kafka.config;

import com.lgcns.domain.entrance.kafka.message.MemberEnteredMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class MemberEnteredConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, MemberEnteredMessage> memberEnteredConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<MemberEnteredMessage> deserializer =
                new JsonDeserializer<>(MemberEnteredMessage.class);
        deserializer.addTrustedPackages("*");
        deserializer.setRemoveTypeHeaders(false);
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                configProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MemberEnteredMessage>
            memberEnteredMessageConcurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MemberEnteredMessage>
                kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(memberEnteredConsumerFactory());
        return kafkaListenerContainerFactory;
    }
}
