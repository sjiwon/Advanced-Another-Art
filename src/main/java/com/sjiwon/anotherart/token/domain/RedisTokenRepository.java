package com.sjiwon.anotherart.token.domain;

import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RedisRefreshToken, Long> {
}
