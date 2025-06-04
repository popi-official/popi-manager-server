ALTER TABLE item add COLUMN sales integer DEFAULT 0;
ALTER TABLE item add COLUMN average_sales integer DEFAULT 0;
ALTER TABLE item add COLUMN last_restock_date date DEFAULT;
ALTER TABLE item add COLUMN restock_count integer DEFAULT 0;
ALTER TABLE item add COLUMN is_alarmed boolean DEFAULT false;