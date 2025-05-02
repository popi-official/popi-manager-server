package com.lgcns.domain.auth.service;

import com.lgcns.domain.auth.domain.Manager;
import com.lgcns.domain.auth.dto.request.ManagerCreateRequest;
import com.lgcns.domain.auth.exception.ManagerErrorCode;
import com.lgcns.domain.auth.repository.ManagerRepository;
import com.lgcns.global.error.exception.CustomException;
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

    @Override
    public void createManager(ManagerCreateRequest request) {

        if (managerRepository.existsByUsername(request.username())) {
            throw new CustomException(ManagerErrorCode.DUPLICATE_USERNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Manager manager = Manager.createManager(request.username(), encodedPassword);

        managerRepository.save(manager);
    }
}
