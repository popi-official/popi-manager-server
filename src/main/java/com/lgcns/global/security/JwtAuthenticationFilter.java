package com.lgcns.global.security;

import static com.lgcns.global.common.constants.SecurityConstants.TOKEN_PREFIX;

import com.lgcns.domain.auth.dto.AccessTokenDto;
import com.lgcns.domain.auth.service.JwtTokenService;
import com.lgcns.domain.manager.domain.ManagerRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessTokenHeaderValue = extractAccessTokenFromHeader(request);

        if (accessTokenHeaderValue != null) {
            AccessTokenDto accessTokenDto =
                    jwtTokenService.retrieveAccessToken(accessTokenHeaderValue);

            // AT가 유효하면 통과
            if (accessTokenDto != null) {
                setAuthenticationToken(accessTokenDto.managerId(), accessTokenDto.role());
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationToken(Long managerId, ManagerRole managerRole) {
        UserDetails userDetails =
                User.withUsername(managerId.toString())
                        .password("")
                        .authorities(managerRole.toString())
                        .build();

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private String extractAccessTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.replace(TOKEN_PREFIX, "");
        }

        return null;
    }
}
