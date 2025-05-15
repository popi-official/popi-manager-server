-- 인기 상품 8개 (stock < 200, min_stock = 200)
UPDATE item SET stock = 150, min_stock = 200 WHERE item_id = 5;
UPDATE item SET stock = 120, min_stock = 200 WHERE item_id = 6;
UPDATE item SET stock = 100, min_stock = 200 WHERE item_id = 7;
UPDATE item SET stock = 170, min_stock = 200 WHERE item_id = 8;
UPDATE item SET stock = 90,  min_stock = 200 WHERE item_id = 11;
UPDATE item SET stock = 160, min_stock = 200 WHERE item_id = 15;
UPDATE item SET stock = 110, min_stock = 200 WHERE item_id = 17;
UPDATE item SET stock = 130, min_stock = 200 WHERE item_id = 22;

-- 일반 상품들 (stock 평균 약 400, min_stock = 200)
UPDATE item SET stock = 420, min_stock = 200 WHERE item_id = 1;
UPDATE item SET stock = 390, min_stock = 200 WHERE item_id = 2;
UPDATE item SET stock = 410, min_stock = 200 WHERE item_id = 3;
UPDATE item SET stock = 400, min_stock = 200 WHERE item_id = 4;
UPDATE item SET stock = 380, min_stock = 200 WHERE item_id = 9;
UPDATE item SET stock = 410, min_stock = 200 WHERE item_id = 10;
UPDATE item SET stock = 370, min_stock = 200 WHERE item_id = 12;
UPDATE item SET stock = 430, min_stock = 200 WHERE item_id = 13;
UPDATE item SET stock = 390, min_stock = 200 WHERE item_id = 14;
UPDATE item SET stock = 400, min_stock = 200 WHERE item_id = 16;
UPDATE item SET stock = 410, min_stock = 200 WHERE item_id = 18;
UPDATE item SET stock = 395, min_stock = 200 WHERE item_id = 19;
UPDATE item SET stock = 420, min_stock = 200 WHERE item_id = 20;
UPDATE item SET stock = 405, min_stock = 200 WHERE item_id = 21;
UPDATE item SET stock = 400, min_stock = 200 WHERE item_id = 23;
UPDATE item SET stock = 415, min_stock = 200 WHERE item_id = 24;