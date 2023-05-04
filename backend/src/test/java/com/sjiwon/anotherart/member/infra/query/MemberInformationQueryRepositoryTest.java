package com.sjiwon.anotherart.member.infra.query;

import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.member.domain.point.PointType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberInformationQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
    }

    @Test
    @DisplayName("사용자의 포인트 활용 내역을 조회한다")
    void findPointRecordByMemberId() {
        // given
        member.addPointRecords(CHARGE, 100_000);
        member.addPointRecords(CHARGE, 200_000);
        member.addPointRecords(PURCHASE, 30_000);
        member.addPointRecords(PURCHASE, 50_000);
        member.addPointRecords(REFUND, 50_000);
        member.addPointRecords(SOLD, 100_000);
        member.addPointRecords(CHARGE, 500_000);

        // when
        List<MemberPointRecord> result = memberRepository.findPointRecordByMemberId(member.getId());

        // then
        assertAll(
                () -> assertThat(result).hasSize(7),
                () -> assertThat(result)
                        .map(MemberPointRecord::getPointType)
                        .containsExactly(
                                CHARGE.getDescription(),
                                SOLD.getDescription(),
                                REFUND.getDescription(),
                                PURCHASE.getDescription(),
                                PURCHASE.getDescription(),
                                CHARGE.getDescription(),
                                CHARGE.getDescription()
                        ),
                () -> assertThat(result)
                        .map(MemberPointRecord::getAmount)
                        .containsExactly(
                                500_000,
                                100_000,
                                50_000,
                                50_000,
                                30_000,
                                200_000,
                                100_000
                        )
        );
    }
}
