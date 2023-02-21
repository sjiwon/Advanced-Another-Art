package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.SimpleTradedArt;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecordRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Repository Layer] -> TradedArtQueryRepository 테스트")
class TradedArtQueryRepositoryTest extends RepositoryTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointDetailRepository pointDetailRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionRecordRepository auctionRecordRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Test
    @DisplayName("사용자가 판매한 경매 작품을 조회한다")
    void test1() {
        // given
        Member owner = createMemberA();
        Member buyer = createMemberB();

        Art auctionArtA = createAuctionArtA(owner);
        Auction auctionA = initAuction(auctionArtA);
        processBid(auctionA, buyer, auctionA.getBidAmount());
        terminateAuction(auctionA);
        processPurchase(buyer, auctionArtA, auctionA.getBidAmount());

        Art auctionArtC = createAuctionArtC(owner);
        Auction auctionC = initAuction(auctionArtC);
        processBid(auctionC, buyer, auctionC.getBidAmount());
        terminateAuction(auctionC);
        processPurchase(buyer, auctionArtC, auctionC.getBidAmount());

        Art generalArt = createGeneralArt(owner);
        processPurchase(buyer, generalArt, generalArt.getPrice());

        // when - then
        List<SimpleTradedArt> simpleTradedArtList = artRepository.findSoldAuctionArtListByMemberId(owner.getId());
        assertAll(
                () -> assertThat(simpleTradedArtList.size()).isEqualTo(2),
                () -> assertThat(simpleTradedArtList.get(0).getArtId()).isEqualTo(auctionArtA.getId()),
                () -> assertThat(simpleTradedArtList.get(0).getBuyerId()).isEqualTo(buyer.getId()),
                () -> assertThat(simpleTradedArtList.get(0).getPurchasePrice()).isEqualTo(auctionA.getBidAmount()),
                () -> assertThat(simpleTradedArtList.get(1).getArtId()).isEqualTo(auctionArtC.getId()),
                () -> assertThat(simpleTradedArtList.get(1).getBuyerId()).isEqualTo(buyer.getId()),
                () -> assertThat(simpleTradedArtList.get(1).getPurchasePrice()).isEqualTo(auctionC.getBidAmount())
        );
    }

    @Test
    @DisplayName("사용자가 판매한 일반 작품을 조회한다")
    void test2() {
        // given
        Member owner = createMemberA();
        Member buyer = createMemberB();

        Art auctionArtA = createAuctionArtA(owner);
        Auction auctionA = initAuction(auctionArtA);
        processBid(auctionA, buyer, auctionA.getBidAmount());
        terminateAuction(auctionA);
        processPurchase(buyer, auctionArtA, auctionA.getBidAmount());

        Art auctionArtC = createAuctionArtC(owner);
        Auction auctionC = initAuction(auctionArtC);
        processBid(auctionC, buyer, auctionC.getBidAmount());
        terminateAuction(auctionC);
        processPurchase(buyer, auctionArtC, auctionC.getBidAmount());

        Art generalArt = createGeneralArt(owner);
        processPurchase(buyer, generalArt, generalArt.getPrice());

        // when - then
        List<SimpleTradedArt> simpleTradedArtList = artRepository.findSoldGeneralArtListByMemberId(owner.getId());
        assertAll(
                () -> assertThat(simpleTradedArtList.size()).isEqualTo(1),
                () -> assertThat(simpleTradedArtList.get(0).getArtId()).isEqualTo(generalArt.getId()),
                () -> assertThat(simpleTradedArtList.get(0).getBuyerId()).isEqualTo(buyer.getId()),
                () -> assertThat(simpleTradedArtList.get(0).getPurchasePrice()).isEqualTo(generalArt.getPrice())
        );
    }

    @Test
    @DisplayName("사용자가 구매한 경매 작품을 조회한다")
    void test3() {
        // given
        Member owner = createMemberA();
        Member buyerB = createMemberB();
        Member buyerC = createMemberC();

        Art auctionArtA = createAuctionArtA(owner);
        Auction auctionA = initAuction(auctionArtA);
        processBid(auctionA, buyerB, auctionA.getBidAmount());
        terminateAuction(auctionA);
        processPurchase(buyerB, auctionArtA, auctionA.getBidAmount());

        Art auctionArtC = createAuctionArtC(owner);
        Auction auctionC = initAuction(auctionArtC);
        processBid(auctionC, buyerC, auctionC.getBidAmount());
        terminateAuction(auctionC);
        processPurchase(buyerC, auctionArtC, auctionC.getBidAmount());

        Art generalArt = createGeneralArt(owner);
        processPurchase(buyerB, generalArt, generalArt.getPrice());

        // when - then
        List<SimpleTradedArt> simpleTradedArtList1 = artRepository.findPurchaseAuctionArtListByMemberId(buyerB.getId());
        assertAll(
                () -> assertThat(simpleTradedArtList1.size()).isEqualTo(1),
                () -> assertThat(simpleTradedArtList1.get(0).getArtId()).isEqualTo(auctionArtA.getId()),
                () -> assertThat(simpleTradedArtList1.get(0).getBuyerId()).isEqualTo(buyerB.getId()),
                () -> assertThat(simpleTradedArtList1.get(0).getPurchasePrice()).isEqualTo(auctionA.getBidAmount())
        );

        List<SimpleTradedArt> simpleTradedArtList2 = artRepository.findPurchaseAuctionArtListByMemberId(buyerC.getId());
        assertAll(
                () -> assertThat(simpleTradedArtList2.size()).isEqualTo(1),
                () -> assertThat(simpleTradedArtList2.get(0).getArtId()).isEqualTo(auctionArtC.getId()),
                () -> assertThat(simpleTradedArtList2.get(0).getBuyerId()).isEqualTo(buyerC.getId()),
                () -> assertThat(simpleTradedArtList2.get(0).getPurchasePrice()).isEqualTo(auctionC.getBidAmount())
        );
    }

    private void processBid(Auction auction, Member bidder, int bidAmount) {
        auction.applyNewBid(bidder, bidAmount);
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, bidder, bidAmount));
    }

    private void terminateAuction(Auction auction) {
        em.createQuery("UPDATE Auction a" +
                        " SET a.period.startDate = :startDate, a.period.endDate = :endDate" +
                        " WHERE a.id = :id")
                .setParameter("startDate", currentTime3DayAgo)
                .setParameter("endDate", currentTime1DayAgo)
                .setParameter("id", auction.getId())
                .executeUpdate();
    }

    private void processPurchase(Member buyer, Art art, int purchasePrice) {
        purchaseRepository.save(Purchase.purchaseArt(buyer, art, purchasePrice));
    }

    private Member createMemberA() {
        Member member = memberRepository.save(MemberFixture.A.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }

    private Member createMemberB() {
        Member member = memberRepository.save(MemberFixture.B.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }

    private Member createMemberC() {
        Member member = memberRepository.save(MemberFixture.C.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }

    private Art createAuctionArtA(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Art createAuctionArtC(Member owner) {
        return artRepository.save(ArtFixture.C.toArt(owner, HASHTAGS));
    }

    private Art createGeneralArt(Member owner) {
        return artRepository.save(ArtFixture.B.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayAgo, currentTime1DayLater)));
    }
}