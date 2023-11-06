package com.sjiwon.anotherart.member.domain.repository;

import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Repository Layer] -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
    }

    @Test
    @DisplayName("로그인 아이디로 사용자를 조회한다")
    void findByLoginId() {
        // when
        final Optional<Member> emptyMember = memberRepository.findByLoginId("fake");
        final Member actualMember = memberRepository.findByLoginId(member.getLoginId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(emptyMember).isEmpty(),
                () -> assertThat(actualMember).isEqualTo(member)
        );
    }

    @Test
    @DisplayName("이름 + 이메일로 사용자를 조회한다")
    void findByNameAndEmail() {
        // when
        final Optional<Member> emptyMember1 = memberRepository.findByNameAndEmail(
                "diff" + member.getName(),
                member.getEmail()
        );
        final Optional<Member> emptyMember2 = memberRepository.findByNameAndEmail(
                member.getName(),
                Email.from("diff" + member.getEmailValue())
        );
        final Member actualMember = memberRepository.findByNameAndEmail(member.getName(), member.getEmail()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(emptyMember1).isEmpty(),
                () -> assertThat(emptyMember2).isEmpty(),
                () -> assertThat(actualMember).isEqualTo(member)
        );
    }

    @Test
    @DisplayName("닉네임에 해당하는 사용자가 존재하는지 확인한다")
    void existsByNickname() {
        // given
        final Nickname same = member.getNickname();
        final Nickname diff = Nickname.from("diff" + member.getNicknameValue());

        // when
        final boolean actual1 = memberRepository.existsByNickname(same);
        final boolean actual2 = memberRepository.existsByNickname(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("해당 닉네임을 타인이 사용하고 있는지 확인한다")
    void existsByIdNotAndNickname() {
        // given
        final Member compare = memberRepository.save(MEMBER_B.toMember());

        // when
        final boolean actual1 = memberRepository.existsByIdNotAndNickname(member.getId(), member.getNickname());
        final boolean actual2 = memberRepository.existsByIdNotAndNickname(member.getId(), compare.getNickname());

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue()
        );
    }

    @Test
    @DisplayName("로그인 아이디에 해당하는 사용자가 존재하는지 확인한다")
    void existsByLoginId() {
        // given
        final String same = member.getLoginId();
        final String diff = member.getLoginId() + "fake";

        // when
        final boolean actual1 = memberRepository.existsByLoginId(same);
        final boolean actual2 = memberRepository.existsByLoginId(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("전화번호에 해당하는 사용자가 존재하는지 확인한다")
    void existsByPhone() {
        // given
        final String same = member.getPhone();
        final String diff = member.getPhone().replaceAll("0", "9");

        // when
        final boolean actual1 = memberRepository.existsByPhone(same);
        final boolean actual2 = memberRepository.existsByPhone(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("이메일에 해당하는 사용자가 존재하는지 확인한다")
    void existsByEmail() {
        // given
        final Email same = member.getEmail();
        final Email diff = Email.from("diff" + member.getEmailValue());

        // when
        final boolean actual1 = memberRepository.existsByEmail(same);
        final boolean actual2 = memberRepository.existsByEmail(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("비밀번호 초기화를 위한 사용자 인증을 진행한다")
    void existsByNameAndEmailAndLoginId() {
        // when
        final boolean actual1 = memberRepository.existsByNameAndEmailAndLoginId(
                member.getName(),
                member.getEmail(),
                member.getLoginId()
        );
        final boolean actual2 = memberRepository.existsByNameAndEmailAndLoginId(
                member.getName() + "diff",
                member.getEmail(),
                member.getLoginId()
        );

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
