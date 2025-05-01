package com.lgcns.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgcns.domain.auth.dto.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String LOGIN_REQUEST_URL = "/api/v1/login";
    private static final String LOGIN_REQUEST_HTTP_METHOD = "POST";
    private static final String LOGIN_REQUEST_CONTENT_TYPE = "application/json";
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(LOGIN_REQUEST_URL, LOGIN_REQUEST_HTTP_METHOD);

    private final Validator validator;

    public AuthenticationFilter(Validator validator) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
        this.validator = validator;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        if (!isApplicationJson(request.getContentType())) {
            throw new AuthenticationServiceException(
                    "Not Supported Content-Type: " + request.getContentType());
        }

        LoginRequest loginRequest = parseRequest(request);
        return getAuthentication(loginRequest);
    }

    private LoginRequest parseRequest(HttpServletRequest request) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest loginRequest =
                objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(loginRequest);

        if (!violations.isEmpty()) {
            Map<String, String> errorMap =
                    violations.stream()
                            .collect(
                                    Collectors.toMap(
                                            k -> k.getPropertyPath().toString(),
                                            ConstraintViolation::getMessage));
            throw new AuthenticationServiceException(objectMapper.writeValueAsString(errorMap));
        }
        return loginRequest;
    }

    private Authentication getAuthentication(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        loginRequest.username(), loginRequest.password());
        return this.getAuthenticationManager().authenticate(authentication);
    }

    private boolean isApplicationJson(String contentType) {
        return contentType != null && contentType.equals(LOGIN_REQUEST_CONTENT_TYPE);
    }
}
