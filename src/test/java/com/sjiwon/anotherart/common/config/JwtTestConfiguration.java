package com.sjiwon.anotherart.common.config;

import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JwtTestConfiguration {
    @Bean
    JwtTokenProvider jwtTokenProvider(@Value("${jwt.secret-key}") final String secretKey,
                                      @Value("${jwt.access-token-validity}") final long accessTokenValidityInMilliseconds,
                                      @Value("${jwt.refresh-token-validity}") final long refreshTokenValidityInMilliseconds) {
        return new JwtTokenProvider(secretKey, accessTokenValidityInMilliseconds, refreshTokenValidityInMilliseconds);
    }
}
