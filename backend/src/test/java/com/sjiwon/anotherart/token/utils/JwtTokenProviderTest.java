package com.sjiwon.anotherart.token.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Token [Utils] -> JwtTokenProvider 테스트")
class JwtTokenProviderTest {
    private static final String SECRET_KEY = "asldfjsadlfjalksjf01jf02j9012f0120f12jf1j29v0saduf012ue101212c01";
    private static final JwtTokenProvider INVALID_PROVIDER = new JwtTokenProvider(SECRET_KEY, 0L, 0L);
    private static final JwtTokenProvider VALID_PROVIDER = new JwtTokenProvider(SECRET_KEY, 7200L, 7200L);
    private static final Long MEMBER_ID = 1L;

    @Test
    @DisplayName("AccessToken과 RefreshToken을 발급한다")
    void issueAccessTokenAndRefreshToken() {
        // when
        final String accessToken = VALID_PROVIDER.createAccessToken(MEMBER_ID);
        final String refreshToken = VALID_PROVIDER.createRefreshToken(MEMBER_ID);

        // then
        assertAll(
                () -> assertThat(accessToken).isNotNull(),
                () -> assertThat(refreshToken).isNotNull()
        );
    }

    @Test
    @DisplayName("Token의 Payload를 추출한다")
    void extractTokenPayload() {
        // when
        final String accessToken = VALID_PROVIDER.createAccessToken(MEMBER_ID);

        // then
        assertThat(VALID_PROVIDER.getId(accessToken)).isEqualTo(MEMBER_ID);
    }

    @Test
    @DisplayName("Token 만료에 대한 유효성을 검증한다")
    void validateTokenExpire() {
        // given
        final String validToken = VALID_PROVIDER.createAccessToken(MEMBER_ID);
        final String invalidToken = INVALID_PROVIDER.createAccessToken(MEMBER_ID);

        // when
        final boolean actual1 = VALID_PROVIDER.isTokenValid(validToken);
        final boolean actual2 = INVALID_PROVIDER.isTokenValid(invalidToken);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("Token 조작에 대한 유효성을 검증한다")
    void validateTokenManipulation() {
        // given
        final String forgedToken = VALID_PROVIDER.createAccessToken(MEMBER_ID) + "hacked";

        // when
        final boolean actual = VALID_PROVIDER.isTokenValid(forgedToken);

        // then
        assertThat(actual).isFalse();
    }
}
