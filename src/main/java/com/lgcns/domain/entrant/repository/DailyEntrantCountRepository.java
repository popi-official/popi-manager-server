package com.lgcns.domain.entrant.repository;

import com.lgcns.domain.entrant.domain.DailyEntrantCount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyEntrantCountRepository extends JpaRepository<DailyEntrantCount, Long> {

    Optional<DailyEntrantCount> findByPopupId(Long popupId);
}
