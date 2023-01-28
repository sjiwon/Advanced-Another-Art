package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.token.domain.RedisRefreshToken;
import com.sjiwon.anotherart.token.domain.RedisTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Token [Redis] -> RedisTokenService 테스트")
class RedisTokenServiceTest {
    @Autowired
    RedisTokenRepository redisTokenRepository;

    @Autowired
    RedisTokenService redisTokenService;

    @Test
    @DisplayName("memberId, RefreshToken을 통해서 Redis에 RedisRefreshToken을 저장한다")
    void test1() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";

        // when
        redisTokenService.saveRefreshToken(refreshToken, memberId);

        // then
        Optional<RedisRefreshToken> findRefreshToken = redisTokenRepository.findById(refreshToken);
        assertThat(findRefreshToken).isPresent();
        assertThat(findRefreshToken.get().getMemberId()).isEqualTo(memberId);
        assertThat(findRefreshToken.get().getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("Refresh Token을 Redis에서 삭제한다")
    void test2() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        redisTokenService.saveRefreshToken(refreshToken, memberId);

        // when
        redisTokenService.deleteRefreshToken(refreshToken);

        // then
        Optional<RedisRefreshToken> findRefreshToken = redisTokenRepository.findById(refreshToken);
        assertThat(findRefreshToken).isEmpty();
    }

    @Test
    @DisplayName("Refresh Token이 Redis에 존재하는지 여부를 확인한다")
    void test3() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        redisTokenService.saveRefreshToken(refreshToken, memberId);

        // when
        final String fakeRefreshToken = "fake_refresh_token_hello_world";
        boolean actual1 = redisTokenService.isRefreshTokenExists(refreshToken);
        boolean actual2 = redisTokenService.isRefreshTokenExists(fakeRefreshToken);

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }
}