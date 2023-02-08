package com.sjiwon.anotherart.common;

import org.junit.ClassRule;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class RedisTestContainers {
    private static final String DOCKER_REDIS_IMAGE = "redis:latest";

    @ClassRule
    public static final GenericContainer<?> REDIS_CONTAINER;

    static {

        REDIS_CONTAINER = new GenericContainer<>(DOCKER_REDIS_IMAGE)
                .withExposedPorts(6379)
                .withReuse(true);
        REDIS_CONTAINER.start();
    }
}
