CREATE TABLE item (
    item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    popup_id BIGINT,
    name VARCHAR(255),
    image_url VARCHAR(255),
    price INT,
    stock INT,
    min_stock INT,
    location VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    CONSTRAINT fk_item_popup Foreign Key (popup_id) REFERENCES popup(popup_id) ON DELETE CASCADE
    );