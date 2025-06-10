ALTER TABLE item add COLUMN popularity_score integer DEFAULT 0;
ALTER TABLE item add COLUMN sales integer DEFAULT 0;
ALTER TABLE item add COLUMN average_sales integer DEFAULT 0;
ALTER TABLE item add COLUMN recommend_count integer;
ALTER TABLE item ADD COLUMN last_restock_date_time DATETIME;
ALTER TABLE item add COLUMN restock_count integer;
ALTER TABLE item add COLUMN is_alarmed boolean DEFAULT false;