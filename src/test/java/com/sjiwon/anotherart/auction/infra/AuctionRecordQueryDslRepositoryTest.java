package com.sjiwon.anotherart.auction.infra;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuctionRecord [Repository Layer] -> AuctionRecordQueryDslRepository 테스트")
class AuctionRecordQueryDslRepositoryTest extends RepositoryTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private AuctionRecordRepository auctionRecordRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointDetailRepository pointDetailRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    private static final int AVAILABLE_POINT = 100_000_000;
    private static final LocalDateTime currentTime1DayLater = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime currentTime3DayLater = LocalDateTime.now().plusDays(3);
    private static final List<String> HASHTAGS = List.of("A", "B", "C", "D", "E");

    @Test
    @DisplayName("작품의 경매 기록이 존재하는지 조회한다")
    void test() {
        // given
        Member owner = createMemberA();
        Art auctionArt = createAuctionArt(owner);

        // when - then
        boolean actual1 = auctionRecordRepository.existsAuctionRecordByArtId(auctionArt.getId());
        assertThat(actual1).isFalse();

        // 입찰 1회 진행
        Member bidder = createMemberB();
        Auction auction = auctionRepository.findByArtId(auctionArt.getId()).orElseThrow();

        final int BID_AMOUNT = 50_000_000;
        processBid(auction, bidder, BID_AMOUNT);

        boolean actual2 = auctionRecordRepository.existsAuctionRecordByArtId(auctionArt.getId());
        assertThat(actual2).isTrue();
        assertThat(bidder.getAvailablePoint()).isEqualTo(AVAILABLE_POINT - BID_AMOUNT);
        assertThat(bidder.getTotalPoints()).isEqualTo(AVAILABLE_POINT);
    }

    private void processBid(Auction auction, Member bidder, int bidAmount) {
        auction.applyNewBid(bidder, bidAmount);
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, bidder, bidAmount));
    }

    private Member createMemberA() {
        Member member = memberRepository.save(MemberFixture.A.toMember());
        pointDetailRepository.save(PointDetail.createPointDetail(member));
        return member;
    }

    private Member createMemberB() {
        Member member = memberRepository.save(MemberFixture.B.toMember());
        pointDetailRepository.save(PointDetail.createPointDetail(member));
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, AVAILABLE_POINT));
        return member;
    }

    private Art createAuctionArt(Member owner) {
        Art art = artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
        auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayLater, currentTime3DayLater)));
        return art;
    }

    private void sync() {
        em.flush();
        em.clear();
    }
}