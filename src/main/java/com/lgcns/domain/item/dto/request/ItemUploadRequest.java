package com.lgcns.domain.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ItemUploadRequest(
        @NotNull(message = "팝업 아이디는 필수입니다.") @Schema(description = "팝업 아이디", example = "1")
        Long popupId
) {
    public static ItemUploadRequest of(Long popupId){
        return new ItemUploadRequest(popupId);
    }
}