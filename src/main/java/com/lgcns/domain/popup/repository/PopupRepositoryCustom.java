package com.lgcns.domain.popup.repository;

import com.lgcns.domain.popup.dto.response.*;
import com.querydsl.core.Tuple;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface PopupRepositoryCustom {

    List<PopupPreviewResponse> findAllPopupsByManagerId(Long managerId);

    List<ChoiceInfoResponse> findAllChoices(Long popupId);

    Slice<PopupInfoResponse> findPopupsByNameWithPagination(
            String keyword, Long lastPopupId, int size);

    List<PopupMapResponse> findPopupsByMapArea(
            Double latMin, Double latMax, Double lngMin, Double lngMax);

    PopupDetailsResponse findPopupDetailsById(Long popupId);

    List<PopupDetailsResponse> findReservedPopupInfo(List<Long> popupIds);

    List<PopupInfoResponse> findPopupsByIds(List<Long> popupIds, int limit);

    List<PopupInfoResponse> findRandomPopups(List<Long> excludeIds, int size);

    List<Long> findAllPopupIdsAfterPopupStartTime(LocalDate nowDate, LocalTime nowTime);

    List<Tuple> findAllPopupIdsAndRemainingDays(LocalDate nowDate, LocalTime nowTime);
}
