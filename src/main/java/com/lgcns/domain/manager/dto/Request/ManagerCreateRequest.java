package com.lgcns.domain.manager.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "매니저 생성 요청 DTO")
public record ManagerCreateRequest(
        @Schema(description = "사용자 이름", example = "manager123", required = true)
                @NotBlank(message = "username은 필수입니다.")
                @Size(min = 4, max = 20, message = "사용자 이름은 4~20자 사이어야 합니다.")
                String username,
        @Schema(description = "비밀번호", example = "password123", required = true)
                @NotBlank(message = "password는 필수입니다.")
                @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이어야 합니다.")
                String password) {}
