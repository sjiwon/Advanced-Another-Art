package com.sjiwon.anotherart.common.config;

import com.sjiwon.anotherart.global.security.validator.TokenPayloadChecker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SecurityValiatorConfiguration {
    @Bean
    TokenPayloadChecker tokenPayloadChecker() {
        return new TokenPayloadChecker();
    }
}
