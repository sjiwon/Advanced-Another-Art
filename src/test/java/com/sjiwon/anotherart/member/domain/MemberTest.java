package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 테스트")
class MemberTest {
    private static final MemberFixture MEMBER_A = MemberFixture.A;

    @Test
    @DisplayName("멤버를 생성한다")
    void test1(){
        Member memberA = MEMBER_A.toMember(PasswordEncoderUtils.getEncoder());

        assertAll(
                () -> assertThat(memberA.getName()).isEqualTo(MEMBER_A.getName()),
                () -> assertThat(memberA.getNickname()).isEqualTo(MEMBER_A.getNickname()),
                () -> assertThat(memberA.getLoginId()).isEqualTo(MEMBER_A.getLoginId()),
                () -> assertThat(PasswordEncoderUtils.getEncoder().matches(MEMBER_A.getPassword(), memberA.getPassword().getValue())).isTrue(),
                () -> assertThat(memberA.getAddress().getPostcode()).isEqualTo(MEMBER_A.getPostcode()),
                () -> assertThat(memberA.getAddress().getDefaultAddress()).isEqualTo(MEMBER_A.getDefaultAddress()),
                () -> assertThat(memberA.getAddress().getDetailAddress()).isEqualTo(MEMBER_A.getDetailAddress()),
                () -> assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(0),
                () -> assertThat(memberA.getRole()).isEqualTo(Role.USER)
        );
    }

    @Test
    @DisplayName("닉네임을 변경한다")
    void test2(){
        // given
        Member memberA = MEMBER_A.toMember(PasswordEncoderUtils.getEncoder());

        // when
        final String changeNickname = "닉네임 변경";
        memberA.changeNickname(changeNickname);

        // then
        assertThat(memberA.getNickname()).isEqualTo(changeNickname);
    }

    @Test
    @DisplayName("비밀번호를 변경한다")
    void test3(){
        // given
        Member memberA = MEMBER_A.toMember(PasswordEncoderUtils.getEncoder());

        // when
        final String changePassword = "987ABCapq!@#";
        memberA.changePassword(changePassword, PasswordEncoderUtils.getEncoder());

        // then
        assertThat(PasswordEncoderUtils.getEncoder().matches(changePassword, memberA.getPassword().getValue())).isTrue();
    }

    @Test
    @DisplayName("주소를 변경한다")
    void test4(){
        // given
        Member memberA = MEMBER_A.toMember(PasswordEncoderUtils.getEncoder());

        // when
        final int changePostcode = 98765;
        final String changeDefault = "성남";
        final String changeDetail = "카카오";
        memberA.changeAddress(changePostcode, changeDefault, changeDetail);

        // then
        assertThat(memberA.getAddress().getPostcode()).isEqualTo(changePostcode);
        assertThat(memberA.getAddress().getDefaultAddress()).isEqualTo(changeDefault);
        assertThat(memberA.getAddress().getDetailAddress()).isEqualTo(changeDetail);
    }

    @Test
    @DisplayName("사용 가능 포인트를 증가시킨다")
    void test5(){
        // given
        Member memberA = MEMBER_A.toMember(PasswordEncoderUtils.getEncoder());

        // when
        final int increasePoint = 10000;
        memberA.increasePoint(increasePoint);

        // then
        assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(increasePoint);
    }

    @Test
    @DisplayName("사용 가능 포인트를 감소시킨다")
    void test6(){
        // given
        Member memberA = MEMBER_A.toMember(PasswordEncoderUtils.getEncoder());
        final int increasePoint = 10000;
        memberA.increasePoint(increasePoint);

        // when
        final int decreasePoint = 5000;
        memberA.decreasePoint(decreasePoint);

        // then
        assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(increasePoint - decreasePoint);
    }
}