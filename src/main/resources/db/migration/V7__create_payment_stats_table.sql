CREATE TABLE payment_stats (
                                `payment_stats_id`	BIGINT	AUTO_INCREMENT PRIMARY KEY,
                                `popup_id`	BIGINT	NOT NULL,
                                `date`	DATE	NOT NULL,
                                `time`	TIME	NOT NULL,
                                `total_payment`	INT	NOT NULL,
                                `user_count`	INT	NOT NULL,
                                CONSTRAINT fk_payment_stats_popup Foreign Key (popup_id) REFERENCES popup(popup_id) ON DELETE CASCADE
);