-- 금일 예약 수 데이터 삽입
INSERT INTO daily_reservation_count (popup_id, reservation_count, created_at, updated_at)
VALUES (1, 152, NOW(), NOW());

-- 요일별 예약 수 데이터 삽입
INSERT INTO day_of_week_reservation_count (
    popup_id,
    monday_count,
    tuesday_count,
    wednesday_count,
    thursday_count,
    friday_count,
    saturday_count,
    sunday_count,
    created_at,
    updated_at
) VALUES (
             1, 155, 138, 130, 174, 285, 364, 309, NOW(), NOW()
         );