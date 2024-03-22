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

ALTER TABLE member_point_record
    ADD CONSTRAINT fk_member_point_record_member_id_from_member
        FOREIGN KEY (member_id)
            REFERENCES member (id);
