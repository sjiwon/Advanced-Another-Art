package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.dto.response.MemberInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberPointService 테스트")
class MemberInformationServiceTest extends ServiceTest {
    @Autowired
    private MemberInformationService memberInformationService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
    }

    @Test
    @DisplayName("사용자의 기본 정보를 조회한다")
    void getInformation() {
        // when
        MemberInformation information = memberInformationService.getInformation(member.getId());

        // then
        assertAll(
                () -> assertThat(information.id()).isEqualTo(member.getId()),
                () -> assertThat(information.name()).isEqualTo(member.getName()),
                () -> assertThat(information.nickname()).isEqualTo(member.getNicknameValue()),
                () -> assertThat(information.loginId()).isEqualTo(member.getLoginId()),
                () -> assertThat(information.school()).isEqualTo(member.getSchool()),
                () -> assertThat(information.phone()).isEqualTo(member.getPhone()),
                () -> assertThat(information.email()).isEqualTo(member.getEmailValue()),
                () -> assertThat(information.address().getPostcode()).isEqualTo(member.getAddress().getPostcode()),
                () -> assertThat(information.address().getDefaultAddress()).isEqualTo(member.getAddress().getDefaultAddress()),
                () -> assertThat(information.address().getDetailAddress()).isEqualTo(member.getAddress().getDetailAddress()),
                () -> assertThat(information.totalPoint()).isEqualTo(member.getTotalPoint()),
                () -> assertThat(information.availablePoint()).isEqualTo(member.getAvailablePoint())
        );
    }
}
