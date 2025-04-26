package com.lgcns.domain.manager.dto;

import jakarta.validation.constraints.NotBlank;

public record ManagerCreateRequest(
        @NotBlank(message = "username은 필수입니다.") String username,
        @NotBlank(message = "password는 필수입니다.") String password) {}
