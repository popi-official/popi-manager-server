CREATE TABLE member_answer (
    member_answer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    survey_id BIGINT,
    answer_number INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    member_gender VARCHAR(255),
    member_age INT,
    CONSTRAINT fk_member_answer_survey FOREIGN KEY (survey_id) REFERENCES survey(survey_id) ON DELETE CASCADE
);