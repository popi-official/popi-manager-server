package com.lgcns.domain.popup.repository;

import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import java.util.List;

public interface PopupRepositoryCustom {

    List<PopupPreviewResponse> findAllPopupsByManagerId(Long managerId);
}
