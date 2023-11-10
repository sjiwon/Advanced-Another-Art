package com.sjiwon.anotherart.auth.infrastructure;

import com.sjiwon.anotherart.auth.domain.AuthKey;
import com.sjiwon.anotherart.common.RedisTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RedisMailAuthenticationProcessorTest extends RedisTest {
    @Autowired
    private RedisMailAuthenticationProcessor sut;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ValueOperations<String, String> operations;

    private final String email = "sjiwon4491@gmail.com";
    private final String value = UUID.randomUUID().toString().substring(0, 8);

    @BeforeEach
    void setUp() {
        operations = redisTemplate.opsForValue();
    }

    @Test
    @DisplayName("Redis에 인증번호를 저장한다")
    void storeAuthCode() {
        // given
        final String key = AuthKey.LOGIN_AUTH_KEY.generateAuthKey(email);

        // when
        sut.storeAuthCode(key, value);

        // then
        assertThat(operations.get(key)).isEqualTo(value);
    }

    @Test
    @DisplayName("Redis에 저장된 인증번호와 요청 인증번호가 일치하는지 확인한다")
    void verifyAuthCode() {
        // given
        final String key = AuthKey.PASSWORD_AUTH_KEY.generateAuthKey(email);
        final String correct = value;
        final String wrong = "fake...";

        sut.storeAuthCode(key, correct);

        // when - then
        assertAll(
                () -> assertDoesNotThrow(() -> sut.verifyAuthCode(key, correct)),
                () -> assertThatThrownBy(() -> sut.verifyAuthCode(key, wrong))
                        .isInstanceOf(AnotherArtException.class)
                        .hasMessage(AuthErrorCode.INVALID_AUTH_CODE.getMessage())
        );
    }
}
