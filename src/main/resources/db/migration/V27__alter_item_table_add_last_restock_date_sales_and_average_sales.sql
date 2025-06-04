ALTER TABLE item add COLUMN sales integer DEFAULT 0 NOT NULL;
ALTER TABLE item add COLUMN average_sales integer DEFAULT 0 NOT NULL;
ALTER TABLE item add COLUMN last_restock_date date DEFAULT NULL;