package com.lgcns.domain.reservationStats.repository;

import com.lgcns.domain.reservationStats.domain.DayOfWeekReservationCount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DayOfWeekReservationCountRepository
        extends JpaRepository<DayOfWeekReservationCount, Long> {

    Optional<DayOfWeekReservationCount> findByPopupId(Long popupId);

    @Query("SELECT popupId FROM DayOfWeekReservationCount WHERE popupId IN :popupIds")
    List<Long> findExistingPopupIds(@Param("popupIds") List<Long> popupIds);
}
