package com.sjiwon.anotherart.member.domain.repository;

import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository sut;

    @Test
    @DisplayName("로그인 아이디로 사용자를 조회한다")
    void findByLoginId() {
        // given
        final Member member = sut.save(MEMBER_A.toMember());

        // when
        final Optional<Member> emptyMember = sut.findByLoginId("fake");
        final Member actualMember = sut.getByLoginId(member.getLoginId());

        // then
        assertAll(
                () -> assertThat(emptyMember).isEmpty(),
                () -> assertThat(actualMember).isEqualTo(member)
        );
    }

    @Test
    @DisplayName("이름 + 이메일로 사용자를 조회한다")
    void findByNameAndEmail() {
        // given
        final Member member = sut.save(MEMBER_A.toMember());

        // when
        final Optional<Member> emptyMember1 = sut.findByNameAndEmail("diff" + member.getName(), member.getEmail().getValue());
        final Optional<Member> emptyMember2 = sut.findByNameAndEmail(member.getName(), "diff" + member.getEmail().getValue());
        final Member actualMember = sut.getByNameAndEmail(member.getName(), member.getEmail().getValue());

        // then
        assertAll(
                () -> assertThat(emptyMember1).isEmpty(),
                () -> assertThat(emptyMember2).isEmpty(),
                () -> assertThat(actualMember).isEqualTo(member)
        );
    }

    @Test
    @DisplayName("닉네임에 해당하는 사용자가 존재하는지 확인한다")
    void existsByNicknameValue() {
        // given
        final Member member = sut.save(MEMBER_A.toMember());
        final String same = member.getNickname().getValue();
        final String diff = "diff" + member.getNickname().getValue();

        // when
        final boolean actual1 = sut.existsByNicknameValue(same);
        final boolean actual2 = sut.existsByNicknameValue(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("해당 닉네임을 본인이 아닌 타인이 사용하고 있는지 확인한다")
    void isNicknameUsedByOther() {
        // given
        final Member memberA = sut.save(MEMBER_A.toMember());
        final Member memberB = sut.save(MEMBER_B.toMember());

        // when
        final boolean actual1 = sut.isNicknameUsedByOther(memberA.getId(), memberA.getNickname().getValue());
        final boolean actual2 = sut.isNicknameUsedByOther(memberA.getId(), memberB.getNickname().getValue());
        final boolean actual3 = sut.isNicknameUsedByOther(memberB.getId(), memberB.getNickname().getValue());
        final boolean actual4 = sut.isNicknameUsedByOther(memberB.getId(), memberA.getNickname().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse(),
                () -> assertThat(actual4).isTrue()
        );
    }

    @Test
    @DisplayName("로그인 아이디에 해당하는 사용자가 존재하는지 확인한다")
    void existsByLoginId() {
        // given
        final Member member = sut.save(MEMBER_A.toMember());
        final String same = member.getLoginId();
        final String diff = member.getLoginId() + "fake";

        // when
        final boolean actual1 = sut.existsByLoginId(same);
        final boolean actual2 = sut.existsByLoginId(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("전화번호에 해당하는 사용자가 존재하는지 확인한다")
    void existsByPhoneValue() {
        // given
        final Member member = sut.save(MEMBER_A.toMember());
        final String same = member.getPhone().getValue();
        final String diff = member.getPhone().getValue().replaceAll("0", "9");

        // when
        final boolean actual1 = sut.existsByPhoneValue(same);
        final boolean actual2 = sut.existsByPhoneValue(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("이메일에 해당하는 사용자가 존재하는지 확인한다")
    void existsByEmailValue() {
        // given
        final Member member = sut.save(MEMBER_A.toMember());
        final String same = member.getEmail().getValue();
        final String diff = "diff" + member.getEmail().getValue();

        // when
        final boolean actual1 = sut.existsByEmailValue(same);
        final boolean actual2 = sut.existsByEmailValue(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("이름 + 이메일 + 로그인 아이디에 해당하는 사용자가 존재하는지 확인한다")
    void existsByNameAndEmailAndLoginId() {
        // given
        final Member member = sut.save(MEMBER_A.toMember());

        // when
        final boolean actual1 = sut.existsByNameAndEmailValueAndLoginId(member.getName(), member.getEmail().getValue(), member.getLoginId());
        final boolean actual2 = sut.existsByNameAndEmailValueAndLoginId("diff" + member.getName(), member.getEmail().getValue(), member.getLoginId());
        final boolean actual3 = sut.existsByNameAndEmailValueAndLoginId(member.getName(), "diff" + member.getEmail().getValue(), member.getLoginId());
        final boolean actual4 = sut.existsByNameAndEmailValueAndLoginId(member.getName(), member.getEmail().getValue(), "diff" + member.getLoginId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse(),
                () -> assertThat(actual3).isFalse(),
                () -> assertThat(actual4).isFalse()
        );
    }
}
