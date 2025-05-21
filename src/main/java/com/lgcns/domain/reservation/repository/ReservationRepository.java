package com.lgcns.domain.reservation.repository;

import com.lgcns.domain.reservation.domain.Reservation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

    List<Reservation> findAllByPopupId(Long popupId);
}
