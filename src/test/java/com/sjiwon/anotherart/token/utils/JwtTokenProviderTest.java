package com.sjiwon.anotherart.token.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtToken -> JwtTokenProvider 테스트")
class JwtTokenProviderTest {
    private static final String SECRET_KEY = "asdjflsadjflj120993i09fa0sdjfjsdAfasjfawjfwjaslfja1120h10hdas80hf0sadhf812e0h01";
    private static final long successAccessTokenValidity = 7200L;
    private static final long failureAccessTokenValidity = 0L;
    private static final long successRefreshTokenValidity = 1209600L;
    private static final long failureRefreshTokenValidity = 0L;

    @Nested
    @DisplayName("Access Token & Refresh Token 발급 성공")
    class issuanceToken {
        private static final JwtTokenProvider JWT_TOKEN_PROVIDER = new JwtTokenProvider(SECRET_KEY, successAccessTokenValidity, successRefreshTokenValidity);

        @Test
        @DisplayName("memberId를 통해서 Access Token 발급에 성공한다")
        void test1() {
            // given
            final Long memberId = 1L;

            // when
            String accessToken = JWT_TOKEN_PROVIDER.createAccessToken(memberId);

            // then
            assertThat(accessToken).isNotNull();
            assertThat(JWT_TOKEN_PROVIDER.getPayload(accessToken)).isEqualTo(memberId);
            assertThat(JWT_TOKEN_PROVIDER.isTokenInvalid(accessToken)).isFalse();
        }

        @Test
        @DisplayName("memberId를 통해서 Refresh Token 발급에 성공한다")
        void test2() {
            // given
            final Long memberId = 1L;

            // when
            String refreshToken = JWT_TOKEN_PROVIDER.createRefreshToken(memberId);

            // then
            assertThat(refreshToken).isNotNull();
            assertThat(JWT_TOKEN_PROVIDER.getPayload(refreshToken)).isEqualTo(memberId);
            assertThat(JWT_TOKEN_PROVIDER.isTokenInvalid(refreshToken)).isFalse();
        }
    }


    @Test
    @DisplayName("발급받은 토큰이 만료됨에 따라 예외가 발생한다")
    void test() {
        // given
        final JwtTokenProvider JWT_TOKEN_PROVIDER = new JwtTokenProvider(SECRET_KEY, failureAccessTokenValidity, failureRefreshTokenValidity);
        final Long memberId = 1L;

        // when
        String accessToken = JWT_TOKEN_PROVIDER.createAccessToken(memberId);
        String refreshToken = JWT_TOKEN_PROVIDER.createRefreshToken(memberId);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(refreshToken).isNotNull();
        assertThat(JWT_TOKEN_PROVIDER.isTokenInvalid(accessToken)).isTrue();
        assertThat(JWT_TOKEN_PROVIDER.isTokenInvalid(refreshToken)).isTrue();
    }
}