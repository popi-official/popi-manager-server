package com.lgcns.domain.reservation.repository;

import com.lgcns.domain.reservation.domain.Reservation;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final EntityManager em;

    @Override
    @Transactional
    public void bulkInsertReservations(List<Reservation> reservations) {
        if (reservations.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append(
                "INSERT INTO reservation (popup_id, reservation_date, reservation_time, possible_count, reservation_open_date_time, reservation_close_date_time) VALUES ");

        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            sb.append("(")
                    .append(r.getPopup().getId())
                    .append(", ")
                    .append("'")
                    .append(r.getReservationDate())
                    .append("', ")
                    .append("'")
                    .append(r.getReservationTime())
                    .append("', ")
                    .append(r.getPossibleCount())
                    .append(", ")
                    .append("'")
                    .append(r.getReservationOpenDateTime())
                    .append("', ")
                    .append("'")
                    .append(r.getReservationCloseDateTime())
                    .append("')");

            if (i < reservations.size() - 1) sb.append(", ");
        }

        em.createNativeQuery(sb.toString()).executeUpdate();
    }
}
