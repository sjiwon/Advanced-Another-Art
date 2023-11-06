package com.sjiwon.anotherart.member.application;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member [Service Layer] -> MemberValidator 테스트")
class MemberValidatorTest extends ServiceTest {
    @Autowired
    private MemberValidator memberValidator;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
    }

    @Test
    @DisplayName("닉네임 중복에 대한 검증을 진행한다")
    void checkUniqueNickname() {
        final Nickname same = member.getNickname();
        final Nickname diff = Nickname.from("diff" + member.getNicknameValue());

        assertThatThrownBy(() -> memberValidator.validateNickname(same))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateNickname(diff));
    }

    @Test
    @DisplayName("닉네임 중복에 대한 검증을 진행한다 [타인이 사용하고 있는지 여부]")
    void checkUniqueNicknameIfOtherUses() {
        final Member compare = memberRepository.save(MEMBER_B.toMember());

        assertThatThrownBy(() -> memberValidator.validateNicknameForModify(member.getId(), compare.getNickname()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateNicknameForModify(member.getId(), member.getNickname()));
    }

    @Test
    @DisplayName("로그인 아이디 중복에 대한 검증을 진행한다")
    void checkUniqueLoginId() {
        final String same = member.getLoginId();
        final String diff = member.getLoginId() + "diff";

        assertThatThrownBy(() -> memberValidator.validateLoginId(same))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_LOGIN_ID.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateLoginId(diff));
    }

    @Test
    @DisplayName("전화번호 중복에 대한 검증을 진행한다")
    void checkUniquePhone() {
        final String same = member.getPhone().getValue();
        final String diff = member.getPhone().getValue().replaceAll("0", "9");

        assertThatThrownBy(() -> memberValidator.validatePhone(same))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        assertDoesNotThrow(() -> memberValidator.validatePhone(diff));
    }

    @Test
    @DisplayName("이메일 중복에 대한 검증을 진행한다")
    void checkUniqueEmail() {
        final Email same = member.getEmail();
        final Email diff = Email.from("diff" + member.getEmailValue());

        assertThatThrownBy(() -> memberValidator.validateEmail(same))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateEmail(diff));
    }
}
