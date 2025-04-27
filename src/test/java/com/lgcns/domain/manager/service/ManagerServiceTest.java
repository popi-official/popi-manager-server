package com.lgcns.domain.manager.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.dto.Request.ManagerCreateRequest;
import com.lgcns.domain.manager.exception.ManagerErrorCode;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.global.error.exception.CustomException;
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

        @Test
        @Transactional
        void 이미_존재하는_운영_이름으로_가입아면_예외가_발생한다() {
            // given
            String username = "existingmanager";
            String password = "password123";

            Manager existingManager = createManager(username, "existingpassword");
            managerRepository.save(existingManager);

            ManagerCreateRequest request = new ManagerCreateRequest(username, password);

            // when & then
            assertThatThrownBy(() -> managerService.createManager(request))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ManagerErrorCode.DUPLICATE_USERNAME);
        }

        private Manager createManager(String username, String plainPassword) {
            String encodedPassword = passwordEncoder.encode(plainPassword);

            return Manager.createManager(username, encodedPassword);
        }
    }
}
