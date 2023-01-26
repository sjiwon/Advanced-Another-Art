package com.sjiwon.anotherart.global.redis.service;

import com.sjiwon.anotherart.global.redis.domain.RedisRefreshToken;
import com.sjiwon.anotherart.global.redis.domain.RedisTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Redis [Service Layer] -> RedisTokenService 테스트")
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
        redisTokenService.saveRefreshToken(memberId, refreshToken);

        // then
        Optional<RedisRefreshToken> findRefreshToken = redisTokenRepository.findById(memberId);
        assertThat(findRefreshToken).isPresent();
        assertThat(findRefreshToken.get().getMemberId()).isEqualTo(memberId);
        assertThat(findRefreshToken.get().getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("memberId에 해당하는 Refresh Token을 Redis에서 삭제한다")
    void test2() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        redisTokenService.saveRefreshToken(memberId, refreshToken);

        // when
        redisTokenService.deleteRefreshTokenViaMemberId(memberId);

        // then
        Optional<RedisRefreshToken> findRefreshToken = redisTokenRepository.findById(memberId);
        assertThat(findRefreshToken).isEmpty();
    }

    @Test
    @DisplayName("memberId에 해당하는 Refresh Token이 Redis에 존재하는지 여부를 확인한다")
    void test3() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        redisTokenService.saveRefreshToken(memberId, refreshToken);

        // when
        final Long fakeMemberId = 100L;
        boolean actual1 = redisTokenService.isRefreshTokenExists(memberId);
        boolean actual2 = redisTokenService.isRefreshTokenExists(100L);

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }
}