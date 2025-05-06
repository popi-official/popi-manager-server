package com.lgcns.domain.auth.service;

import com.lgcns.domain.auth.dto.response.TokenReissueResponse;

public interface AuthService {
    TokenReissueResponse reissueToken(String refreshTokenValue);
}
