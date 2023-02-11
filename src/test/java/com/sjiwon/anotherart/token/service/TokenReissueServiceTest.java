package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.TokenResponse;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.token.domain.RefreshToken;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Token [Service Layer] -> TokenReissueService 테스트")
@RequiredArgsConstructor
class TokenReissueServiceTest extends ServiceIntegrateTest {
    private final TokenReissueService tokenReissueService;
    private final JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("RTR 정책에 의해서 이전에 사용한 Refresh Token으로 Access Token + Refresh Token을 재발급받으려고 하면 예외가 발생한다")
    void test1() {
        // given
        Member member = createMember();
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        // when - then
        assertThatThrownBy(() -> tokenReissueService.reissueTokens(member.getId(), refreshToken))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuthErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("Refresh Token(RTR)을 활용해서 Access Token + Refresh Token을 재발급받는다")
    void test2() {
        // given
        Member member = createMember();
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        refreshTokenRepository.save(RefreshToken.issueRefreshToken(member.getId(), refreshToken));

        // when
        TokenResponse tokenResponse = tokenReissueService.reissueTokens(member.getId(), refreshToken);

        // then
        assertAll(
                () -> assertThat(tokenResponse).isNotNull(),
                () -> assertThat(tokenResponse.getAccessToken()).isNotNull(),
                () -> assertThat(tokenResponse.getRefreshToken()).isNotNull()
        );
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }
}
