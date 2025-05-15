package com.lgcns.domain.reservationStats.repository;

import com.lgcns.domain.reservationStats.domain.DailyReservationCount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyReservationCountRepository
        extends JpaRepository<DailyReservationCount, Long> {

    Optional<DailyReservationCount> findByPopupId(Long popupId);
}
