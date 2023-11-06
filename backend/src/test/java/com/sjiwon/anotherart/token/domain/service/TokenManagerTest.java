package com.sjiwon.anotherart.token.domain.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.token.domain.model.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Token [Service Layer] -> TokenManager 테스트")
class TokenManagerTest extends ServiceTest {
    @Autowired
    private TokenManager tokenManager;

    private Member member;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
        refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
    }

    @Nested
    @DisplayName("RefreshToken 동기화")
    class synchronizedRefreshToken {
        @Test
        @DisplayName("RefreshToken을 보유하고 있지 않은 사용자에게는 새로운 RefreshToken을 발급한다")
        void reissueRefreshToken() {
            // when
            tokenManager.synchronizeRefreshToken(member.getId(), refreshToken);

            // then
            final Token findToken = tokenRepository.findByMemberId(member.getId()).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(refreshToken);
        }

        @Test
        @DisplayName("RefreshToken을 보유하고 있는 사용자에게는 새로운 RefreshToken으로 업데이트한다")
        void updateRefreshToken() {
            // given
            tokenRepository.save(Token.issueRefreshToken(member.getId(), refreshToken));

            // when
            final String newRefreshToken = refreshToken + "new";
            tokenManager.synchronizeRefreshToken(member.getId(), newRefreshToken);

            // then
            final Token findToken = tokenRepository.findByMemberId(member.getId()).orElseThrow();
            assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
        }
    }

    @Test
    @DisplayName("RTR정책에 의해서 RefreshToken을 재발급한다")
    void reissueRefreshTokenByRtrPolicy() {
        // given
        tokenRepository.save(Token.issueRefreshToken(member.getId(), refreshToken));

        // when
        final String newRefreshToken = refreshToken + "new";
        tokenManager.reissueRefreshTokenByRtrPolicy(member.getId(), newRefreshToken);

        // then
        final Token findToken = tokenRepository.findByMemberId(member.getId()).orElseThrow();
        assertThat(findToken.getRefreshToken()).isEqualTo(newRefreshToken);
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken을 삭제한다")
    void deleteRefreshTokenByMemberId() {
        // given
        tokenRepository.save(Token.issueRefreshToken(member.getId(), refreshToken));

        // when
        tokenManager.deleteRefreshTokenByMemberId(member.getId());

        // then
        assertThat(tokenRepository.findByMemberId(member.getId())).isEmpty();
    }

    @Test
    @DisplayName("사용자가 보유하고 있는 RefreshToken인지 확인한다")
    void checkMemberHasSpecificRefreshToken() {
        // given
        tokenRepository.save(Token.issueRefreshToken(member.getId(), refreshToken));

        // when
        final boolean actual1 = tokenManager.isRefreshTokenExists(member.getId(), refreshToken);
        final boolean actual2 = tokenManager.isRefreshTokenExists(member.getId(), refreshToken + "fake");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
