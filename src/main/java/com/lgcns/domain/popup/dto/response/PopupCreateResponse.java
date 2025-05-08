package com.lgcns.domain.popup.dto.response;

public record PopupCreateResponse(Long popupId) {
    public static PopupCreateResponse of(Long popupId) {
        return new PopupCreateResponse(popupId);
    }
}
