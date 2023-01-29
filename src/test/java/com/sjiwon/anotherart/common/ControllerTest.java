package com.sjiwon.anotherart.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.token.domain.RedisTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public abstract class ControllerTest extends RedisTestContainers {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RedisTokenRepository redisTokenRepository;

    @AfterEach
    void after() {
        redisTokenRepository.deleteAll();
    }
}
