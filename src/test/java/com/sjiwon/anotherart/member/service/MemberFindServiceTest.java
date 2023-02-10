package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member [Service Layer] -> MemberFindService 테스트")
@RequiredArgsConstructor
class MemberFindServiceTest extends ServiceIntegrateTest {
    private final MemberFindService memberFindService;

    @Test
    @DisplayName("ID(PK)로 사용자 조회하기")
    void test1() {
        // given
        Member member = createMember();

        // when
        assertThatThrownBy(() -> memberFindService.findById(member.getId() + 10000L))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        Member findMember = memberFindService.findById(member.getId());

        // then
        assertThat(findMember.getName()).isEqualTo(member.getName());
        assertThat(findMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(findMember.getLoginId()).isEqualTo(member.getLoginId());
    }

    @Test
    @DisplayName("로그인 아이디로 사용자 조회하기")
    void test2() {
        // given
        Member member = createMember();

        // when
        assertThatThrownBy(() -> memberFindService.findByLoginId(member.getLoginId() + "fake"))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        Member findMember = memberFindService.findByLoginId(member.getLoginId());

        // then
        assertThat(findMember.getName()).isEqualTo(member.getName());
        assertThat(findMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(findMember.getLoginId()).isEqualTo(member.getLoginId());
    }

    @Test
    @DisplayName("이름, 이메일로 사용자 조회하기")
    void test3() {
        // given
        Member member = createMember();

        // when
        assertThatThrownBy(() -> memberFindService.findByNameAndEmail(member.getName() + "fake", member.getEmail()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        Member findMember = memberFindService.findByNameAndEmail(member.getName(), member.getEmail());

        // then
        assertThat(findMember.getName()).isEqualTo(member.getName());
        assertThat(findMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(findMember.getLoginId()).isEqualTo(member.getLoginId());
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }
}