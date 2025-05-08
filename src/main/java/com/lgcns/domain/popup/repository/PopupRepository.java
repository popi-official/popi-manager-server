package com.lgcns.domain.popup.repository;

import com.lgcns.domain.popup.domain.Popup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopupRepository extends JpaRepository<Popup, Long>, PopupRepositoryCustom {
    List<Popup> findAllByManagerIdOrderByIdDesc(Long managerId);
}
