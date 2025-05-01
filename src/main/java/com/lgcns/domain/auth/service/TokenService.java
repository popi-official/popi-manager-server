package com.lgcns.domain.auth.service;

import com.lgcns.domain.auth.domain.Manager;
import com.lgcns.domain.auth.domain.RefreshToken;
import com.lgcns.domain.auth.repository.RefreshTokenRepository;
import com.lgcns.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createAccessToken(Manager manager) {
        return jwtUtil.generateAccessToken(manager);
    }

    public String createRefreshToken(Manager manager) {
        String token = jwtUtil.generateRefreshToken(manager);

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .managerId(manager.getId())
                        .token(token)
                        .ttl(jwtUtil.getRefreshTokenExpirationTime())
                        .build();
        refreshTokenRepository.save(refreshToken);

        return token;
    }
}
