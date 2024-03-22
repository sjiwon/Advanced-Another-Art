package com.sjiwon.anotherart.global.security.provider;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Global/Security -> RdbUserDetailsService 테스트")
class RdbUserDetailsServiceTest extends UnitTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final RdbUserDetailsService sut = new RdbUserDetailsService(memberRepository);

    private final Member member = MEMBER_A.toDomain().apply(1L);

    @Test
    @DisplayName("로그인 아이디에 해당하는 Member가 없다면 예외가 발생한다")
    void throwExceptionByMemberNotFound() {
        // given
        final String loginId = member.getLoginId();
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.empty());

        // when - then
        assertThatThrownBy(() -> sut.loadUserByUsername(loginId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(MemberExceptionCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 아이디에 해당하는 Member 정보를 Wrapping한 UserDetails를 응답받는다")
    void success() {
        // given
        final String loginId = member.getLoginId();
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(member));

        // when
        final UserDetails userDetails = sut.loadUserByUsername(loginId);

        // then
        final MemberPrincipal memberPrincipal = (MemberPrincipal) userDetails;
        assertAll(
                () -> assertThat(memberPrincipal.id()).isEqualTo(member.getId()),
                () -> assertThat(memberPrincipal.name()).isEqualTo(member.getName()),
                () -> assertThat(memberPrincipal.nickname()).isEqualTo(member.getNickname().getValue()),
                () -> assertThat(memberPrincipal.loginId()).isEqualTo(member.getLoginId()),
                () -> assertThat(memberPrincipal.password()).isEqualTo(member.getPassword().getValue()),
                () -> assertThat(memberPrincipal.authority()).isEqualTo(member.getRole().getAuthority())
        );
    }
}
