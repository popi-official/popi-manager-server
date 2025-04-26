package com.lgcns.domain.manager.controller;

import com.lgcns.domain.manager.dto.ManagerCreateRequest;
import com.lgcns.domain.manager.dto.ManagerCreateResponse;
import com.lgcns.domain.manager.service.ManagerService;
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
@RequestMapping()
@RequiredArgsConstructor
@Tag(name = "Manager", description = "운영자 API")
public class ManagerController {
    private final ManagerService managerService;

    @PostMapping("/signup")
    @Operation(summary = "운영자 회원가입", description = "운영자가 직접 가입하지 않음")
    public ResponseEntity<ManagerCreateResponse> managerCreate(
            @RequestBody @Valid ManagerCreateRequest request) {
        ManagerCreateResponse response = managerService.createManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
