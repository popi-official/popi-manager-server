CREATE TABLE choice (
    choice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    survey_id BIGINT,
    content VARCHAR(255) NOT NULL,
    CONSTRAINT fk_choice_survey FOREIGN KEY (survey_id) REFERENCES survey(survey_id) ON DELETE CASCADE
);