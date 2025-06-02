ALTER TABLE member_answer
    MODIFY COLUMN choice_id BIGINT NOT NULL;

ALTER TABLE member_answer
    MODIFY COLUMN member_id BIGINT NOT NULL;