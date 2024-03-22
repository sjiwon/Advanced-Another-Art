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
