package com.sjiwon.anotherart.auth.infrastructure;

import com.sjiwon.anotherart.auth.domain.AuthCodeGenerator;
import com.sjiwon.anotherart.auth.domain.AuthKey;
import com.sjiwon.anotherart.common.RedisTest;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.global.security.exception.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Import(RedisMailAuthenticationProcessor.class)
@DisplayName("Auth -> RedisMailAuthenticationProcessor 테스트")
class RedisMailAuthenticationProcessorTest extends RedisTest {
    @Autowired
    private RedisMailAuthenticationProcessor sut;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> operations;

    private static final String EMAIL = "sjiwon4491@gmail.com";
    private static final String VALUE = UUID.randomUUID().toString().substring(0, 8);

    @TestConfiguration
    static class RedisMailAuthenticationProcessorTestConfig {
        @Bean
        public AuthCodeGenerator authCodeGenerator() {
            return () -> VALUE;
        }
    }

    @BeforeEach
    void setUp() {
        operations = redisTemplate.opsForValue();
    }

    @Test
    @DisplayName("Redis에 인증번호를 저장한다")
    void storeAuthCode() {
        // given
        final String key = AuthKey.LOGIN_AUTH_KEY.generateAuthKey(EMAIL);

        // when
        final String result = sut.storeAuthCode(key);

        // then
        assertAll(
                () -> assertThat(result).isEqualTo(VALUE),
                () -> assertThat(operations.get(key)).isEqualTo(VALUE)
        );
    }

    @Test
    @DisplayName("Redis에 저장된 인증번호와 요청 인증번호가 일치하는지 확인한다")
    void verifyAuthCode() {
        // given
        final String key = AuthKey.PASSWORD_AUTH_KEY.generateAuthKey(EMAIL);
        final String correct = VALUE;
        final String wrong = "fake...";

        sut.storeAuthCode(key);

        // when - then
        assertAll(
                () -> assertDoesNotThrow(() -> sut.verifyAuthCode(key, correct)),
                () -> assertThatThrownBy(() -> sut.verifyAuthCode(key, wrong))
                        .isInstanceOf(AuthException.class)
                        .hasMessage(AuthErrorCode.INVALID_AUTH_CODE.getMessage())
        );
    }

    @Test
    @DisplayName("인증 완료 후 Redis에 저장된 인증번호를 제거한다")
    void deleteAuthCode() {
        // given
        final String key = AuthKey.PASSWORD_AUTH_KEY.generateAuthKey(EMAIL);

        sut.storeAuthCode(key);
        assertThat(operations.get(key)).isEqualTo(VALUE);

        // when
        sut.deleteAuthCode(key);

        // then
        assertThat(operations.get(key)).isNull();
    }
}
