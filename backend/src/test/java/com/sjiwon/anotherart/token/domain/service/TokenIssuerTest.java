package com.sjiwon.anotherart.token.domain.service;

import com.sjiwon.anotherart.common.mock.stub.StubTokenProvider;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.utils.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Token -> TokenIssuer 테스트")
public class TokenIssuerTest {
    private final TokenProvider tokenProvider = new StubTokenProvider();
    private final TokenManager tokenManager = mock(TokenManager.class);
    private final TokenIssuer sut = new TokenIssuer(tokenProvider, tokenManager);

    private final Member member = MEMBER_A.toMember().apply(1L);

    @Test
    @DisplayName("AuthToken[Access + Refresh]을 제공한다")
    void provideAuthorityToken() {
        // when
        final AuthToken authToken = sut.provideAuthorityToken(member.getId());

        // then
        assertAll(
                () -> verify(tokenManager, times(1)).synchronizeRefreshToken(member.getId(), REFRESH_TOKEN),
                () -> assertThat(authToken.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authToken.refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }

    @Test
    @DisplayName("AuthToken[Access + Refresh]을 재발급한다")
    void reissueAuthorityToken() {
        // when
        final AuthToken authToken = sut.reissueAuthorityToken(member.getId());

        // then
        assertAll(
                () -> verify(tokenManager, times(1)).updateRefreshToken(member.getId(), REFRESH_TOKEN),
                () -> assertThat(authToken.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(authToken.refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }
}
