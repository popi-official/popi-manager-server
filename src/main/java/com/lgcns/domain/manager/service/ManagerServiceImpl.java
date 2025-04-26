package com.lgcns.domain.manager.service;

import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.dto.Request.ManagerCreateRequest;
import com.lgcns.domain.manager.exception.ManagerErrorCode;
import com.lgcns.domain.manager.repository.ManagerRepository;
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
    @Transactional(readOnly = true)
    public void createManager(ManagerCreateRequest request) {
        // 사용자 이름 중복 체크
        if (managerRepository.existsByUsername(request.username())) {
            throw new CustomException(ManagerErrorCode.DUPLICATE_USERNAME);
        }

        // 비밇번호 인코딩
        String encodedPassword = passwordEncoder.encode(request.password());

        // Manager 엔티티 저장
        Manager manager =
                Manager.createManager(request.username(), encodedPassword, passwordEncoder);
        Manager savedManager = managerRepository.save(manager);
    }
}
