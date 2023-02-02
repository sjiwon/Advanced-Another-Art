package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Member [Repository Layer] -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("닉네임으로 사용자가 존재하는지 확인한다")
    void test1() {
        // given
        Member expectedMember = createMemberA();
        synchronizePersistenceContext();

        // when
        boolean actual1 = memberRepository.existsByNickname(expectedMember.getNickname());
        boolean actual2 = memberRepository.existsByNickname("Fake Nickname");

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    @Test
    @DisplayName("로그인 아이디로 사용자가 존재하는지 확인한다")
    void test2() {
        // given
        Member expectedMember = createMemberA();
        synchronizePersistenceContext();

        // when
        boolean actual1 = memberRepository.existsByLoginId(expectedMember.getLoginId());
        boolean actual2 = memberRepository.existsByLoginId("Fake LoginId");

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    @Test
    @DisplayName("전화번호로 사용자가 존재하는지 확인한다")
    void test3() {
        // given
        Member expectedMember = createMemberA();
        synchronizePersistenceContext();

        // when
        boolean actual1 = memberRepository.existsByPhone(expectedMember.getPhone());
        boolean actual2 = memberRepository.existsByPhone("99988887777");

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    @Test
    @DisplayName("이메일로 사용자가 존재하는지 확인한다")
    void test4() {
        // given
        Member expectedMember = createMemberA();
        synchronizePersistenceContext();

        // when
        boolean actual1 = memberRepository.existsByEmail(expectedMember.getEmail());
        boolean actual2 = memberRepository.existsByEmail(Email.from("fake@gmail.com"));

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    @Test
    @DisplayName("로그인 아이디로 사용자를 조회한다")
    void test5(){
        // given
        final Member expectedMember = createMemberA();
        final String loginId = expectedMember.getLoginId();
        synchronizePersistenceContext();

        // when
        Optional<Member> actualMember = memberRepository.findByLoginId(loginId);
        Optional<Member> emptyMember = memberRepository.findByLoginId("fakeLoginId");

        // then
        assertThat(emptyMember).isEmpty();
        assertThat(actualMember).isPresent();
        assertThat(actualMember.get().getId()).isEqualTo(expectedMember.getId());
        assertThat(actualMember.get().getName()).isEqualTo(expectedMember.getName());
        assertThat(actualMember.get().getNickname()).isEqualTo(expectedMember.getNickname());
    }

    @Test
    @DisplayName("이름, 이메일로 사용자를 조회한다")
    void test6() {
        // given
        final Member expectedMember = createMemberA();
        final String name = expectedMember.getName();
        final Email email = expectedMember.getEmail();
        synchronizePersistenceContext();

        // when
        Optional<Member> emptyMember1 = memberRepository.findByNameAndEmail(name + "fake", email);
        Optional<Member> emptyMember2 = memberRepository.findByNameAndEmail(name, Email.from("fake" + email.getValue()));
        Optional<Member> actualMember = memberRepository.findByNameAndEmail(name, email);

        // then
        assertThat(emptyMember1).isEmpty();
        assertThat(emptyMember2).isEmpty();
        assertThat(actualMember).isPresent();
        assertThat(actualMember.get().getId()).isEqualTo(expectedMember.getId());
        assertThat(actualMember.get().getName()).isEqualTo(expectedMember.getName());
        assertThat(actualMember.get().getNickname()).isEqualTo(expectedMember.getNickname());
    }

    @Test
    @DisplayName("이름, 로그인 아이디, 이메일에 해당하는 사용자가 있는지 확인한다")
    void test7() {
        // given
        final Member expectedMember = createMemberA();
        final String name = expectedMember.getName();
        final String loginId = expectedMember.getLoginId();
        final Email email = expectedMember.getEmail();
        synchronizePersistenceContext();

        // when
        boolean actual1 = memberRepository.existsByNameAndLoginIdAndEmail(name, loginId, email);
        boolean actual2 = memberRepository.existsByNameAndLoginIdAndEmail(name + "diff", loginId, email);
        boolean actual3 = memberRepository.existsByNameAndLoginIdAndEmail(name, loginId + "diff", email);
        boolean actual4 = memberRepository.existsByNameAndLoginIdAndEmail(name, loginId, Email.from("diff" + email.getValue()));

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
        assertThat(actual3).isFalse();
        assertThat(actual4).isFalse();
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private void synchronizePersistenceContext() {
        em.flush();
        em.clear();
    }
}