package com.sjiwon.anotherart.global.security.provider;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Security [Provider] -> AjaxAuthenticationProvider 테스트")
class AjaxAuthenticationProviderTest extends ServiceTest {
    @Autowired
    private AuthenticationProvider authenticationProvider;

    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();
    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
    }

    @Test
    @DisplayName("로그인 아이디에 해당하는 사용자가 존재하지 않음에 따라 인증에 실패한다")
    void throwExceptionByMemberNotFound() {
        // given
        final Authentication authentication = new UsernamePasswordAuthenticationToken(
                "fake",
                MEMBER_A.getPassword()
        );

        // when - then
        assertThatThrownBy(() -> authenticationProvider.authenticate(authentication))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않음에 따라 인증에 실패한다")
    void throwExceptionByNotCorrectPassword() {
        // given
        final Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getLoginId(),
                "fake"
        );

        // when - then
        assertThatThrownBy(() -> authenticationProvider.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage(MemberErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("인증에 성공한다")
    void success() {
        // given
        final Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getLoginId(),
                MEMBER_A.getPassword()
        );

        // when
        final Authentication authenticate = authenticationProvider.authenticate(authentication);

        // then
        final MemberPrincipal principal = (MemberPrincipal) authenticate.getPrincipal();
        final List<String> authorities = authenticate.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertAll(
                () -> assertThat(principal.id()).isEqualTo(member.getId()),
                () -> assertThat(principal.name()).isEqualTo(member.getName()),
                () -> assertThat(principal.nickname()).isEqualTo(member.getNicknameValue()),
                () -> assertThat(principal.loginId()).isEqualTo(member.getLoginId()),
                () -> assertThat(ENCODER.matches(MEMBER_A.getPassword(), principal.password())).isTrue(),
                () -> assertThat(principal.role()).isEqualTo(member.getRole().getAuthority()),
                () -> assertThat(authorities).containsExactlyInAnyOrder(member.getRole().getAuthority())
        );
    }
}
