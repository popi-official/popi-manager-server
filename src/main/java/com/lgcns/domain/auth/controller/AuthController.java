package com.lgcns.domain.auth.controller;

import com.lgcns.domain.auth.dto.request.ManagerCreateRequest;
import com.lgcns.domain.auth.service.ManagerService;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1. 인증 API", description = "로그인 및 인증 관련 API")
public class AuthController {

    private final ManagerService managerService;

    @PostMapping("/signup")
    @Operation(summary = "운영자 회원가입", description = "운영자가 직접 가입하지 않음")
    public ResponseEntity<Void> managerCreate(@RequestBody @Valid ManagerCreateRequest request) {
        managerService.createManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
