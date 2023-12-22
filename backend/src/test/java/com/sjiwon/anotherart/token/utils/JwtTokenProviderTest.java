package com.sjiwon.anotherart.token.utils;

import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.token.exception.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Token -> JwtTokenProvider 테스트")
class JwtTokenProviderTest {
    private static final String SECRET_KEY = "asldfjsadlfjalksjf01jf02j9012f0120f12jf1j29v0saduf012ue101212c01";

    private final JwtTokenProvider invalidProvider = new JwtTokenProvider(SECRET_KEY, 0L, 0L);
    private final JwtTokenProvider validProvider = new JwtTokenProvider(SECRET_KEY, 7200L, 7200L);
    private final Member member = MEMBER_A.toMember().apply(1L);

    @Test
    @DisplayName("AccessToken과 RefreshToken을 발급한다")
    void issueAccessTokenAndRefreshToken() {
        // when
        final String accessToken = validProvider.createAccessToken(member.getId());
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
        // when
        final String accessToken = validProvider.createAccessToken(member.getId());

        // then
        assertThat(validProvider.getId(accessToken)).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("Token 만료에 대한 유효성을 검증한다")
    void validateTokenExpire() {
        // given
        final String validToken = validProvider.createAccessToken(member.getId());
        final String invalidToken = invalidProvider.createAccessToken(member.getId());

        // when - then
        assertDoesNotThrow(() -> validProvider.validateToken(validToken));
        assertThatThrownBy(() -> invalidProvider.validateToken(invalidToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("Token 조작에 대한 유효성을 검증한다")
    void validateTokenManipulation() {
        // given
        final String forgedToken = validProvider.createAccessToken(member.getId()) + "hacked";

        // when - then
        assertThatThrownBy(() -> validProvider.validateToken(forgedToken))
                .isInstanceOf(InvalidTokenException.class);
    }
}
