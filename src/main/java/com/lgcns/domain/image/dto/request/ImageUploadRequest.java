package com.lgcns.domain.image.dto.request;

import com.lgcns.domain.image.dto.ImageDirectory;
import com.lgcns.domain.image.dto.ImageFileExtension;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ImageUploadRequest(
        @NotNull(message = "이미지 파일 확장자는 비워둘 수 없습니다.")
                @Schema(description = "이미지 파일 확장자", defaultValue = "JPEG")
                ImageFileExtension imageFileExtension,
        @NotNull(message = "이미지 저장 디렉토리는 비워둘 수 없습니다.")
                @Schema(description = "이미지를 저장할 디렉토리 (예: POPUP, ITEM)", defaultValue = "POPUP")
                ImageDirectory imageDirectory) {}
