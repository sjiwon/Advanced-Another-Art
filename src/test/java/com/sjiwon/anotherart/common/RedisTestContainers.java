package com.sjiwon.anotherart.common;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class RedisTestContainers {
    private static final String DOCKER_REDIS_IMAGE = "redis:latest";

    @Container
    static GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse(DOCKER_REDIS_IMAGE))
            .withExposedPorts(6379);
}
