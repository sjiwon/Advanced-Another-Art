CREATE TABLE IF NOT EXISTS member
(
    id               BIGINT AUTO_INCREMENT,
    name             VARCHAR(30)  NOT NULL,
    nickname         VARCHAR(100) NOT NULL UNIQUE,
    login_id         VARCHAR(50)  NOT NULL UNIQUE,
    password         VARCHAR(200) NOT NULL,
    school           VARCHAR(50)  NOT NULL,
    postcode         INT          NOT NULL,
    default_address  VARCHAR(200) NOT NULL,
    detail_address   VARCHAR(200) NOT NULL,
    phone            VARCHAR(13)  NOT NULL UNIQUE,
    email            VARCHAR(100) NOT NULL UNIQUE,
    total_point      INT          NOT NULL DEFAULT 0,
    available_point  INT          NOT NULL DEFAULT 0,
    role             VARCHAR(20)  NOT NULL,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_token
(
    id               BIGINT AUTO_INCREMENT,
    member_id        BIGINT       NOT NULL UNIQUE,
    refresh_token    VARCHAR(255) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS member_point_record
(
    id               BIGINT AUTO_INCREMENT,
    member_id        BIGINT      NOT NULL,
    point_type       VARCHAR(30) NOT NULL,
    amount           INT         NOT NULL,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS art
(
    id               BIGINT AUTO_INCREMENT,
    owner_id         BIGINT       NOT NULL,
    name             VARCHAR(100) NOT NULL UNIQUE,
    description      TEXT         NOT NULL,
    art_type         VARCHAR(30)  NOT NULL,
    price            INT          NOT NULL,
    art_status       VARCHAR(30)  NOT NULL,
    upload_file_name VARCHAR(200) NOT NULL UNIQUE,
    link             VARCHAR(200) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS art_hashtag
(
    id     BIGINT AUTO_INCREMENT,
    art_id BIGINT       NOT NULL,
    name   VARCHAR(100) NOT NULL,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS art_favorite
(
    id               BIGINT AUTO_INCREMENT,
    art_id           BIGINT   NOT NULL,
    member_id        BIGINT   NOT NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS auction
(
    id                BIGINT AUTO_INCREMENT,
    art_id            BIGINT   NOT NULL,
    start_date        DATETIME NOT NULL,
    end_date          DATETIME NOT NULL,
    highest_bidder_id BIGINT,
    highest_bid_price INT      NOT NULL,
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS auction_record
(
    id               BIGINT AUTO_INCREMENT,
    auction_id       BIGINT   NOT NULL,
    bidder_id        BIGINT   NOT NULL,
    bid_price        INT      NOT NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS art_purchase
(
    id               BIGINT AUTO_INCREMENT,
    buyer_id         BIGINT   NOT NULL,
    art_id           BIGINT   NOT NULL UNIQUE,
    purchase_price   INT      NOT NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE member_token
    ADD CONSTRAINT fk_member_token_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE member_point_record
    ADD CONSTRAINT fk_member_point_record_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE art
    ADD CONSTRAINT fk_art_owner_id_from_member
        FOREIGN KEY (owner_id)
            REFERENCES member (id);

ALTER TABLE art_hashtag
    ADD CONSTRAINT fk_art_hashtag_art_id_from_art
        FOREIGN KEY (art_id)
            REFERENCES art (id);

ALTER TABLE art_hashtag
    ADD UNIQUE (art_id, name);

ALTER TABLE art_favorite
    ADD CONSTRAINT fk_art_favorite_art_id_from_art
        FOREIGN KEY (art_id)
            REFERENCES art (id);

ALTER TABLE art_favorite
    ADD CONSTRAINT fk_art_favorite_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE art_favorite
    ADD UNIQUE (art_id, member_id);

ALTER TABLE auction
    ADD CONSTRAINT fk_auction_art_id_from_art
        FOREIGN KEY (art_id)
            REFERENCES art (id);

ALTER TABLE auction
    ADD CONSTRAINT fk_auction_highest_bidder_id_from_member
        FOREIGN KEY (highest_bidder_id)
            REFERENCES member (id);

ALTER TABLE auction_record
    ADD CONSTRAINT fk_auction_record_auction_id_from_auction
        FOREIGN KEY (auction_id)
            REFERENCES auction (id);

ALTER TABLE auction_record
    ADD CONSTRAINT fk_auction_record_bidder_id_from_member
        FOREIGN KEY (bidder_id)
            REFERENCES member (id);

ALTER TABLE art_purchase
    ADD CONSTRAINT fk_art_purchase_buyer_id_from_member
        FOREIGN KEY (buyer_id)
            REFERENCES member (id);

ALTER TABLE art_purchase
    ADD CONSTRAINT fk_art_purchase_art_id_from_art
        FOREIGN KEY (art_id)
            REFERENCES art (id);
