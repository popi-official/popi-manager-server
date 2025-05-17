CREATE TABLE congestion_stats (
congestion_stats_id BIGINT AUTO_INCREMENT PRIMARY KEY,
popup_id BIGINT NOT NULL,
entrant_count INT NOT NULL,
day_of_week VARCHAR(20) NOT NULL,
analyzed_date DATE NOT NULL,
analyzed_time TIME NOT NULL,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- popup_id에 인덱스 추가
CREATE INDEX idx_congestion_stats_popup_id ON congestion_stats(popup_id);
