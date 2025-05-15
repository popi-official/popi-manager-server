CREATE TABLE visitor_stats (
                               visitor_stats_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
                               popup_id           BIGINT NOT NULL,

                               male_count         INT NOT NULL,
                               female_count       INT NOT NULL,

                               teen_count         INT NOT NULL,
                               twenty_count       INT NOT NULL,
                               thirty_count       INT NOT NULL,
                               forty_count        INT NOT NULL,

                               analyzed_date      DATE NOT NULL,
                               analyzed_time      TIME NOT NULL
);