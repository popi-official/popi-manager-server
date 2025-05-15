CREATE TABLE daily_entrant_count (
    daily_entrant_count_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    popup_id BIGINT NOT NULL,
    entrant_count BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- popup_id에 인덱스 추가
CREATE INDEX idx_daily_entrant_count_popup_id ON daily_entrant_count (popup_id);