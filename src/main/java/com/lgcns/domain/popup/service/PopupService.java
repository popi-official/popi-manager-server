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
            String searchName, Long lastPopupId, int size);

    List<MemberReservationDetailResponse> findPopupDetails(PopupIdsRequest request);
}
