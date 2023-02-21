package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.service.dto.response.UserTradedArt;
import com.sjiwon.anotherart.member.service.dto.response.UserWinningAuction;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberProfileWithArtService 테스트")
@RequiredArgsConstructor
class MemberProfileWithArtServiceTest extends ServiceIntegrateTest {
    private final MemberProfileWithArtService memberProfileWithArtService;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("사용자의 낙찰된 경매 작품들을 조회한다")
    void test1() {
        // given
        Member owner = createMemberA();
        Member bidder = createMemberB();

        // 경매 작품 A 낙찰
        Art auctionArtA = createAuctionArtA(owner);
        Auction auctionA = initAuction(auctionArtA);
        processBid(auctionA, bidder, auctionA.getBidAmount());
        terminateAuction(auctionA);

        List<UserWinningAuction> userWinningAuctionList1 = memberProfileWithArtService.getWinningAuction(bidder.getId());
        assertAll(
                () -> assertThat(userWinningAuctionList1.size()).isEqualTo(1),
                () -> assertThat(userWinningAuctionList1.get(0).getArt().getArtId()).isEqualTo(auctionArtA.getId()),
                () -> assertThat(userWinningAuctionList1.get(0).getArt().getHighestBidPrice()).isEqualTo(auctionA.getBidAmount()),
                () -> assertThat(userWinningAuctionList1.get(0).getArt().getHighestBidderId()).isEqualTo(bidder.getId()),
                () -> assertThat(userWinningAuctionList1.get(0).getArt().getHighestBidderNickname()).isEqualTo(bidder.getNickname()),
                () -> assertThat(userWinningAuctionList1.get(0).getArt().getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(userWinningAuctionList1.get(0).getArt().getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(userWinningAuctionList1.get(0).getHashtags()).containsAll(HASHTAGS)
        );

        // 경매 작품 C 낙찰
        Art auctionArtC = createAuctionArtC(owner);
        Auction auctionC = initAuction(auctionArtC);
        processBid(auctionC, bidder, auctionC.getBidAmount());
        terminateAuction(auctionC);

        List<UserWinningAuction> userWinningAuctionList2 = memberProfileWithArtService.getWinningAuction(bidder.getId());
        assertAll(
                () -> assertThat(userWinningAuctionList2.size()).isEqualTo(2),
                () -> assertThat(userWinningAuctionList2.get(0).getArt().getArtId()).isEqualTo(auctionArtA.getId()),
                () -> assertThat(userWinningAuctionList2.get(0).getArt().getHighestBidPrice()).isEqualTo(auctionA.getBidAmount()),
                () -> assertThat(userWinningAuctionList2.get(0).getArt().getHighestBidderId()).isEqualTo(bidder.getId()),
                () -> assertThat(userWinningAuctionList2.get(0).getArt().getHighestBidderNickname()).isEqualTo(bidder.getNickname()),
                () -> assertThat(userWinningAuctionList2.get(0).getArt().getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(userWinningAuctionList2.get(0).getArt().getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(userWinningAuctionList2.get(0).getHashtags()).containsAll(HASHTAGS),
                () -> assertThat(userWinningAuctionList2.get(1).getArt().getArtId()).isEqualTo(auctionArtC.getId()),
                () -> assertThat(userWinningAuctionList2.get(1).getArt().getHighestBidPrice()).isEqualTo(auctionC.getBidAmount()),
                () -> assertThat(userWinningAuctionList2.get(1).getArt().getHighestBidderId()).isEqualTo(bidder.getId()),
                () -> assertThat(userWinningAuctionList2.get(1).getArt().getHighestBidderNickname()).isEqualTo(bidder.getNickname()),
                () -> assertThat(userWinningAuctionList2.get(1).getArt().getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(userWinningAuctionList2.get(1).getArt().getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(userWinningAuctionList2.get(1).getHashtags()).containsAll(HASHTAGS)
        );
    }
    
    @Test
    @DisplayName("사용자가 판매한 경매 작품들을 조회한다")
    void test2() {
        // given
        Member owner = createMemberA();
        Member buyer = createMemberB();

        Art auctionArtA = createAuctionArtA(owner);
        Auction auctionA = initAuction(auctionArtA);
        processBid(auctionA, buyer, auctionA.getBidAmount());
        terminateAuction(auctionA);

        Art auctionArtC = createAuctionArtC(owner);
        Auction auctionC = initAuction(auctionArtC);
        processBid(auctionC, buyer, auctionC.getBidAmount());
        terminateAuction(auctionC);

        // 구매 진행
        processPurchase(buyer, auctionArtA, auctionA.getBidAmount());
        processPurchase(buyer, auctionArtC, auctionC.getBidAmount());

        // when - then
        List<UserTradedArt> soldAuctionArtList = memberProfileWithArtService.getSoldAuctionArt(owner.getId());
        assertAll(
                () -> assertThat(soldAuctionArtList.size()).isEqualTo(2),
                () -> assertThat(soldAuctionArtList.get(0).getArt().getArtId()).isEqualTo(auctionArtA.getId()),
                () -> assertThat(soldAuctionArtList.get(0).getArt().getBuyerId()).isEqualTo(buyer.getId()),
                () -> assertThat(soldAuctionArtList.get(0).getArt().getPurchasePrice()).isEqualTo(auctionA.getBidAmount()),
                () -> assertThat(soldAuctionArtList.get(0).getHashtags()).containsAll(HASHTAGS),
                () -> assertThat(soldAuctionArtList.get(1).getArt().getArtId()).isEqualTo(auctionArtC.getId()),
                () -> assertThat(soldAuctionArtList.get(1).getArt().getBuyerId()).isEqualTo(buyer.getId()),
                () -> assertThat(soldAuctionArtList.get(1).getArt().getPurchasePrice()).isEqualTo(auctionC.getBidAmount()),
                () -> assertThat(soldAuctionArtList.get(1).getHashtags()).containsAll(HASHTAGS)
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

    private Art createAuctionArtA(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Art createAuctionArtC(Member owner) {
        return artRepository.save(ArtFixture.C.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayAgo, currentTime1DayLater)));
    }
}