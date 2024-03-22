CREATE TABLE IF NOT EXISTS art_like
(
    id               BIGINT AUTO_INCREMENT,
    art_id           BIGINT   NOT NULL,
    member_id        BIGINT   NOT NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8mb4;

ALTER TABLE art_like
    ADD CONSTRAINT fk_art_like_art_id_from_art
        FOREIGN KEY (art_id)
            REFERENCES art (id);

ALTER TABLE art_like
    ADD CONSTRAINT fk_art_like_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);

ALTER TABLE art_like
    ADD UNIQUE (art_id, member_id);
