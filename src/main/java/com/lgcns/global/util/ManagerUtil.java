package com.lgcns.global.util;

import com.lgcns.domain.auth.exception.AuthErrorCode;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.exception.ManagerErrorCode;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerUtil {

    private final ManagerRepository managerRepository;

    public Manager getCurrentManager() {
        return managerRepository
                .findById(getCurrentManagerId())
                .orElseThrow(() -> new CustomException(ManagerErrorCode.MANAGER_NOT_FOUND));
    }

    public Long getCurrentManagerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.AUTH_NOT_FOUND);
        }
    }
}
