CREATE TABLE payment_stats (
    `payment_stats_id`	BIGINT	AUTO_INCREMENT PRIMARY KEY,
    `popup_id`	BIGINT	NOT NULL,
    `analyzed_date`	DATE	NOT NULL,
    `analyzed_time`	TIME	NOT NULL,
    `total_payment`	INT	NOT NULL,
    `user_count`	INT	NOT NULL
);