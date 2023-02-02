package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("Member [Service Layer] -> MemberFindService 테스트")
class MemberFindServiceTest extends ServiceTest {
    @InjectMocks
    private MemberFindService memberFindService;

    @Mock
    private MemberRepository memberRepository;

    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();

    @Test
    @DisplayName("ID(PK)로 사용자 조회하기")
    void test1() {
        // given
        final Member member = MemberFixture.A.toMember(ENCODER);
        final Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.ofNullable(member));

        // when
        final Long fakeId = 100L;
        Member actualMember = memberFindService.findById(memberId);
        assertThatThrownBy(() -> memberFindService.findById(fakeId))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

        // then
        assertThat(actualMember).isNotNull();
        assertThat(actualMember.getName()).isEqualTo(member.getName());
        assertThat(actualMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(actualMember.getLoginId()).isEqualTo(member.getLoginId());
    }

    @Test
    @DisplayName("로그인 아이디로 사용자 조회하기")
    void test2() {
        // given
        final Member member = MemberFixture.A.toMember(ENCODER);
        final String loginId = member.getLoginId();
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.ofNullable(member));

        // when
        final String fakeLoginId = "fake" + loginId;
        Member actualMember = memberFindService.findByLoginId(loginId);
        assertThatThrownBy(() -> memberFindService.findByLoginId(fakeLoginId))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

        // then
        assertThat(actualMember).isNotNull();
        assertThat(actualMember.getName()).isEqualTo(member.getName());
        assertThat(actualMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(actualMember.getLoginId()).isEqualTo(member.getLoginId());
    }

    @Test
    @DisplayName("이름, 이메일로 사용자 조회하기")
    void test3() {
        // given
        final Member member = MemberFixture.A.toMember(ENCODER);
        final String name = member.getName();
        final Email email = member.getEmail();
        given(memberRepository.findByNameAndEmail(name, email)).willReturn(Optional.ofNullable(member));

        // when
        final String fakeName = "fake" + name;
        Member actualMember = memberFindService.findByNameAndEmail(name, email);
        assertThatThrownBy(() -> memberFindService.findByNameAndEmail(fakeName, email))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

        // then
        assertThat(actualMember).isNotNull();
        assertThat(actualMember.getName()).isEqualTo(member.getName());
        assertThat(actualMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(actualMember.getLoginId()).isEqualTo(member.getLoginId());
    }
}