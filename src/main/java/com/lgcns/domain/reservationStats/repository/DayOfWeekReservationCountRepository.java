package com.lgcns.domain.reservationStats.repository;

import com.lgcns.domain.reservationStats.domain.DayOfWeekReservationCount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DayOfWeekReservationCountRepository
        extends JpaRepository<DayOfWeekReservationCount, Long> {

    Optional<DayOfWeekReservationCount> findByPopupId(Long popupId);

    @Modifying
    @Query(
            "UPDATE DayOfWeekReservationCount d SET "
                    + "d.mondayCount = :mondayCount, "
                    + "d.tuesdayCount = :tuesdayCount, "
                    + "d.wednesdayCount = :wednesdayCount, "
                    + "d.thursdayCount = :thursdayCount, "
                    + "d.fridayCount = :fridayCount, "
                    + "d.saturdayCount = :saturdayCount, "
                    + "d.sundayCount = :sundayCount "
                    + "WHERE d.popupId = :popupId")
    void updateDayOfWeekCounts(
            @Param("popupId") Long popupId,
            @Param("mondayCount") int mondayCount,
            @Param("tuesdayCount") int tuesdayCount,
            @Param("wednesdayCount") int wednesdayCount,
            @Param("thursdayCount") int thursdayCount,
            @Param("fridayCount") int fridayCount,
            @Param("saturdayCount") int saturdayCount,
            @Param("sundayCount") int sundayCount);

    void deleteByPopupId(Long popupId);

    boolean existsByPopupId(Long popupId);
}
