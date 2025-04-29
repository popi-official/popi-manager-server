CREATE TABLE survey (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    popup_id BIGINT,
    number INT NOT NULL,
    CONSTRAINT fk_survey_popup FOREIGN KEY (popup_id) REFERENCES popup(id)
);