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

ALTER TABLE art
    ADD CONSTRAINT fk_art_owner_id_from_member
        FOREIGN KEY (owner_id)
            REFERENCES member (id);

ALTER TABLE art_hashtag
    ADD CONSTRAINT fk_art_hashtag_art_id_from_art
        FOREIGN KEY (art_id)
            REFERENCES art (id);
