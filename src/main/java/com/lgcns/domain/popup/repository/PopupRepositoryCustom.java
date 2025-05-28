package com.lgcns.domain.popup.repository;

import com.lgcns.domain.popup.dto.response.*;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface PopupRepositoryCustom {

    List<PopupPreviewResponse> findAllPopupsByManagerId(Long managerId);

    List<ChoiceInfoResponse> findAllChoices(Long popupId);

    Slice<PopupInfoResponse> findPopupsByNameWithPagination(
            String keyword, Long lastPopupId, int size);

    PopupDetailsResponse findPopupDetailsById(Long popupId);

    List<PopupDetailsResponse> findReservedPopupInfo(List<Long> popupIds);
}
