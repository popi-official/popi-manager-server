package com.lgcns.domain.auth.service;

import com.lgcns.domain.auth.domain.Manager;
import com.lgcns.domain.auth.domain.PrincipalDetails;
import com.lgcns.domain.auth.exception.ManagerErrorCode;
import com.lgcns.domain.auth.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final ManagerRepository managerRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager =
                managerRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () ->
                                        new BadCredentialsException(
                                                ManagerErrorCode.USER_NOT_FOUND.getMessage()));

        return PrincipalDetails.from(manager);
    }
}
