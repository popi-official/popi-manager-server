-- 금일 예약 수 테이블
CREATE TABLE daily_reservation_count (
    daily_reservation_count_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    popup_id BIGINT NOT NULL,
    reservation_count INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 요일별 예약 수 테이블
CREATE TABLE week_day_reservation_count (
    week_day_reservation_count_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    popup_id BIGINT NOT NULL,
    monday_count INT NOT NULL,
    tuesday_count INT NOT NULL,
    wednesday_count INT NOT NULL,
    thursday_count INT NOT NULL,
    friday_count INT NOT NULL,
    saturday_count INT NOT NULL,
    sunday_count INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 인덱스 추가 (조회 성능 개선)
CREATE INDEX idx_daily_reservation_popup_id ON daily_reservation_count(popup_id);
CREATE INDEX idx_weekday_reservation_popup_id ON week_day_reservation_count(popup_id);