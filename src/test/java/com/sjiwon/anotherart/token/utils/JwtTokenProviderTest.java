package com.sjiwon.anotherart.token.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Token [Utils] -> JwtTokenProvider 테스트")
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
            assertAll(
                    () -> assertThat(accessToken).isNotNull(),
                    () -> assertThat(JWT_TOKEN_PROVIDER.getPayload(accessToken)).isEqualTo(memberId),
                    () -> assertThat(JWT_TOKEN_PROVIDER.isTokenValid(accessToken)).isTrue()
            );
        }

        @Test
        @DisplayName("memberId를 통해서 Refresh Token 발급에 성공한다")
        void test2() {
            // given
            final Long memberId = 1L;

            // when
            String refreshToken = JWT_TOKEN_PROVIDER.createRefreshToken(memberId);

            // then
            assertAll(
                    () -> assertThat(refreshToken).isNotNull(),
                    () -> assertThat(JWT_TOKEN_PROVIDER.getPayload(refreshToken)).isEqualTo(memberId),
                    () -> assertThat(JWT_TOKEN_PROVIDER.isTokenValid(refreshToken)).isTrue()
            );
        }
    }


    @Test
    @DisplayName("발급받은 토큰이 만료되었을 경우에 대한 유효성 검사를 진행한다")
    void test3() {
        // given
        final JwtTokenProvider JWT_TOKEN_PROVIDER = new JwtTokenProvider(SECRET_KEY, failureAccessTokenValidity, failureRefreshTokenValidity);
        final Long memberId = 1L;

        // when
        String accessToken = JWT_TOKEN_PROVIDER.createAccessToken(memberId);
        String refreshToken = JWT_TOKEN_PROVIDER.createRefreshToken(memberId);

        // then
        assertAll(
                () -> assertThat(accessToken).isNotNull(),
                () -> assertThat(refreshToken).isNotNull(),
                () -> assertThat(JWT_TOKEN_PROVIDER.isTokenValid(accessToken)).isFalse(),
                () -> assertThat(JWT_TOKEN_PROVIDER.isTokenValid(refreshToken)).isFalse()
        );
    }
    
    @Test
    @DisplayName("임의 조작한 토큰에 대해서 유효성 검사를 진행한다")
    void test4() {
        // given
        final JwtTokenProvider JWT_TOKEN_PROVIDER = new JwtTokenProvider(SECRET_KEY, failureAccessTokenValidity, failureRefreshTokenValidity);
        final Long memberId = 1L;

        // when
        String invalidAccessToken = JWT_TOKEN_PROVIDER.createAccessToken(memberId) + "fake";
        String invalidRefreshToken = JWT_TOKEN_PROVIDER.createRefreshToken(memberId) + "fake";

        // then
        assertAll(
                () -> assertThat(invalidAccessToken).isNotNull(),
                () -> assertThat(invalidRefreshToken).isNotNull(),
                () -> assertThat(JWT_TOKEN_PROVIDER.isTokenValid(invalidAccessToken)).isFalse(),
                () -> assertThat(JWT_TOKEN_PROVIDER.isTokenValid(invalidRefreshToken)).isFalse()
        );
    }
}