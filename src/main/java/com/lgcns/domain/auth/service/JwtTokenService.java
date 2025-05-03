package com.lgcns.domain.auth.service;

import com.lgcns.domain.auth.domain.RefreshToken;
import com.lgcns.domain.auth.repository.RefreshTokenRepository;
import com.lgcns.domain.manager.domain.ManagerRole;
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
}
