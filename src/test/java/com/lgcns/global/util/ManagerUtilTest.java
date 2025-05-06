package com.lgcns.global.util;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.global.security.PrincipalDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class ManagerUtilTest extends IntegrationTest {

    @Autowired private ManagerUtil managerUtil;
    @Autowired private ManagerRepository managerRepository;

    private Manager manager;

    @BeforeEach
    void setUp() {
        manager = managerRepository.save(Manager.createManager("testUsername", "testPassword"));

        UserDetails userDetails = new PrincipalDetails(manager.getId(), manager.getRole(), null);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Test
    void 로그인한_매니저의_정보를_조회한다() {
        // when
        Manager currentManager = managerUtil.getCurrentManager();

        // then
        Assertions.assertEquals(manager.getId(), currentManager.getId());
        Assertions.assertEquals(manager.getRole(), currentManager.getRole());
    }
}
