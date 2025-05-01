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

    private String refreshToken;

    @TimeToLive private long ttl;

    @Builder
    private RefreshToken(Long managerId, String refreshToken, long ttl) {
        this.managerId = managerId;
        this.refreshToken = refreshToken;
        this.ttl = ttl;
    }
}
