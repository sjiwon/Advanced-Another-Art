package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 테스트")
class MemberTest {
    private static final MemberFixture MEMBER_A = MemberFixture.A;
    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();

    @Test
    @DisplayName("멤버를 생성한다")
    void test1() {
        Member memberA = MEMBER_A.toMember(ENCODER);

        assertAll(
                () -> assertThat(memberA.getName()).isEqualTo(MEMBER_A.getName()),
                () -> assertThat(memberA.getNickname()).isEqualTo(MEMBER_A.getNickname()),
                () -> assertThat(memberA.getLoginId()).isEqualTo(MEMBER_A.getLoginId()),
                () -> assertThat(ENCODER.matches(MEMBER_A.getPassword(), memberA.getPassword().getValue())).isTrue(),
                () -> assertThat(memberA.getAddress().getPostcode()).isEqualTo(MEMBER_A.getPostcode()),
                () -> assertThat(memberA.getAddress().getDefaultAddress()).isEqualTo(MEMBER_A.getDefaultAddress()),
                () -> assertThat(memberA.getAddress().getDetailAddress()).isEqualTo(MEMBER_A.getDetailAddress()),
                () -> assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(0),
                () -> assertThat(memberA.getRole()).isEqualTo(Role.USER)
        );
    }

    @Test
    @DisplayName("닉네임을 변경한다")
    void test2() {
        // given
        Member memberA = MEMBER_A.toMember(ENCODER);

        // when
        final String changeNickname = "닉네임 변경";
        memberA.changeNickname(changeNickname);

        // then
        assertThat(memberA.getNickname()).isEqualTo(changeNickname);
    }

    @Test
    @DisplayName("비밀번호를 변경한다")
    void test3() {
        // given
        Member memberA = MEMBER_A.toMember(ENCODER);
        final String samePassword = MEMBER_A.getPassword();
        final String diffPassword = MEMBER_A.getPassword() + "456";

        // when
        assertThatThrownBy(() -> memberA.changePassword(samePassword, ENCODER))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.PASSWORD_SAME_AS_BEFORE.getMessage());
        memberA.changePassword(diffPassword, ENCODER);

        // then
        assertThat(ENCODER.matches(diffPassword, memberA.getPassword().getValue())).isTrue();
    }

    @Test
    @DisplayName("주소를 변경한다")
    void test4() {
        // given
        Member memberA = MEMBER_A.toMember(ENCODER);

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
    void test5() {
        // given
        Member memberA = MEMBER_A.toMember(ENCODER);

        // when
        final int increasePoint = 10000;
        memberA.increasePoint(increasePoint);

        // then
        assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(increasePoint);
    }

    @Test
    @DisplayName("사용 가능 포인트를 감소시킨다")
    void test6() {
        // given
        Member memberA = MEMBER_A.toMember(ENCODER);
        final int increasePoint = 10000;
        memberA.increasePoint(increasePoint);

        // when
        final int decreasePoint = 5000;
        memberA.decreasePoint(decreasePoint);

        // then
        assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(increasePoint - decreasePoint);
    }

    @Test
    @DisplayName("동일한 닉네임인지 검증한다")
    void test7() {
        // given
        Member memberA = MEMBER_A.toMember(ENCODER);
        final String same = memberA.getNickname();
        final String diff = memberA.getNickname() + "diff";

        // when
        boolean actual1 = memberA.isSameNickname(same);
        boolean actual2 = memberA.isSameNickname(diff);

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    @Test
    @DisplayName("동일한 이름인지 검증한다")
    void test8() {
        // given
        Member memberA = MEMBER_A.toMember(ENCODER);
        final String same = memberA.getNickname();
        final String diff = memberA.getNickname() + "diff";

        // when
        boolean actual1 = memberA.isSameName(same);
        boolean actual2 = memberA.isSameName(diff);

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    @Test
    @DisplayName("동일한 이름인지 검증한다")
    void test9() {
        // given
        Member memberA = MEMBER_A.toMember(ENCODER);
        final Email same = memberA.getEmail();
        final Email diff = Email.from("diff" + memberA.getEmail().getValue());

        // when
        boolean actual1 = memberA.isSameEmail(same);
        boolean actual2 = memberA.isSameEmail(diff);

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }
}