
CREATE TABLE item_analysis (
                               item_analysis_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               item_id BIGINT,
                               popularity_score INT,
                               pre_survey_popularity DOUBLE,
                               sales_volume INT,
                               CONSTRAINT fk_item_analysis_item FOREIGN KEY (item_id) REFERENCES item(item_id) ON DELETE CASCADE
);