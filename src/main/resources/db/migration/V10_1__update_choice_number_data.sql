WITH ranked_choices AS (
    SELECT
        choice_id,
        ROW_NUMBER() OVER (PARTITION BY survey_id ORDER BY choice_id) AS new_number
    FROM choice
)
UPDATE choice c
    JOIN ranked_choices rc ON c.choice_id = rc.choice_id
    SET c.number = rc.new_number;