package com.sjiwon.anotherart.purchase.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.domain.point.PointType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Purchase [Repository Layer] -> PurchaseRepository 테스트")
class PurchaseRepositoryTest extends RepositoryTest {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointDetailRepository pointDetailRepository;

    @Autowired
    private ArtRepository artRepository;

    @Test
    @DisplayName("사용자 ID(PK)를 통해서 사용자의 작품 구매 내역들을 조회한다")
    void test() {
        // given
        Member owner = createMemberA(INIT_AVAILABLE_POINT);
        Art art = createArt(owner);

        Member buyer = createMemberB(INIT_AVAILABLE_POINT);
        proceedingPurchase(art, buyer);

        // when
        List<Purchase> purchaseList = purchaseRepository.findByBuyerId(buyer.getId());

        // then
        assertAll(
                () -> assertThat(purchaseList.size()).isEqualTo(1),
                () -> assertThat(purchaseList.get(0).getBuyer()).isNotNull(),
                () -> assertThat(purchaseList.get(0).getBuyer().getId()).isEqualTo(buyer.getId()),
                () -> assertThat(purchaseList.get(0).getBuyer().getName()).isEqualTo(buyer.getName()),
                () -> assertThat(purchaseList.get(0).getArt()).isNotNull(),
                () -> assertThat(purchaseList.get(0).getArt().getId()).isEqualTo(art.getId()),
                () -> assertThat(purchaseList.get(0).getArt().getName()).isEqualTo(art.getName()),
                () -> assertThat(purchaseList.get(0).getPurchasePrice()).isEqualTo(art.getPrice())
        );
    }

    private void proceedingPurchase(Art art, Member buyer) {
        purchaseRepository.save(Purchase.purchaseArt(buyer, art, art.getPrice()));
    }

    private Member createMemberA(int chargePoint) {
        Member member = memberRepository.save(MemberFixture.A.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargePoint));
        return member;
    }

    private Member createMemberB(int chargePoint) {
        Member member = memberRepository.save(MemberFixture.B.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargePoint));
        return member;
    }

    private Art createArt(Member owner) {
        return artRepository.save(ArtFixture.B.toArt(owner, HASHTAGS));
    }
}