package com.lgcns.domain.popup.service;

import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.PopupCreateResponse;
import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import java.util.List;

public interface PopupService {
    PopupCreateResponse createPopup(PopupWithChoicesCreateRequest popupWithChoicesCreateRequest);

    List<PopupPreviewResponse> findAllPopups();

    void deletePopup(Long popupId);
}
