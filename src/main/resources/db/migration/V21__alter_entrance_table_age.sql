ALTER TABLE entrance
DROP COLUMN age_group;

ALTER TABLE entrance
ADD COLUMN age VARCHAR(20) NOT NULL;

UPDATE entrance SET age = '20대' WHERE entrance_id IN (1, 6, 9, 12, 15, 20, 22, 25, 30);
UPDATE entrance SET age = '30대' WHERE entrance_id IN (2, 5, 10, 13, 18, 23, 26, 29);
UPDATE entrance SET age = '10대' WHERE entrance_id IN (3, 8, 14, 17, 21, 28);
UPDATE entrance SET age = '40대 이상' WHERE entrance_id IN (4, 7, 11, 16, 19, 24, 27);