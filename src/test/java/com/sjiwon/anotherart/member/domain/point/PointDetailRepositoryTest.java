package com.sjiwon.anotherart.member.domain.point;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PointDetailRepositoryTest extends RepositoryTest {
    @Autowired
    private PointDetailRepository pointDetailRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    private static final int CHARGE_AMOUNT = 100_000_000;
    private static final int REFUND_AMOUNT = 500_000;

    @Test
    @DisplayName("사용자의 포인트 내역을 조회한다")
    void test1() {
        // 1. 회원가입
        Member member = createMemberA();

        List<PointDetail> pointDetails1 = pointDetailRepository.findByMemberId(member.getId());
        assertAll(
                () -> assertThat(pointDetails1.size()).isEqualTo(1),
                // 회원가입
                () -> assertThat(pointDetails1.get(0).getPointType()).isEqualTo(PointType.JOIN),
                () -> assertThat(pointDetails1.get(0).getAmount()).isEqualTo(0),
                () -> assertThat(pointDetails1.get(0).getMember().getId()).isEqualTo(member.getId()),
                // 최종 사용자 포인트 현황
                () -> assertThat(member.getAvailablePoint()).isEqualTo(0),
                () -> assertThat(member.getTotalPoints()).isEqualTo(0)
        );

        // 2. 포인트 충전
        doCharge(member, CHARGE_AMOUNT);

        List<PointDetail> pointDetails2 = pointDetailRepository.findByMemberId(member.getId());
        assertAll(
                () -> assertThat(pointDetails2.size()).isEqualTo(2),
                // 회원가입
                () -> assertThat(pointDetails2.get(0).getPointType()).isEqualTo(PointType.JOIN),
                () -> assertThat(pointDetails2.get(0).getAmount()).isEqualTo(0),
                () -> assertThat(pointDetails2.get(0).getMember().getId()).isEqualTo(member.getId()),
                // 포인트 충전
                () -> assertThat(pointDetails2.get(1).getPointType()).isEqualTo(PointType.CHARGE),
                () -> assertThat(pointDetails2.get(1).getAmount()).isEqualTo(CHARGE_AMOUNT),
                () -> assertThat(pointDetails2.get(1).getMember().getId()).isEqualTo(member.getId()),
                // 최종 사용자 포인트 현황
                () -> assertThat(member.getAvailablePoint()).isEqualTo(CHARGE_AMOUNT),
                () -> assertThat(member.getTotalPoints()).isEqualTo(CHARGE_AMOUNT)
        );

        // 3. 작품 구매
        Member owner = createMemberB();
        Art art = createArt(owner);
        doPurchase(art, owner, member);

        // 판매자 포인트 내역
        List<PointDetail> pointDetails3 = pointDetailRepository.findByMemberId(owner.getId());
        assertAll(
                () -> assertThat(pointDetails3.size()).isEqualTo(2),
                // 회원가입
                () -> assertThat(pointDetails3.get(0).getPointType()).isEqualTo(PointType.JOIN),
                () -> assertThat(pointDetails3.get(0).getAmount()).isEqualTo(0),
                () -> assertThat(pointDetails3.get(0).getMember().getId()).isEqualTo(owner.getId()),
                // 작품 판매
                () -> assertThat(pointDetails3.get(1).getPointType()).isEqualTo(PointType.SOLD),
                () -> assertThat(pointDetails3.get(1).getAmount()).isEqualTo(art.getPrice()),
                () -> assertThat(pointDetails3.get(1).getMember().getId()).isEqualTo(owner.getId()),
                // 최종 사용자 포인트 현황
                () -> assertThat(owner.getAvailablePoint()).isEqualTo(art.getPrice()),
                () -> assertThat(owner.getTotalPoints()).isEqualTo(art.getPrice())
        );

        // 구매자 포인트 내역
        List<PointDetail> pointDetails4 = pointDetailRepository.findByMemberId(member.getId());
        assertAll(
                () -> assertThat(pointDetails4.size()).isEqualTo(3),
                // 회원가입
                () -> assertThat(pointDetails4.get(0).getPointType()).isEqualTo(PointType.JOIN),
                () -> assertThat(pointDetails4.get(0).getAmount()).isEqualTo(0),
                () -> assertThat(pointDetails4.get(0).getMember().getId()).isEqualTo(member.getId()),
                // 포인트 충전
                () -> assertThat(pointDetails4.get(1).getPointType()).isEqualTo(PointType.CHARGE),
                () -> assertThat(pointDetails4.get(1).getAmount()).isEqualTo(CHARGE_AMOUNT),
                () -> assertThat(pointDetails4.get(1).getMember().getId()).isEqualTo(member.getId()),
                // 작품 구매
                () -> assertThat(pointDetails4.get(2).getPointType()).isEqualTo(PointType.PURCHASE),
                () -> assertThat(pointDetails4.get(2).getAmount()).isEqualTo(art.getPrice()),
                () -> assertThat(pointDetails4.get(2).getMember().getId()).isEqualTo(member.getId()),
                // 최종 사용자 포인트 현황
                () -> assertThat(member.getAvailablePoint()).isEqualTo(CHARGE_AMOUNT - art.getPrice()),
                () -> assertThat(member.getTotalPoints()).isEqualTo(CHARGE_AMOUNT - art.getPrice())
        );

        // 4. 포인트 환불
        doRefund(member, REFUND_AMOUNT);

        List<PointDetail> pointDetails5 = pointDetailRepository.findByMemberId(member.getId());
        assertAll(
                () -> assertThat(pointDetails5.size()).isEqualTo(4),
                // 회원가입
                () -> assertThat(pointDetails5.get(0).getPointType()).isEqualTo(PointType.JOIN),
                () -> assertThat(pointDetails5.get(0).getAmount()).isEqualTo(0),
                () -> assertThat(pointDetails5.get(0).getMember().getId()).isEqualTo(member.getId()),
                // 포인트 충전
                () -> assertThat(pointDetails5.get(1).getPointType()).isEqualTo(PointType.CHARGE),
                () -> assertThat(pointDetails5.get(1).getAmount()).isEqualTo(CHARGE_AMOUNT),
                () -> assertThat(pointDetails5.get(1).getMember().getId()).isEqualTo(member.getId()),
                // 작품 구매
                () -> assertThat(pointDetails5.get(2).getPointType()).isEqualTo(PointType.PURCHASE),
                () -> assertThat(pointDetails5.get(2).getAmount()).isEqualTo(art.getPrice()),
                () -> assertThat(pointDetails5.get(2).getMember().getId()).isEqualTo(member.getId()),
                // 포인트 환불
                () -> assertThat(pointDetails5.get(3).getPointType()).isEqualTo(PointType.REFUND),
                () -> assertThat(pointDetails5.get(3).getAmount()).isEqualTo(REFUND_AMOUNT),
                () -> assertThat(pointDetails5.get(3).getMember().getId()).isEqualTo(member.getId()),
                // 최종 사용자 포인트 현황
                () -> assertThat(member.getAvailablePoint()).isEqualTo(CHARGE_AMOUNT - art.getPrice() - REFUND_AMOUNT),
                () -> assertThat(member.getTotalPoints()).isEqualTo(CHARGE_AMOUNT - art.getPrice() - REFUND_AMOUNT)
        );
    }

    private void doCharge(Member member, int amount) {
        member.addPointDetail(PointDetail.insertPointDetail(member, PointType.CHARGE, amount));
    }

    private void doPurchase(Art art, Member owner, Member buyer) {
        art.changeArtStatus(ArtStatus.SOLD_OUT);
        owner.addPointDetail(PointDetail.insertPointDetail(owner, PointType.SOLD, art.getPrice()));
        buyer.addPointDetail(PointDetail.insertPointDetail(buyer, PointType.PURCHASE, art.getPrice()));
    }

    private void doRefund(Member member, int amount) {
        member.addPointDetail(PointDetail.insertPointDetail(member, PointType.REFUND, amount));
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember());
    }

    private Art createArt(Member owner) {
        return artRepository.save(ArtFixture.B.toArt(owner, HASHTAGS));
    }
}