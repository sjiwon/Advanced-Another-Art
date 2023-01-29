package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.token.domain.RedisTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@DataRedisTest
public abstract class RedisTest {
    @Autowired
    protected RedisTokenRepository redisTokenRepository;

    @AfterEach
    void after() {
        redisTokenRepository.deleteAll();
    }
}
