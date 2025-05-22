package com.lgcns.domain.popup.repository;

import com.lgcns.domain.popup.dto.response.PopupInfoResponse;
import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface PopupRepositoryCustom {

    List<PopupPreviewResponse> findAllPopupsByManagerId(Long managerId);

    Slice<PopupInfoResponse> findAllActivePopups(Long lastPopupId, int size);
}
