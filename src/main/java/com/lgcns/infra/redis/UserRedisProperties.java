package com.lgcns.infra.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.data.user-redis")
public record UserRedisProperties(String host, int port, String password) {}
