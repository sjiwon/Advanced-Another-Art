package com.sjiwon.anotherart.token.domain.model;

import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Token -> 도메인 [Token] 테스트")
class TokenTest {
    private final Member member = MEMBER_A.toDomain().apply(1L);

    @Test
    @DisplayName("Token을 업데이트한다")
    void updateRefreshToken() {
        // given
        final Token token = new Token(member.getId(), REFRESH_TOKEN);

        // when
        token.updateRefreshToken(REFRESH_TOKEN + "_update");

        // then
        assertThat(token.getRefreshToken()).isEqualTo(REFRESH_TOKEN + "_update");
    }
}
