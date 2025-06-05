package com.lgcns.domain.popup.repository;

import com.lgcns.domain.popup.domain.Popup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PopupRepository extends JpaRepository<Popup, Long>, PopupRepositoryCustom {
    @Query(
            "select p.id from Popup p where current_date between p.popupStartDate and p.popupEndDate")
    List<Long> findOperatingPopupIds();
}
