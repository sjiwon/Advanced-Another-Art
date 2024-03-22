package com.sjiwon.anotherart.token.domain.service;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.token.exception.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Token -> TokenProvider 테스트")
class TokenProviderTest extends UnitTest {
    private static final String SECRET_KEY = "asldfjsadlfjalksjf01jf02j9012f0120f12jf1j29v0saduf012ue101212c01";

    private final TokenProvider invalidProvider = new TokenProvider(SECRET_KEY, 0L, 0L);
    private final TokenProvider validProvider = new TokenProvider(SECRET_KEY, 7200L, 7200L);
    private final Member member = MEMBER_A.toDomain().apply(1L);

    @Test
    @DisplayName("AccessToken과 RefreshToken을 발급한다")
    void issueAccessTokenAndRefreshToken() {
        // when
        final String accessToken = validProvider.createAccessToken(member.getId(), member.getAuthority());
        final String refreshToken = validProvider.createRefreshToken(member.getId());

        // then
        assertAll(
                () -> assertThat(accessToken).isNotNull(),
                () -> assertThat(refreshToken).isNotNull()
        );
    }

    @Test
    @DisplayName("Token의 Payload를 추출한다")
    void extractTokenPayload() {
        // given
        final String accessToken = validProvider.createAccessToken(member.getId(), member.getAuthority());
        final String refreshToken = validProvider.createRefreshToken(member.getId());

        // when - then
        assertAll(
                () -> assertThat(validProvider.getId(accessToken)).isEqualTo(member.getId()),
                () -> assertThat(validProvider.getId(refreshToken)).isEqualTo(member.getId())
        );
    }

    @Test
    @DisplayName("토큰 만료에 대한 유효성 검사를 진행한다")
    void validateTokenWithExpired() {
        // given
        final String liveToken = validProvider.createAccessToken(member.getId(), member.getAuthority());
        final String expiredToken = invalidProvider.createAccessToken(member.getId(), member.getAuthority());

        // when - then
        assertAll(
                () -> assertDoesNotThrow(() -> validProvider.validateAccessToken(liveToken)),
                () -> assertThatThrownBy(() -> invalidProvider.validateAccessToken(expiredToken))
                        .isInstanceOf(InvalidTokenException.class)
        );
    }

    @Test
    @DisplayName("토큰 조작에 대한 유효성 검사를 진행한다")
    void validateTokenWithMalformed() {
        // given
        final String validToken = validProvider.createAccessToken(member.getId(), member.getAuthority());
        final String forgedToken = validProvider.createAccessToken(member.getId(), member.getAuthority()) + "hacked";

        // when - then
        assertAll(
                () -> assertDoesNotThrow(() -> validProvider.validateAccessToken(validToken)),
                () -> assertThatThrownBy(() -> invalidProvider.validateAccessToken(forgedToken))
                        .isInstanceOf(InvalidTokenException.class)
        );
    }

    @Test
    @DisplayName("토큰 Subject에 대한 유효성 검사를 진행한다")
    void validateTokenWithSubject() {
        // given
        final String accessToken = validProvider.createAccessToken(member.getId(), member.getAuthority());
        final String refreshToken = validProvider.createRefreshToken(member.getId());

        // when - then
        assertAll(
                () -> assertDoesNotThrow(() -> validProvider.validateAccessToken(accessToken)),
                () -> assertDoesNotThrow(() -> validProvider.validateRefreshToken(refreshToken)),
                () -> assertThatThrownBy(() -> validProvider.validateAccessToken(refreshToken))
                        .isInstanceOf(InvalidTokenException.class),
                () -> assertThatThrownBy(() -> validProvider.validateRefreshToken(accessToken))
                        .isInstanceOf(InvalidTokenException.class)
        );
    }
}
