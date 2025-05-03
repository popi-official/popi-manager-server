package com.lgcns.domain.auth.service;

import com.lgcns.domain.auth.domain.RefreshToken;
import com.lgcns.domain.auth.dto.AccessTokenDto;
import com.lgcns.domain.auth.dto.RefreshTokenDto;
import com.lgcns.domain.auth.exception.AuthErrorCode;
import com.lgcns.domain.auth.repository.RefreshTokenRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.domain.ManagerRole;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createAccessToken(Long managerId, ManagerRole managerRole) {
        return jwtUtil.generateAccessToken(managerId, managerRole);
    }

    public String createRefreshToken(Long managerId) {
        String token = jwtUtil.generateRefreshToken(managerId);

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .managerId(managerId)
                        .token(token)
                        .ttl(jwtUtil.getRefreshTokenExpirationTime())
                        .build();
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public AccessTokenDto retrieveAccessToken(String accessTokenValue) {
        try {
            return jwtUtil.parseAccessToken(accessTokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    public RefreshTokenDto reissueRefreshToken(RefreshTokenDto oldRefreshTokenDto) {
        RefreshToken refreshToken =
                refreshTokenRepository
                        .findById(oldRefreshTokenDto.managerId())
                        .orElseThrow(
                                () -> new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

        RefreshTokenDto refreshTokenDto =
                jwtUtil.generateRefreshTokenDto(refreshToken.getManagerId());
        refreshToken.updateRefreshToken(refreshTokenDto.refreshTokenValue(), refreshTokenDto.ttl());

        refreshTokenRepository.save(refreshToken);

        return refreshTokenDto;
    }

    public AccessTokenDto reissueAccessToken(Manager manager) {
        return jwtUtil.generateAccessTokenDto(manager.getId(), manager.getRole());
    }

    public RefreshTokenDto validateRefreshToken(String refreshToken) {
        return jwtUtil.parseRefreshToken(refreshToken);
    }
}
