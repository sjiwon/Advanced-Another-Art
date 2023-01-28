package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.TokenResponse;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("Token [Service Layer] -> TokenReissueService 테스트")
class TokenReissueServiceTest extends ServiceTest {
    @InjectMocks
    private TokenReissueService tokenReissueService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisTokenService redisTokenService;
    
    @Test
    @DisplayName("RTR 정책에 의해서 이전에 사용한 Refresh Token으로 Access Token + Refresh Token을 재발급받으려고 하면 예외가 발생한다")
    void test1() {
        // given
        final String refreshToken = "refresh-token";
        given(redisTokenService.isRefreshTokenExists(refreshToken)).willReturn(false);

        // when - then
        Assertions.assertThatThrownBy(() -> tokenReissueService.reissueTokens(refreshToken))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuthErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("Refresh Token(RTR)을 활용해서 Access Token + Refresh Token을 재발급받는다")
    void test2() {
        // given
        final Long memberId = 1L;
        final String accessToken = "access-token";
        final String refreshToken = "refresh-token";
        given(redisTokenService.isRefreshTokenExists(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getPayload(refreshToken)).willReturn(memberId);
        given(jwtTokenProvider.createAccessToken(memberId)).willReturn(accessToken);
        given(jwtTokenProvider.createRefreshToken(memberId)).willReturn(refreshToken);

        // when
        TokenResponse tokenResponse = tokenReissueService.reissueTokens(refreshToken);

        // then
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getAccessToken()).isEqualTo(accessToken);
        assertThat(tokenResponse.getRefreshToken()).isEqualTo(refreshToken);
    }
}