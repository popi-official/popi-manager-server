package com.lgcns.domain.auth.service;

import com.lgcns.domain.auth.dto.AccessTokenDto;
import com.lgcns.domain.auth.dto.RefreshTokenDto;
import com.lgcns.domain.auth.dto.response.TokenReissueResponse;
import com.lgcns.domain.auth.exception.AuthErrorCode;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.exception.ManagerErrorCode;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final JwtTokenService jwtTokenService;
    private final ManagerRepository managerRepository;

    @Override
    public TokenReissueResponse reissueToken(String refreshTokenValue) {
        RefreshTokenDto oldRefreshTokenDto =
                jwtTokenService.validateRefreshToken(refreshTokenValue);

        if (oldRefreshTokenDto == null) {
            throw new CustomException(AuthErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        RefreshTokenDto newRefreshTokenDto =
                jwtTokenService.reissueRefreshToken(oldRefreshTokenDto);
        AccessTokenDto newAccessTokenDto =
                jwtTokenService.reissueAccessToken(getManager(newRefreshTokenDto));

        return TokenReissueResponse.of(
                newAccessTokenDto.accessTokenValue(), newRefreshTokenDto.refreshTokenValue());
    }

    private Manager getManager(RefreshTokenDto refreshTokenDto) {
        return managerRepository
                .findById(refreshTokenDto.managerId())
                .orElseThrow(() -> new CustomException(ManagerErrorCode.MANAGER_NOT_FOUND));
    }
}
