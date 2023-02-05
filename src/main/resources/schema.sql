DROP TABLE IF EXISTS purchase;
DROP TABLE IF EXISTS auction_record;
DROP TABLE IF EXISTS auction;
DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS art_hashtag;
DROP TABLE IF EXISTS art;
DROP TABLE IF EXISTS point_detail;
DROP TABLE IF EXISTS member;

CREATE TABLE member (
    id BIGINT AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL COMMENT '사용자 이름',
    nickname VARCHAR(100) NOT NULL UNIQUE COMMENT '사용자 닉네임',
    login_id VARCHAR(50) NOT NULL UNIQUE COMMENT '사용자 로그인 아이디',
    password VARCHAR(200) NOT NULL COMMENT '사용자 로그인 비밀번호',
    school VARCHAR(50) NOT NULL COMMENT '사용자 학교',
    postcode INT NOT NULL COMMENT '우편번호',
    default_address VARCHAR(200) NOT NULL COMMENT '기본 주소',
    detail_address VARCHAR(200) NOT NULL COMMENT '상세 주소',
    phone VARCHAR(11) NOT NULL UNIQUE COMMENT '사용자 핸드폰 번호',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '사용자 이메일',
    available_point INT NOT NULL DEFAULT 0 COMMENT '사용자의 사용 가능한 포인트',
    role VARCHAR(20) NOT NULL COMMENT '사용자 권한',

    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE point_detail (
    id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL COMMENT '사용자 ID (FK)',
    point_type VARCHAR(10) NOT NULL DEFAULT 'JOIN' COMMENT '포인트 거래 종류',
    amount INT NOT NULL COMMENT '포인트 거래량',
    record_date DATETIME NOT NULL COMMENT '포인트 거래 날짜',

    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE art (
    id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL COMMENT '작품 소유자 ID (FK)',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '작품명',
    description TEXT NOT NULL COMMENT '작품 설명',
    art_type VARCHAR(10) NOT NULL COMMENT '작품 타입 -> 일반/경매',
    price INT NOT NULL COMMENT '작품 가격 -> 일반(고정) / 경매(비드에 따른 변동)',
    art_status VARCHAR(10) NOT NULL COMMENT '작품 상태 -> 판매 중/판매 완료',
    upload_name VARCHAR(200) NOT NULL COMMENT '작품 업로드명',
    storage_name VARCHAR(40) NOT NULL UNIQUE COMMENT '작품 서버 저장명',
    registration_date DATETIME NOT NULL COMMENT '작품 등록 날짜',

    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE art_hashtag (
    id BIGINT AUTO_INCREMENT,
    art_id BIGINT NOT NULL COMMENT '작품 ID (FK)',
    name VARCHAR(100) NOT NULL COMMENT '해시태그명',

    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE favorite (
    id BIGINT AUTO_INCREMENT,
    art_id BIGINT NOT NULL COMMENT '작품 ID (FK)',
    member_id BIGINT NOT NULL COMMENT '사용자 ID (FK)',

    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE auction (
    id BIGINT AUTO_INCREMENT,
    art_id BIGINT NOT NULL COMMENT '경매 작품 ID (FK)',
    start_date DATETIME NOT NULL COMMENT '경매 시작 날짜',
    end_date DATETIME NOT NULL COMMENT '경매 종료 날짜',
    member_id BIGINT NOT NULL COMMENT '최고 입찰자 ID (FK) (초기값 = 작품 소유자)',
    bid_price INT NOT NULL COMMENT '최고 입찰가 (초기값 = 작품 가격)',

    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE auction_record (
    id BIGINT AUTO_INCREMENT,
    auction_id BIGINT NOT NULL COMMENT '경매 ID (FK)',
    member_id BIGINT NOT NULL COMMENT '현재 입찰자 ID (FK)',
    bid_price INT NOT NULL COMMENT '현재 입찰가',
    bid_date DATETIME NOT NULL COMMENT '입찰 날짜',

    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE TABLE purchase (
    id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL COMMENT '구매한 사용자 ID (FK)',
    art_id BIGINT NOT NULL UNIQUE COMMENT '구매한 작품 ID (FK)',
    purchase_price INT NOT NULL COMMENT '구매 가격',
    purchase_date DATETIME NOT NULL COMMENT '구매 날짜',

    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

ALTER TABLE art
ADD CONSTRAINT art_ibfk1_member_id
FOREIGN KEY (member_id)
REFERENCES member(id);

ALTER TABLE art_hashtag
ADD CONSTRAINT art_hashtag_ibfk1_art_id
FOREIGN KEY (art_id)
REFERENCES art(id);

ALTER TABLE art_hashtag
ADD UNIQUE(art_id, name);

ALTER TABLE point_detail
ADD CONSTRAINT point_detail_ibfk1_member_id
FOREIGN KEY (member_id)
REFERENCES member(id);

ALTER TABLE favorite
ADD CONSTRAINT favorite_ibfk1_art_id
FOREIGN KEY (art_id)
REFERENCES art(id);

ALTER TABLE favorite
ADD CONSTRAINT favorite_ibfk2_member_id
FOREIGN KEY (member_id)
REFERENCES member(id);

ALTER TABLE favorite
ADD UNIQUE(art_id, member_id);

ALTER TABLE auction
ADD CONSTRAINT auction_ibfk1_art_id
FOREIGN KEY (art_id)
REFERENCES art(id);

ALTER TABLE auction
ADD CONSTRAINT auction_ibfk2_member_id
FOREIGN KEY (member_id)
REFERENCES member(id);

ALTER TABLE auction_record
ADD CONSTRAINT auction_record_ibfk1_auction_id
FOREIGN KEY (auction_id)
REFERENCES auction(id);

ALTER TABLE auction_record
ADD CONSTRAINT auction_record_ibfk2_member_id
FOREIGN KEY (member_id)
REFERENCES member(id);

ALTER TABLE purchase
ADD CONSTRAINT purchase_ibfk1_member_id
FOREIGN KEY (member_id)
REFERENCES member(id);

ALTER TABLE purchase
ADD CONSTRAINT purchase_ibfk2_art_id
FOREIGN KEY (art_id)
REFERENCES art(id);