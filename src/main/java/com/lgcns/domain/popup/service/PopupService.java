package com.lgcns.domain.popup.service;

import com.lgcns.domain.popup.dto.request.PopupIdsRequest;
import com.lgcns.domain.popup.dto.request.PopupWithChoicesCreateRequest;
import com.lgcns.domain.popup.dto.response.*;
import com.lgcns.global.common.response.SliceResponse;
import java.util.List;

public interface PopupService {
    PopupCreateResponse createPopup(PopupWithChoicesCreateRequest popupWithChoicesCreateRequest);

    List<PopupPreviewResponse> findAllPopups();

    void deletePopup(Long popupId);

    List<SurveyChoiceResponse> findAllChoicesByPopupId(Long popupId);

    SliceResponse<PopupInfoResponse> findPopupsByNameWithPagination(
            String keyword, Long lastPopupId, int size);

    PopupDetailsResponse findPopupDetailsById(Long popupId);

    List<PopupDetailsResponse> findReservedPopupInfo(PopupIdsRequest request);

    List<PopupInfoResponse> findPopupsByIds(PopupIdsRequest request);
}
