package com.lgcns.domain.popup.repository;

import com.lgcns.domain.popup.dto.response.*;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface PopupRepositoryCustom {

    List<PopupPreviewResponse> findAllPopupsByManagerId(Long managerId);

    Slice<PopupInfoResponse> findAllActivePopups(Long lastPopupId, int size);

    List<ChoiceInfoResponse> findAllChoices(Long popupId);
}
