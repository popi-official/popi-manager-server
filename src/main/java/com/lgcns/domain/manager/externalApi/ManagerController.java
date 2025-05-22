package com.lgcns.domain.manager.externalApi;

import com.lgcns.domain.manager.dto.request.ManagerCreateRequest;
import com.lgcns.domain.manager.service.ManagerService;
import com.lgcns.global.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/managers")
@RequiredArgsConstructor
@Tag(name = "1-2. 운영자 API", description = "운영자 관련 API입니다.")
public class ManagerController {

    private final ManagerService managerService;
    private final CookieUtil cookieUtil;

    @PostMapping
    @Operation(summary = "운영자 계정 생성", description = "운영자가 직접 요청하지 않고, 개발자가 생성 후 전달하는 내부용 API입니다.")
    public ResponseEntity<Void> managerCreate(@RequestBody @Valid ManagerCreateRequest request) {
        managerService.createManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/logout")
    @Operation(summary = "운영자 로그아웃", description = "운영자 로그아웃을 진행합니다.")
    public ResponseEntity<Void> managerLogout() {
        managerService.logoutManager();
        return ResponseEntity.ok().headers(cookieUtil.deleteRefreshTokenCookie()).build();
    }
}
