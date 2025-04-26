package com.lgcns.domain.manager.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.dto.Request.ManagerCreateRequest;
import com.lgcns.domain.manager.repository.ManagerRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

class ManagerServiceTest extends IntegrationTest {
    @Autowired private ManagerRepository managerRepository;
    @Autowired private ManagerService managerService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Nested
    class 운영자_회원가입을_할_때 {

        @Test
        @Transactional
        void 유효한_입력_값이면_회원가입에_성공한다() {
            // given
            String username = "testmanager";
            String password = "password123";
            ManagerCreateRequest request = new ManagerCreateRequest(username, password);

            // when
            managerService.createManager(request);

            // then
            Manager savedManager =
                    managerRepository
                            .findByUsername(username)
                            .orElseThrow(() -> new AssertionError("Manager 조회 실패"));
            assertThat(savedManager.getUsername()).isEqualTo(username);
            assertThat(passwordEncoder.matches(password, savedManager.getPassword())).isTrue();
        }
    }
}
