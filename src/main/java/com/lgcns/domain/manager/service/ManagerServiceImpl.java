package com.lgcns.domain.manager.service;

import com.lgcns.domain.auth.repository.RefreshTokenRepository;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.dto.request.ManagerCreateRequest;
import com.lgcns.domain.manager.exception.ManagerErrorCode;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.util.ManagerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ManagerUtil managerUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void createManager(ManagerCreateRequest request) {

        if (managerRepository.existsByUsername(request.username())) {
            throw new CustomException(ManagerErrorCode.DUPLICATE_USERNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Manager manager = Manager.createManager(request.username(), encodedPassword);

        managerRepository.save(manager);
    }

    @Override
    public void logoutManager() {
        final Manager currentManager = managerUtil.getCurrentManager();

        refreshTokenRepository
                .findById(currentManager.getId())
                .ifPresent(refreshTokenRepository::delete);
    }
}
