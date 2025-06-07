ALTER  TABLE item_analysis
ADD COLUMN created_at DATETIME(6),
ADD COLUMN updated_at DATETIME(6);

UPDATE item_analysis
SET created_at = NOW(),
    updated_at = NOW();

ALTER TABLE item_analysis
    MODIFY COLUMN created_at DATETIME(6) NOT NULL,
    MODIFY COLUMN updated_at DATETIME(6) NOT NULL;