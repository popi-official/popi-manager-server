CREATE TABLE entrance (
                             entrance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             popup_id BIGINT NOT NULL,
                             gender VARCHAR(10) NOT NULL,
                             age_group INT NOT NULL,
                             date DATE NOT NULL,
                             time TIME NOT NULL
);