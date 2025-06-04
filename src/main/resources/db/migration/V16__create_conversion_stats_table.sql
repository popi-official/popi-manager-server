CREATE TABLE conversion_stats (
                               conversion_stats_id	BIGINT	AUTO_INCREMENT PRIMARY KEY,
                               popup_id	BIGINT	NOT NULL,
                               item_id	BIGINT	NOT NULL,
                               interested_count INT NOT NULL,
                               buyer_count INT NOT NULL,
                               conversion_rate INT NOT NULL,
                               analyzed_date	DATE	NOT NULL,
                               analyzed_time	TIME	NOT NULL,
                               created_at DATETIME NOT NULL,
                               updated_at DATETIME
);

