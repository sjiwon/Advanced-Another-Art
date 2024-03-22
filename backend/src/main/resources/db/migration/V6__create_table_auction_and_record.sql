CREATE TABLE IF NOT EXISTS auction
(
    id                BIGINT AUTO_INCREMENT,
    art_id            BIGINT   NOT NULL,
    start_date        DATETIME NOT NULL,
    end_date          DATETIME NOT NULL,
    highest_bidder_id BIGINT   NULL,
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
