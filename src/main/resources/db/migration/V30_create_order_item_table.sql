CREATE TABLE order_item (
                            order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            item_id BIGINT NOT NULL,
                            real_count INT NOT NULL,
                            status VARCHAR(20) NOT NULL,
                            created_at DATETIME NOT NULL,
                            updated_at DATETIME NOT NULL,

                            CONSTRAINT fk_order_item_item
                                FOREIGN KEY (item_id) REFERENCES item (item_id)
                                    ON DELETE CASCADE
);