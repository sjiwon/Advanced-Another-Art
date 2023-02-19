package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Token [Service Layer] -> TokenPersistenceService 테스트")
@RequiredArgsConstructor
class TokenPersistenceServiceTest extends ServiceIntegrateTest {
    private final TokenPersistenceService tokenPersistenceService;

    @Test
    @DisplayName("RefreshToken을 DB에 저장한다")
    void test1() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";

        // when
        tokenPersistenceService.saveOrUpdateRefreshToken(memberId, refreshToken);

        // then
        assertThat(refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken)).isTrue();
    }

    @Test
    @DisplayName("RTR(Refresh Token Rotation) 정책에 의해 사용자 소유의 Refesh Token을 새롭게 교체한다")
    void test2() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        tokenPersistenceService.saveOrUpdateRefreshToken(memberId, refreshToken);

        // when
        final String newRefreshToken = refreshToken + "_update";
        tokenPersistenceService.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);

        // then
        assertAll(
                () -> assertThat(refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken)).isFalse(),
                () -> assertThat(refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, newRefreshToken)).isTrue()
        );
    }

    @Test
    @DisplayName("사용자 소유의 Refresh Token을 DB에서 삭제한다")
    void test3() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        tokenPersistenceService.saveOrUpdateRefreshToken(memberId, refreshToken);

        // when
        tokenPersistenceService.deleteRefreshTokenViaMemberId(memberId);

        // then
        assertThat(refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken)).isFalse();
    }

    @Test
    @DisplayName("사용자 ID(PK)에 대한 Refresh Token이 DB에 유효하게 존재하는지 확인한다")
    void test4() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        tokenPersistenceService.saveOrUpdateRefreshToken(memberId, refreshToken);

        // when
        final String fakeRefreshToken = "fake_refresh_token_hello_world";
        boolean actual1 = tokenPersistenceService.isRefreshTokenExists(memberId, refreshToken);
        boolean actual2 = tokenPersistenceService.isRefreshTokenExists(memberId, fakeRefreshToken);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}