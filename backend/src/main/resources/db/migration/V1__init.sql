CREATE TABLE IF NOT EXISTS member
(
    id              BIGINT AUTO_INCREMENT,
    name            VARCHAR(30)  NOT NULL COMMENT '사용자 이름',
    nickname        VARCHAR(100) NOT NULL UNIQUE COMMENT '사용자 닉네임',
    login_id        VARCHAR(50)  NOT NULL UNIQUE COMMENT '사용자 로그인 아이디',
    password        VARCHAR(200) NOT NULL COMMENT '사용자 로그인 비밀번호',
    school          VARCHAR(50)  NOT NULL COMMENT '사용자 학교',
    postcode        INT          NOT NULL COMMENT '우편번호',
    default_address VARCHAR(200) NOT NULL COMMENT '기본 주소',
    detail_address  VARCHAR(200) NOT NULL COMMENT '상세 주소',
    phone           VARCHAR(11)  NOT NULL UNIQUE COMMENT '사용자 핸드폰 번호',
    email           VARCHAR(100) NOT NULL UNIQUE COMMENT '사용자 이메일',
    total_point     INT          NOT NULL DEFAULT 0 COMMENT '전체 보유 포인트',
    available_point INT          NOT NULL DEFAULT 0 COMMENT '사용 가능한 포인트',
    role            VARCHAR(20)  NOT NULL COMMENT '사용자 권한',
    created_at      DATETIME     NOT NULL COMMENT '생성 날짜',
    modified_at     DATETIME     NOT NULL COMMENT '수정 날짜',

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_token
(
    id            BIGINT AUTO_INCREMENT,
    member_id     BIGINT       NOT NULL UNIQUE COMMENT '토큰 보유 사용자 ID (FK)',
    refresh_token VARCHAR(255) NOT NULL UNIQUE COMMENT 'Refresh Token (RTR)',

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_point_record
(
    id          BIGINT AUTO_INCREMENT,
    member_id   BIGINT      NOT NULL COMMENT '사용자 ID (FK)',
    point_type  VARCHAR(10) NOT NULL COMMENT '포인트 활용 종류',
    amount      INT         NOT NULL COMMENT '포인트 거래량',
    created_at  DATETIME    NOT NULL COMMENT '생성 날짜',
    modified_at DATETIME    NOT NULL COMMENT '수정 날짜 [not used]',

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS art
(
    id           BIGINT AUTO_INCREMENT,
    owner_id     BIGINT       NOT NULL COMMENT '작품 소유자 ID (FK)',
    name         VARCHAR(100) NOT NULL UNIQUE COMMENT '작품명',
    description  TEXT         NOT NULL COMMENT '작품 설명',
    art_type     VARCHAR(10)  NOT NULL COMMENT '작품 타입 -> 일반/경매',
    price        INT          NOT NULL COMMENT '작품 가격',
    art_status   VARCHAR(10)  NOT NULL COMMENT '작품 상태 -> 판매중/판매 완료',
    storage_name VARCHAR(255) NOT NULL UNIQUE COMMENT '작품 서버 저장명',
    created_at   DATETIME     NOT NULL COMMENT '생성 날짜',
    modified_at  DATETIME     NOT NULL COMMENT '수정 날짜',

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS art_hashtag
(
    id     BIGINT AUTO_INCREMENT,
    art_id BIGINT       NOT NULL COMMENT '작품 ID (FK)',
    name   VARCHAR(100) NOT NULL COMMENT '해시태그명',

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS art_favorite
(
    id        BIGINT AUTO_INCREMENT,
    art_id    BIGINT NOT NULL COMMENT '작품 ID (FK)',
    member_id BIGINT NOT NULL COMMENT '사용자 ID (FK)',

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS auction
(
    id                BIGINT AUTO_INCREMENT,
    art_id            BIGINT   NOT NULL COMMENT '경매 작품 ID (FK)',
    start_date        DATETIME NOT NULL COMMENT '경매 시작 날짜',
    end_date          DATETIME NOT NULL COMMENT '경매 종료 날짜',
    highest_bidder_id BIGINT COMMENT '최고 입찰자 ID (FK)',
    highest_bid_price INT      NOT NULL COMMENT '최고 입찰가 (초기값 = 작품 가격)',
    created_at        DATETIME NOT NULL COMMENT '생성 날짜',
    modified_at       DATETIME NOT NULL COMMENT '수정 날짜',

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS auction_record
(
    id          BIGINT AUTO_INCREMENT,
    auction_id  BIGINT   NOT NULL COMMENT '경매 ID (FK)',
    bidder_id   BIGINT   NOT NULL COMMENT '현재 입찰자 ID (FK)',
    bid_price   INT      NOT NULL COMMENT '현재 입찰가',
    created_at  DATETIME NOT NULL COMMENT '생성 날짜',
    modified_at DATETIME NOT NULL COMMENT '수정 날짜',

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS art_purchase
(
    id             BIGINT AUTO_INCREMENT,
    buyer_id       BIGINT   NOT NULL COMMENT '구매한 사용자 ID (FK)',
    art_id         BIGINT   NOT NULL UNIQUE COMMENT '구매한 작품 ID (FK)',
    purchase_price INT      NOT NULL COMMENT '구매 가격',
    created_at     DATETIME NOT NULL COMMENT '생성 날짜',
    modified_at    DATETIME NOT NULL COMMENT '날짜 날짜',

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE member_token
    ADD CONSTRAINT member_token_fk1_member_id
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE member_point_record
    ADD CONSTRAINT member_point_record_fk1_member_id
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE art
    ADD CONSTRAINT art_fk1_member_id
        FOREIGN KEY (owner_id)
            REFERENCES member (id);

ALTER TABLE art_hashtag
    ADD CONSTRAINT art_hashtag_fk1_art_id
        FOREIGN KEY (art_id)
            REFERENCES art (id);

ALTER TABLE art_hashtag
    ADD UNIQUE (art_id, name);

ALTER TABLE art_favorite
    ADD CONSTRAINT art_favorite_fk1_art_id
        FOREIGN KEY (art_id)
            REFERENCES art (id);

ALTER TABLE art_favorite
    ADD CONSTRAINT art_favorite_fk2_member_id
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE art_favorite
    ADD UNIQUE (art_id, member_id);

ALTER TABLE auction
    ADD CONSTRAINT auction_fk1_art_id
        FOREIGN KEY (art_id)
            REFERENCES art (id);

ALTER TABLE auction
    ADD CONSTRAINT auction_fk2_highest_bidder_id
        FOREIGN KEY (highest_bidder_id)
            REFERENCES member (id);

ALTER TABLE auction_record
    ADD CONSTRAINT auction_record_fk1_auction_id
        FOREIGN KEY (auction_id)
            REFERENCES auction (id);

ALTER TABLE auction_record
    ADD CONSTRAINT auction_record_fk2_bidder_id
        FOREIGN KEY (bidder_id)
            REFERENCES member (id);

ALTER TABLE art_purchase
    ADD CONSTRAINT purchase_fk1_buyer_id
        FOREIGN KEY (buyer_id)
            REFERENCES member (id);

ALTER TABLE art_purchase
    ADD CONSTRAINT purchase_fk2_art_id
        FOREIGN KEY (art_id)
            REFERENCES art (id);
