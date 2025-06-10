package com.lgcns.domain.reservation.repository;

import static com.lgcns.domain.reservation.domain.QReservation.reservation;

import com.lgcns.domain.reservation.domain.Reservation;
import com.lgcns.domain.reservation.dto.response.DailyReservation;
import com.lgcns.domain.reservation.dto.response.TimeSlot;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

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

    @Override
    public List<DailyReservation> findReservationByIdAndDate(Long popupId, String date) {
        YearMonth yearMonth = YearMonth.parse(date);

        List<Tuple> results = fetchReservations(popupId, yearMonth);

        return mapToDailyReservations(results);
    }

    private List<Tuple> fetchReservations(Long popupId, YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        return queryFactory
                .select(reservation.reservationDate, reservation.id, reservation.reservationTime)
                .from(reservation)
                .where(
                        reservation.popup.id.eq(popupId),
                        reservation.reservationDate.between(start, end),
                        reservation.reservationDate.after(LocalDate.now().minusDays(1)))
                .fetch();
    }

    private List<DailyReservation> mapToDailyReservations(List<Tuple> tuples) {
        Map<LocalDate, List<TimeSlot>> grouped =
                tuples.stream()
                        .collect(
                                Collectors.groupingBy(
                                        tuple -> tuple.get(reservation.reservationDate),
                                        Collectors.mapping(
                                                this::mapToTimeSlot, Collectors.toList())));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> DailyReservation.of(entry.getKey(), entry.getValue()))
                .toList();
    }

    private TimeSlot mapToTimeSlot(Tuple tuple) {
        return TimeSlot.of(tuple.get(reservation.id), tuple.get(reservation.reservationTime));
    }
}
