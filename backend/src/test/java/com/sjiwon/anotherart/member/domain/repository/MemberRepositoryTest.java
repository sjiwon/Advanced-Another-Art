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
        sut.save(MEMBER_A.toDomain());

        // when
        final Optional<Member> actual1 = sut.findByLoginId(MEMBER_A.getLoginId());
        final Optional<Member> actual2 = sut.findByLoginId(MEMBER_B.getLoginId());

        // then
        assertAll(
                () -> assertThat(actual1).isPresent(),
                () -> assertThat(actual2).isEmpty()
        );
    }

    @Test
    @DisplayName("이름 + 이메일로 사용자를 조회한다")
    void findByNameAndEmail() {
        // given
        sut.save(MEMBER_A.toDomain());
        sut.save(MEMBER_B.toDomain());

        // when
        final Optional<Member> actual1 = sut.findByNameAndEmail(MEMBER_A.getName(), MEMBER_A.getEmail().getValue());
        final Optional<Member> actual2 = sut.findByNameAndEmail(MEMBER_A.getName(), MEMBER_B.getEmail().getValue());
        final Optional<Member> actual3 = sut.findByNameAndEmail(MEMBER_B.getName(), MEMBER_B.getEmail().getValue());
        final Optional<Member> actual4 = sut.findByNameAndEmail(MEMBER_B.getName(), MEMBER_A.getEmail().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isPresent(),
                () -> assertThat(actual2).isEmpty(),
                () -> assertThat(actual3).isPresent(),
                () -> assertThat(actual4).isEmpty()
        );
    }

    @Test
    @DisplayName("이름 + 이메일 + 로그인 아이디로 사용자를 조회한다")
    void findByNameAndEmailAndLoginId() {
        // given
        sut.save(MEMBER_A.toDomain());

        // when
        final Optional<Member> actual1 = sut.findByNameAndEmailAndLoginId(MEMBER_A.getName(), MEMBER_A.getEmail().getValue(), MEMBER_A.getLoginId());
        final Optional<Member> actual2 = sut.findByNameAndEmailAndLoginId(MEMBER_A.getName(), MEMBER_A.getEmail().getValue(), MEMBER_B.getLoginId());
        final Optional<Member> actual3 = sut.findByNameAndEmailAndLoginId(MEMBER_A.getName(), MEMBER_B.getEmail().getValue(), MEMBER_A.getLoginId());
        final Optional<Member> actual4 = sut.findByNameAndEmailAndLoginId(MEMBER_A.getName(), MEMBER_B.getEmail().getValue(), MEMBER_B.getLoginId());
        final Optional<Member> actual5 = sut.findByNameAndEmailAndLoginId(MEMBER_B.getName(), MEMBER_B.getEmail().getValue(), MEMBER_A.getLoginId());
        final Optional<Member> actual6 = sut.findByNameAndEmailAndLoginId(MEMBER_B.getName(), MEMBER_A.getEmail().getValue(), MEMBER_B.getLoginId());
        final Optional<Member> actual7 = sut.findByNameAndEmailAndLoginId(MEMBER_B.getName(), MEMBER_B.getEmail().getValue(), MEMBER_B.getLoginId());

        // then
        assertAll(
                () -> assertThat(actual1).isPresent(),
                () -> assertThat(actual2).isEmpty(),
                () -> assertThat(actual3).isEmpty(),
                () -> assertThat(actual4).isEmpty(),
                () -> assertThat(actual5).isEmpty(),
                () -> assertThat(actual6).isEmpty(),
                () -> assertThat(actual7).isEmpty()
        );
    }

    @Test
    @DisplayName("닉네임에 해당하는 사용자 ID를 조회한다")
    void findIdByNickname() {
        // given
        final Member memberA = sut.save(MEMBER_A.toDomain());
        final Member memberB = sut.save(MEMBER_B.toDomain());

        // when
        final Long actual1 = sut.findIdByNickname(memberA.getNickname().getValue());
        final Long actual2 = sut.findIdByNickname(memberB.getNickname().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isEqualTo(memberA.getId()),
                () -> assertThat(actual2).isEqualTo(memberB.getId())
        );
    }

    @Test
    @DisplayName("닉네임에 해당하는 사용자가 존재하는지 확인한다")
    void existsByNicknameValue() {
        // given
        sut.save(MEMBER_A.toDomain());

        // when
        final boolean actual1 = sut.existsByNicknameValue(MEMBER_A.getNickname().getValue());
        final boolean actual2 = sut.existsByNicknameValue(MEMBER_B.getNickname().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("로그인 아이디에 해당하는 사용자가 존재하는지 확인한다")
    void existsByLoginId() {
        // given
        sut.save(MEMBER_A.toDomain());

        // when
        final boolean actual1 = sut.existsByLoginId(MEMBER_A.getLoginId());
        final boolean actual2 = sut.existsByLoginId(MEMBER_B.getLoginId());

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
        sut.save(MEMBER_A.toDomain());

        // when
        final boolean actual1 = sut.existsByPhoneValue(MEMBER_A.getPhone().getValue());
        final boolean actual2 = sut.existsByPhoneValue(MEMBER_B.getPhone().getValue());

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
        sut.save(MEMBER_A.toDomain());

        // when
        final boolean actual1 = sut.existsByEmailValue(MEMBER_A.getEmail().getValue());
        final boolean actual2 = sut.existsByEmailValue(MEMBER_B.getEmail().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
