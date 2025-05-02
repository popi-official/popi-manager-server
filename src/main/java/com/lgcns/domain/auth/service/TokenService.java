package com.lgcns.domain.auth.service;

import com.lgcns.domain.auth.domain.RefreshToken;
import com.lgcns.domain.auth.repository.RefreshTokenRepository;
import com.lgcns.domain.manager.domain.ManagerRole;
import com.lgcns.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createAccessToken(String username, ManagerRole managerRole) {
        return jwtUtil.generateAccessToken(username, managerRole);
    }

    public String createRefreshToken(String username, Long managerId) {
        String token = jwtUtil.generateRefreshToken(username);

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
