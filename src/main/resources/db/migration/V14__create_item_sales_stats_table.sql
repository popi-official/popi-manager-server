CREATE TABLE item_sales_stats (
    item_sales_stats_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    popup_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL ,
    sales_volume INT NOT NULL DEFAULT 0
);