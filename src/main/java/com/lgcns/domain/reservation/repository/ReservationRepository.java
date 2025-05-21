package com.lgcns.domain.reservation.repository;

import com.lgcns.domain.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {}
