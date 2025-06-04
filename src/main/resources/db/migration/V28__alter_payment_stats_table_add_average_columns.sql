ALTER TABLE payment_stats DROP COLUMN total_payment;
ALTER TABLE payment_stats DROP COLUMN user_count;

ALTER TABLE payment_stats ADD COLUMN average_amount INT NOT NULL;
ALTER TABLE payment_stats ADD COLUMN period ENUM('TOTAL', 'TODAY') NOT NULL;