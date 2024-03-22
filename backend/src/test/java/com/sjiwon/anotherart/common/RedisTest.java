package com.sjiwon.anotherart.common;

import com.sjiwon.anotherart.common.config.RedisTestContainersExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;

@Tag("Redis")
@ExtendWith(RedisTestContainersExtension.class)
@DataRedisTest
public abstract class RedisTest {
}
