CREATE TABLE notification (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    manager_id BIGINT NOT NULL,
    popup_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    popularity VARCHAR(50) NOT NULL,
    min_stock INT,

    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    INDEX idx_notification_user_id_popup_id (manager_id, popup_id),
    INDEX idx_notification_item_id (item_id)
);