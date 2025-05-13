package com.lgcns.domain.popupAccess.service;

import com.lgcns.domain.popupAccess.dto.request.PopupEnterCreateRequest;

public interface PopupAccessService {
    void createPopupEnter(Long popupId, PopupEnterCreateRequest request);
}
