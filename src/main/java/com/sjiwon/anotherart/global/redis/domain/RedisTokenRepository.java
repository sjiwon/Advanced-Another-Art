package com.sjiwon.anotherart.global.redis.domain;

import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RedisRefreshToken, Long> {
}
