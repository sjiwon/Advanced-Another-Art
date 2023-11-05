package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member [Service Layer] -> MemberFindService 테스트")
class MemberFindServiceTest extends ServiceTest {
    @Autowired
    private MemberFindService memberFindService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
    }

    @Test
    @DisplayName("ID(PK)로 사용자를 조회한다")
    void findById() {
        // when
        assertThatThrownBy(() -> memberFindService.findById(member.getId() + 10000L))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

        final Member findMember = memberFindService.findById(member.getId());

        // then
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @DisplayName("로그인 아이디로 사용자를 조회한다")
    void findByLoginId() {
        // when
        assertThatThrownBy(() -> memberFindService.findByLoginId(member.getLoginId() + "fake"))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

        final Member findMember = memberFindService.findByLoginId(member.getLoginId());

        // then
        assertThat(findMember).isEqualTo(member);
    }
}
