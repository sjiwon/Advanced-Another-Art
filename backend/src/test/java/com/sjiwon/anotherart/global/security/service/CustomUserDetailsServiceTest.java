package com.sjiwon.anotherart.global.security.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Security [Service] -> CustomUserDetailsService 테스트")
class CustomUserDetailsServiceTest extends ServiceTest {
    @Autowired
    private UserDetailsService userDetailsService;

    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();
    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
    }

    @Test
    @DisplayName("로그인 아이디에 해당하는 사용자가 존재하지 않는다")
    void throwExceptionByMemberNotFound() {
        // when - then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("fake"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("로그인 아이디에 해당하는 사용자를 찾아온다 [UserDetails]")
    void success() {
        // when
        MemberPrincipal userDetails = (MemberPrincipal) userDetailsService.loadUserByUsername(member.getLoginId());

        // then
        assertAll(
                () -> assertThat(userDetails.id()).isEqualTo(member.getId()),
                () -> assertThat(userDetails.name()).isEqualTo(member.getName()),
                () -> assertThat(userDetails.nickname()).isEqualTo(member.getNicknameValue()),
                () -> assertThat(userDetails.loginId()).isEqualTo(member.getLoginId()),
                () -> assertThat(ENCODER.matches(MEMBER_A.getPassword(), userDetails.password())).isTrue(),
                () -> assertThat(userDetails.role()).isEqualTo(member.getRole().getAuthority())
        );
    }
}
