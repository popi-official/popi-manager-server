package com.lgcns.domain.manager.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "매니저 생성 요청 DTO")
public record ManagerCreateRequest(
        @Schema(description = "사용자 이름", example = "manager123", required = true)
                @NotBlank(message = "username은 필수입니다.")
                String username,
        @Schema(description = "비밀번호", example = "password123", required = true)
                @NotBlank(message = "password는 필수입니다.")
                String password) {}
