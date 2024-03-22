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

ALTER TABLE art_purchase
    ADD CONSTRAINT fk_art_purchase_buyer_id_from_member
        FOREIGN KEY (buyer_id)
            REFERENCES member (id);

ALTER TABLE art_purchase
    ADD CONSTRAINT fk_art_purchase_art_id_from_art
        FOREIGN KEY (art_id)
            REFERENCES art (id);
