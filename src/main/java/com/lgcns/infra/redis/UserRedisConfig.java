package com.lgcns.infra.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
@EnableRedisRepositories(
        basePackages = "com.lgcns.domain.reservation.repository",
        redisTemplateRef = "userRedisTemplate")
@RequiredArgsConstructor
public class UserRedisConfig {

    private final UserRedisProperties userRedisProperties;

    @Bean(name = "userRedisConnectionFactory")
    public RedisConnectionFactory userRedisConnectionFactory() {
        RedisStandaloneConfiguration userRedisStandaloneConfig =
                new RedisStandaloneConfiguration(
                        userRedisProperties.host(), userRedisProperties.port());
        userRedisStandaloneConfig.setDatabase(userRedisProperties.database());
        if (!userRedisProperties.password().isBlank()) {
            userRedisStandaloneConfig.setPassword(userRedisProperties.password());
        }

        LettuceClientConfiguration lettuceClientConfig =
                LettuceClientConfiguration.builder()
                        .commandTimeout(Duration.ofSeconds(1))
                        .shutdownTimeout(Duration.ZERO)
                        .build();

        return new LettuceConnectionFactory(userRedisStandaloneConfig, lettuceClientConfig);
    }

    @Bean(name = "userRedisTemplate")
    public RedisTemplate<Long, Object> userRedisTemplate(
            @Qualifier("userRedisConnectionFactory") RedisConnectionFactory factory) {

        RedisTemplate<Long, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new GenericToStringSerializer<>(Long.class));
        template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
        return template;
    }
}
