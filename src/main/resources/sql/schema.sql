CREATE TABLE coupon_event
(
    id                  INT AUTO_INCREMENT PRIMARY KEY COMMENT '쿠폰 이벤트 id',
    event_name          VARCHAR(100) NOT NULL COMMENT '쿠폰 이벤트 이름',
    expire_at          TIMESTAMP    NOT NULL COMMENT '쿠폰 만료기한',
    issue_limit_count   BIGINT       NOT NULL COMMENT '쿠폰 발급 제한 개수'
);

INSERT INTO coupon_event (id, event_name, expire_at, issue_limit)
VALUES (1, '[선착순 발급] 50% 할인 쿠폰 (2024년 한정)', '2025-01-01 00:00:00', 10);

CREATE TABLE coupon
(
    id              INT AUTO_INCREMENT PRIMARY KEY COMMENT 'coupon의 id',
    coupon_event_id INT       NOT NULL COMMENT 'coupon event의 id',
    user_id         INT       NOT NULL COMMENT 'coupon을 발급받은 user id',
    issued_at       TIMESTAMP NOT NULL COMMENT 'coupon 발급 일시',
    used_at         TIMESTAMP COMMENT 'coupon 사용 일시',
    UNIQUE KEY unique_user_id_coupon_event_id (user_id, coupon_event_id),
    FOREIGN KEY (coupon_event_id) REFERENCES coupon_event (id)
);
