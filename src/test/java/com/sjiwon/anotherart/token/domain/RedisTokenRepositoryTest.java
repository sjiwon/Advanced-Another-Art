package com.sjiwon.anotherart.token.domain;

import com.sjiwon.anotherart.common.RedisTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Redis [Repository Layer] -> RedisTokenRepository 테스트")
class RedisTokenRepositoryTest extends RedisTest {
    @Autowired
    RedisTokenRepository redisTokenRepository;

    @Test
    @DisplayName("Redis에 Refresh Token을 저장한 후 memberId릍 통해서 조회한다")
    void test1() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";

        // when
        final RedisRefreshToken redisRefreshToken = new RedisRefreshToken(memberId, refreshToken);
        redisTokenRepository.save(redisRefreshToken);

        // then
        Optional<RedisRefreshToken> findRefreshToken = redisTokenRepository.findById(memberId);
        assertThat(findRefreshToken).isPresent();
        assertThat(findRefreshToken.get().getMemberId()).isEqualTo(redisRefreshToken.getMemberId());
        assertThat(findRefreshToken.get().getRefreshToken()).isEqualTo(redisRefreshToken.getRefreshToken());
    }

    @Test
    @DisplayName("Redis에 Refresh Token을 저장한 후 설정한 TTL이 지나면 Refresh Token은 삭제된다")
    void test2() throws InterruptedException {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";

        // when
        final RedisRefreshToken redisRefreshToken = new RedisRefreshToken(memberId, refreshToken);
        ReflectionTestUtils.setField(redisRefreshToken, "timeToLive", 2L);
        redisTokenRepository.save(redisRefreshToken);

        // then
        Thread.sleep(3000);
        Optional<RedisRefreshToken> findRefreshToken = redisTokenRepository.findById(memberId);
        assertThat(findRefreshToken).isEmpty();
    }
}