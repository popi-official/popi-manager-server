CREATE TABLE survey (
    survey_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    popup_id BIGINT,
    number INT NOT NULL,
    CONSTRAINT fk_survey_popup FOREIGN KEY (popup_id) REFERENCES popup(popup_id) ON DELETE CASCADE
);