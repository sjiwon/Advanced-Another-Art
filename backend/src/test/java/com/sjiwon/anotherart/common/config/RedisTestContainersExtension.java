package com.sjiwon.anotherart.common.config;

import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RedisTestContainersExtension implements Extension {
    private static final String REDIS_IMAGE = "redis:latest";
    private static final int REDIS_PORT = 6379;
    private static final GenericContainer<?> CONTAINER;

    static {
        CONTAINER = new GenericContainer(REDIS_IMAGE)
                .withExposedPorts(REDIS_PORT);
        CONTAINER.start();

        System.setProperty("spring.data.redis.host", CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(CONTAINER.getMappedPort(REDIS_PORT)));
    }
}
