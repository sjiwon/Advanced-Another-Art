package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Member [Repository Layer] -> MemberRepository 테스트")
class MemberRepositoryTest extends RepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

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

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember(PASSWORD_ENCODER));
    }

    private void synchronizePersistenceContext() {
        em.flush();
        em.clear();
    }
}