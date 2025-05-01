package com.lgcns.domain.auth.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash(value = "refreshToken")
public class RefreshToken {
    @Id private Long managerId;

    private String token;

    @TimeToLive private long ttl;

    @Builder
    private RefreshToken(Long managerId, String token, long ttl) {
        this.managerId = managerId;
        this.token = token;
        this.ttl = ttl;
    }
}
