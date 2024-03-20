package com.sjiwon.anotherart.token.application.usecase;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.domain.service.TokenIssuer;
import com.sjiwon.anotherart.token.domain.service.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Token -> ReissueTokenUseCase 테스트")
class ReissueTokenUseCaseTest extends UnitTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final TokenProvider tokenProvider = mock(TokenProvider.class);
    private final TokenIssuer tokenIssuer = mock(TokenIssuer.class);
    private final ReissueTokenUseCase sut = new ReissueTokenUseCase(
            memberRepository,
            tokenProvider,
            tokenIssuer
    );

    private final Member member = MEMBER_A.toMember().apply(1L);

    @Test
    @DisplayName("RefreshToken이 유효하지 않으면 토큰 재발급에 실패한다")
    void throwExceptionByInvalidRefreshToken() {
        // given
        given(tokenProvider.getId(REFRESH_TOKEN)).willReturn(member.getId());
        given(memberRepository.getById(member.getId())).willReturn(member);
        given(tokenIssuer.isMemberRefreshToken(member.getId(), REFRESH_TOKEN)).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.invoke(REFRESH_TOKEN))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuthErrorCode.INVALID_TOKEN.getMessage());

        assertAll(
                () -> verify(tokenProvider, times(1)).getId(REFRESH_TOKEN),
                () -> verify(tokenIssuer, times(1)).isMemberRefreshToken(member.getId(), REFRESH_TOKEN)
        );
    }

    @Test
    @DisplayName("유효성이 확인된 RefreshToken을 통해서 새로운 AccessToken과 RefreshToken을 재발급받는다")
    void reissueSuccess() {
        // given
        given(tokenProvider.getId(REFRESH_TOKEN)).willReturn(member.getId());
        given(memberRepository.getById(member.getId())).willReturn(member);
        given(tokenIssuer.isMemberRefreshToken(member.getId(), REFRESH_TOKEN)).willReturn(true);

        final AuthToken authToken = new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN);
        given(tokenIssuer.reissueAuthorityToken(member.getId(), member.getAuthority())).willReturn(authToken);

        // when
        final AuthToken result = sut.invoke(REFRESH_TOKEN);

        // then
        assertAll(
                () -> verify(tokenProvider, times(1)).getId(REFRESH_TOKEN),
                () -> verify(tokenIssuer, times(1)).isMemberRefreshToken(member.getId(), REFRESH_TOKEN),
                () -> assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN),
                () -> assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN)
        );
    }
}
