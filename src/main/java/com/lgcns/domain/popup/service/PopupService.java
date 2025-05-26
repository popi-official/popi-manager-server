package com.lgcns.domain.popup.service;

import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.PopupCreateResponse;
import com.lgcns.domain.popup.dto.response.PopupInfoResponse;
import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import com.lgcns.domain.popup.dto.response.SurveyChoiceResponse;
import com.lgcns.global.common.response.SliceResponse;
import java.util.List;

public interface PopupService {
    PopupCreateResponse createPopup(PopupWithChoicesCreateRequest popupWithChoicesCreateRequest);

    List<PopupPreviewResponse> findAllPopups();

    void deletePopup(Long popupId);

    SliceResponse<PopupInfoResponse> findAllActivePopups(Long lastPopupId, int size);

    List<SurveyChoiceResponse> findAllChoicesByPopupId(Long popupId);
}
