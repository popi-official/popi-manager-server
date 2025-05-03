package com.lgcns.domain.auth.dto;

import com.lgcns.domain.manager.domain.ManagerRole;

public record AccessTokenDto(Long managerId, ManagerRole role, String accessTokenValue) {
    public static AccessTokenDto of(Long memberId, ManagerRole role, String accessTokenValue) {
        return new AccessTokenDto(memberId, role, accessTokenValue);
    }
}
