-- 1. 팝업1 생성 (popup)
INSERT INTO popup (
    popup_id, manager_id, name, image_url, popup_start_date, popup_end_date,
    reservation_open_date_time, reservation_close_date_time,
    run_open_time, run_close_time, total_capacity, time_capacity,
    road_address, detail_address, latitude, longitude, created_at, updated_at
) VALUES (
              1,1, 'BLACK PINK 팝업스토어', 'https://bucket/asdf', '2025-06-01', '2025-06-15',
             '2025-05-15T10:00:00', '2025-06-15T19:00:00',
             '10:00:00', '20:00:00', 2000, 200,
             '서울특별시 영등포구 여의대로 108', '5층', 37.527097, 126.927301,
             NOW(), NOW()
         );

-- 2. 설문 생성 (survey) - 4개
INSERT INTO survey (popup_id, number)
VALUES
    ( 1, 0),
    ( 1, 1),
    ( 1, 2),
    ( 1, 3);

-- 3. 보기 생성 (choice) - 각 survey당 4개씩
INSERT INTO choice (survey_id, content)
VALUES
    (1, '보기1'),
    (1, '보기2'),
    (1, '보기3'),
    (1, '보기4'),

    (2, '보기1'),
    (2, '보기2'),
    (2, '보기3'),
    (2, '보기4'),

    (3, '보기1'),
    (3, '보기2'),
    (3, '보기3'),
    (3, '보기4'),

    (4, '보기1'),
    (4, '보기2'),
    (4, '보기3'),
    (4, '보기4');


-- 1. 팝업 생성 (popup)------------------------------------------------------------
INSERT INTO popup (
    popup_id, manager_id, name, image_url, popup_start_date, popup_end_date,
    reservation_open_date_time, reservation_close_date_time,
    run_open_time, run_close_time, total_capacity, time_capacity,
    road_address, detail_address, latitude, longitude, created_at, updated_at
) VALUES (
             2,2, 'IVE 팝업스토어', 'https://bucket/asdf', '2025-07-01', '2025-07-16',
             '2025-05-30T10:00:00', '2025-07-16T10:00:00',
             '10:00:00', '20:00:00', 1000, 100,
             '서울특별시 강남구 압구정로 165', '4층',37.527190, 127.02776,
             NOW(), NOW()
         );

-- 2. 설문 생성 (survey) - 4개
INSERT INTO survey (popup_id, number)
VALUES
    ( 2, 0),
    ( 2, 1),
    ( 2, 2),
    ( 2, 3);

-- 3. 보기 생성 (choice) - 각 survey당 4개씩
INSERT INTO choice (survey_id, content)
VALUES
    (5, '보기1'),
    (5, '보기2'),
    (5, '보기3'),
    (5, '보기4'),

    (6, '보기1'),
    (6, '보기2'),
    (6, '보기3'),
    (6, '보기4'),

    (7, '보기1'),
    (7, '보기2'),
    (7, '보기3'),
    (7, '보기4'),

    (8, '보기1'),
    (8, '보기2'),
    (8, '보기3'),
    (8, '보기4');

-- 1. 팝업 생성 (popup) ------------------------------------------------------------
INSERT INTO popup (
    popup_id, manager_id, name, image_url, popup_start_date, popup_end_date,
    reservation_open_date_time, reservation_close_date_time,
    run_open_time, run_close_time, total_capacity, time_capacity,
    road_address, detail_address, latitude, longitude, created_at, updated_at
) VALUES (
             3,1, 'G-DRAGON 팝업스토어', 'https://bucket/asdf', '2025-08-08', '2025-08-28',
             '2025-07-27T10:00:00', '2025-08-28T20:00:00',
             '10:00:00', '20:00:00', 5000, 800,
             '부산광역시 동구 범일로 125', '7층',35.141182, 129.05897,
             NOW(), NOW()
         );

-- 2. 설문 생성 (survey) - 4개
INSERT INTO survey (popup_id, number)
VALUES
    ( 3, 0),
    ( 3, 1),
    ( 3, 2),
    ( 3, 3);

-- 3. 보기 생성 (choice) - 각 survey당 4개씩
INSERT INTO choice (survey_id, content)
VALUES
    (9, '보기1'),
    (9, '보기2'),
    (9, '보기3'),
    (9, '보기4'),

    (10, '보기1'),
    (10, '보기2'),
    (10, '보기3'),
    (10, '보기4'),

    (11, '보기1'),
    (11, '보기2'),
    (11, '보기3'),
    (11, '보기4'),

    (12, '보기1'),
    (12, '보기2'),
    (12, '보기3'),
    (12, '보기4');