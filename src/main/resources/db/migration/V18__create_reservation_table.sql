CREATE TABLE reservation (
    reservation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    popup_id BIGINT NOT NULL,
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    possible_count INT NOT NULL,
    reservation_open_date_time DATETIME NOT NULL,
    reservation_close_date_time DATETIME NOT NULL,

    CONSTRAINT fk_reservation_popup FOREIGN KEY (popup_id) REFERENCES popup(popup_id) ON DELETE CASCADE
);