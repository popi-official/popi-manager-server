package com.lgcns.domain.auth.dto;

public record RefreshTokenDto(Long managerId, String refreshTokenValue, Long ttl) {
    public static RefreshTokenDto of(Long managerId, String refreshTokenValue, Long ttl) {
        return new RefreshTokenDto(managerId, refreshTokenValue, ttl);
    }
}
