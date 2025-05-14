package com.lgcns.domain.reservationStats.repository;

import com.lgcns.domain.reservationStats.domain.WeekDayReservationCount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeekDayReservationCountRepository
        extends JpaRepository<WeekDayReservationCount, Long> {

    Optional<WeekDayReservationCount> findByPopupId(Long popupId);
}
