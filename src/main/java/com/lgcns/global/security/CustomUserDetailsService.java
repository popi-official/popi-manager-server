package com.lgcns.global.security;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.exception.ManagerErrorCode;
import com.lgcns.domain.manager.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ManagerRepository managerRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager =
                managerRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () ->
                                        new BadCredentialsException(
                                                ManagerErrorCode.MANAGER_NOT_FOUND.getMessage()));

        return PrincipalDetails.from(manager);
    }
}
