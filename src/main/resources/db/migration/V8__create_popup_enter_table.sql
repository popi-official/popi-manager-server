CREATE TABLE popup_enter (
                             popup_enter_id BIGINT NOT NULL,
                             popup_id BIGINT NULL,
                             gender VARCHAR(10) NULL,
                             age_group INT NULL,
                             reservation_date DATE NULL,
                             reservation_time TIME NULL,
                             PRIMARY KEY (popup_enter_id)
);