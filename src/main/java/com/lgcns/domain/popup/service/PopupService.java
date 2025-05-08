package com.lgcns.domain.popup.service;

import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.PopupCreateResponse;

public interface PopupService {

    PopupCreateResponse createPopup(PopupWithChoicesCreateRequest popupWithChoicesCreateRequest);
}
