package com.sjiwon.anotherart.common;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class RedisTestContainers {
    private static final String DOCKER_REDIS_IMAGE = "redis:latest";

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DOCKER_REDIS_IMAGE)
            .withReuse(true);
}
