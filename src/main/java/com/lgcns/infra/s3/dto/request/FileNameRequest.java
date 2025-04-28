package com.lgcns.infra.s3.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "S3 업로드 파일 이름 DTO")
public record FileNameRequest(
        @NotBlank(message = "파일 이름은 필수입니다.") @Schema(description = "파일 이름", example = "popi.jpg")
                String fileName) {}
