package com.lgcns.global.util;

import com.lgcns.domain.manager.domain.ManagerRole;
import com.lgcns.infra.jwt.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;

    public String generateAccessToken(Long managerId, ManagerRole role) {
        Date issuedAt = new Date();
        Date expiredAt =
                new Date(issuedAt.getTime() + jwtProperties.accessTokenExpirationMilliTime());
        return buildAccessToken(managerId, role, issuedAt, expiredAt);
    }

    public String generateRefreshToken(Long managerId) {
        Date issuedAt = new Date();
        Date expiredAt =
                new Date(issuedAt.getTime() + jwtProperties.refreshTokenExpirationMilliTime());
        return buildRefreshToken(managerId, issuedAt, expiredAt);
    }

    public long getRefreshTokenExpirationTime() {
        return jwtProperties.refreshTokenExpirationTime();
    }

    private Key getAccessTokenKey() {
        return Keys.hmacShaKeyFor(jwtProperties.accessTokenSecret().getBytes());
    }

    private Key getRefreshTokenKey() {
        return Keys.hmacShaKeyFor(jwtProperties.refreshTokenSecret().getBytes());
    }

    private String buildAccessToken(
            Long managerId, ManagerRole role, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .setIssuer(jwtProperties.issuer())
                .setSubject(managerId.toString())
                .claim("authorities", role.getRole())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(getAccessTokenKey())
                .compact();
    }

    private String buildRefreshToken(Long managerId, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .setIssuer(jwtProperties.issuer())
                .setSubject(managerId.toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(getRefreshTokenKey())
                .compact();
    }
}
