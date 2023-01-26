package com.sjiwon.anotherart.global.redis;

import org.springframework.data.repository.CrudRepository;

public interface RedisTokenRepository extends CrudRepository<RedisRefreshToken, Long> {
}
