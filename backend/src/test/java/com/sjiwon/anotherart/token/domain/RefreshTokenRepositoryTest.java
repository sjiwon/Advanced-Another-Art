package com.sjiwon.anotherart.token.domain;

import com.sjiwon.anotherart.common.RepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Token [Repository Layer] -> RefreshTokenRepository 테스트")
class RefreshTokenRepositoryTest extends RepositoryTest {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("RTR정책에 의해서 RefreshToken을 재발급한다")
    void test1() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        refreshTokenRepository.save(RefreshToken.issueRefreshToken(memberId, refreshToken));

        // when
        final String newRefreshToken = refreshToken + "_update";
        refreshTokenRepository.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);

        // then
        assertAll(
                () -> assertThat(refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken)).isFalse(),
                () -> assertThat(refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, newRefreshToken)).isTrue()
        );
    }
    
    @Test
    @DisplayName("memberId, refreshToken에 대해서 DB에 존재하는지 확인한다")
    void test2() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        refreshTokenRepository.save(RefreshToken.issueRefreshToken(memberId, refreshToken));

        // when
        boolean actual1 = refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
        boolean actual2 = refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken + "diff");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 Refresh Token을 삭제한다")
    void test3() {
        // given
        final Long memberId = 1L;
        final String refreshToken = "refresh_token_hello_world";
        refreshTokenRepository.save(RefreshToken.issueRefreshToken(memberId, refreshToken));

        // when
        refreshTokenRepository.deleteByMemberId(memberId);

        // then
        assertThat(refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken)).isFalse();
    }
}