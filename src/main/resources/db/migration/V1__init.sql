CREATE TABLE manager (
                         manager_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         username VARCHAR(255) NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         role VARCHAR(20) NOT NULL,
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME
);