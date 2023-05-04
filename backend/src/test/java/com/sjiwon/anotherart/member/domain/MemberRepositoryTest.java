package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.common.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_B;
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
    void findByLoginId(){
        // when
        Optional<Member> emptyMember = memberRepository.findByLoginId("fake");
        Member actualMember = memberRepository.findByLoginId(member.getLoginId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(emptyMember).isEmpty(),
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
        boolean actual1 = memberRepository.existsByNickname(same);
        boolean actual2 = memberRepository.existsByNickname(diff);

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
        Member compare = memberRepository.save(MEMBER_B.toMember());

        // when
        boolean actual1 = memberRepository.existsByIdNotAndNickname(member.getId(), member.getNickname());
        boolean actual2 = memberRepository.existsByIdNotAndNickname(member.getId(), compare.getNickname());

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
        boolean actual1 = memberRepository.existsByLoginId(same);
        boolean actual2 = memberRepository.existsByLoginId(diff);

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
        boolean actual1 = memberRepository.existsByPhone(same);
        boolean actual2 = memberRepository.existsByPhone(diff);

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
        boolean actual1 = memberRepository.existsByEmail(same);
        boolean actual2 = memberRepository.existsByEmail(diff);

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
