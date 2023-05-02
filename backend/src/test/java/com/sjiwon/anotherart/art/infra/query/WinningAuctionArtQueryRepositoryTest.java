package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
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

@DisplayName("Art [Repository Layer] -> WinningAuctionArtQueryRepository 테스트")
class WinningAuctionArtQueryRepositoryTest extends RepositoryTest {
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

        List<BasicAuctionArt> winningAuctionArtList1 = artRepository.findWinningAuctionArtListByMemberId(bidder.getId());
        assertAll(
                () -> assertThat(winningAuctionArtList1.size()).isEqualTo(1),
                () -> assertThat(winningAuctionArtList1.get(0).getArtId()).isEqualTo(auctionArtA.getId()),
                () -> assertThat(winningAuctionArtList1.get(0).getHighestBidPrice()).isEqualTo(auctionA.getBidAmount()),
                () -> assertThat(winningAuctionArtList1.get(0).getHighestBidderId()).isEqualTo(bidder.getId()),
                () -> assertThat(winningAuctionArtList1.get(0).getHighestBidderNickname()).isEqualTo(bidder.getNickname()),
                () -> assertThat(winningAuctionArtList1.get(0).getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(winningAuctionArtList1.get(0).getOwnerNickname()).isEqualTo(owner.getNickname())
        );

        // 경매 작품 C 낙찰
        Art auctionArtC = createAuctionArtC(owner);
        Auction auctionC = initAuction(auctionArtC);
        processBid(auctionC, bidder, auctionC.getBidAmount());
        terminateAuction(auctionC);

        List<BasicAuctionArt> winningAuctionArtList2 = artRepository.findWinningAuctionArtListByMemberId(bidder.getId());
        assertAll(
                () -> assertThat(winningAuctionArtList2.size()).isEqualTo(2),
                () -> assertThat(winningAuctionArtList2.get(0).getArtId()).isEqualTo(auctionArtA.getId()),
                () -> assertThat(winningAuctionArtList2.get(0).getHighestBidPrice()).isEqualTo(auctionA.getBidAmount()),
                () -> assertThat(winningAuctionArtList2.get(0).getHighestBidderId()).isEqualTo(bidder.getId()),
                () -> assertThat(winningAuctionArtList2.get(0).getHighestBidderNickname()).isEqualTo(bidder.getNickname()),
                () -> assertThat(winningAuctionArtList2.get(0).getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(winningAuctionArtList2.get(0).getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(winningAuctionArtList2.get(1).getArtId()).isEqualTo(auctionArtC.getId()),
                () -> assertThat(winningAuctionArtList2.get(1).getHighestBidPrice()).isEqualTo(auctionC.getBidAmount()),
                () -> assertThat(winningAuctionArtList2.get(1).getHighestBidderId()).isEqualTo(bidder.getId()),
                () -> assertThat(winningAuctionArtList2.get(1).getHighestBidderNickname()).isEqualTo(bidder.getNickname()),
                () -> assertThat(winningAuctionArtList2.get(1).getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(winningAuctionArtList2.get(1).getOwnerNickname()).isEqualTo(owner.getNickname())
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