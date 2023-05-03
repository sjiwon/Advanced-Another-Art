package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.token.domain.Token;
import com.sjiwon.anotherart.token.exception.TokenErrorCode;
import com.sjiwon.anotherart.token.service.response.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth [Service Layer] -> TokenReissueService 테스트")
class TokenReissueServiceTest extends ServiceTest {
    @Autowired
    private TokenReissueService tokenReissueService;

    private Member member;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
        refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
    }

    @Nested
    @DisplayName("토큰 재발급")
    class reissueTokens {
        @Test
        @DisplayName("RefreshToken이 유효하지 않으면 토큰 재발급에 실패한다")
        void throwExceptionByInvalidRefreshToken() {
            // when - then
            assertThatThrownBy(() -> tokenReissueService.reissueTokens(member.getId(), refreshToken))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(TokenErrorCode.AUTH_INVALID_TOKEN.getMessage());
        }

        @Test
        @DisplayName("유효성이 확인된 RefreshToken을 통해서 AccessToken과 RefreshToken을 재발급받는다")
        void reissueSuccess() {
            // given
            tokenRepository.save(Token.issueRefreshToken(member.getId(), refreshToken));

            // when
            TokenResponse response = tokenReissueService.reissueTokens(member.getId(), refreshToken);

            // then
            assertAll(
                    () -> assertThat(response).isNotNull(),
                    () -> assertThat(response)
                            .usingRecursiveComparison()
                            .isNotNull()
            );
        }
    }
}
