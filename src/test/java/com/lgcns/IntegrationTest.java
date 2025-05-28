package com.lgcns;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.global.security.PrincipalDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
public abstract class IntegrationTest {
    @Autowired protected DatabaseCleaner databaseCleaner;
    @Autowired private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();

        wireMockServer.stop();
        wireMockServer.start();
    }

    @AfterEach
    void afterEach() {
        wireMockServer.resetAll();
    }

    // 지정된 관리자로 로그인 상태로 변경
    protected void setAuthentication(Manager manager) {
        UserDetails userDetails = new PrincipalDetails(manager.getId(), manager.getRole(), null);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
