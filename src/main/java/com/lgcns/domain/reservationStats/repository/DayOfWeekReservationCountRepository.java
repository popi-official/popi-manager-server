package com.lgcns.domain.reservationStats.repository;

import com.lgcns.domain.reservationStats.domain.DayOfWeekReservationCount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayOfWeekReservationCountRepository
        extends JpaRepository<DayOfWeekReservationCount, Long> {

    Optional<DayOfWeekReservationCount> findByPopupId(Long popupId);
}
