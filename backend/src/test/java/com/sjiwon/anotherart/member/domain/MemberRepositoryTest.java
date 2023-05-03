package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.common.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
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
}
