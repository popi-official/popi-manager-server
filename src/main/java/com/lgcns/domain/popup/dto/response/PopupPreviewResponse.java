package com.lgcns.domain.popup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PopupPreviewResponse(
        @Schema(description = "팝업스토어 ID", example = "1") Long popupId,
        @Schema(description = "팝업스토어 이름", example = "BLACKPINK 팝업스토어") String name,
        @Schema(description = "팝업스토어 이미지 URL", example = "https://bucket/asdf") String imageUrl) {
    public static PopupPreviewResponse of(Long popupId, String name, String imageUrl) {
        return new PopupPreviewResponse(popupId, name, imageUrl);
    }
}
