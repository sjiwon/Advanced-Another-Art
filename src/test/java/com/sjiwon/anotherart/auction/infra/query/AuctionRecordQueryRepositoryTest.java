package com.sjiwon.anotherart.auction.infra.query;

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

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("AuctionRecord [Repository Layer] -> AuctionRecordQueryRepository 테스트")
class AuctionRecordQueryRepositoryTest extends RepositoryTest {
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

    @Test
    @DisplayName("작품의 경매 기록이 존재하는지 조회한다")
    void test1() {
        // given
        Member owner = createMemberA();
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt);

        // when - then
        boolean actual1 = auctionRecordRepository.existsAuctionRecordByArtId(auctionArt.getId());
        assertThat(actual1).isFalse();

        // 입찰 진행
        Member bidder = createMemberB();
        final int BID_AMOUNT = 50_000_000;
        processBid(auction, bidder, BID_AMOUNT);

        boolean actual2 = auctionRecordRepository.existsAuctionRecordByArtId(auctionArt.getId());
        assertAll(
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(bidder.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - BID_AMOUNT),
                () -> assertThat(memberRepository.getTotalPointsByMemberId(bidder.getId())).isEqualTo(INIT_AVAILABLE_POINT)
        );
    }

    @Test
    @DisplayName("경매 작품의 입찰 횟수를 조회한다")
    void test2() {
        // given
        Member owner = createMemberA();
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt);

        Member memberB = createMemberB();
        Member memberC = createMemberC();

        // 입찰 1회
        processBid(auction, memberB, auction.getBidAmount() + 5_000);
        assertThat(auctionRecordRepository.getBidCountByArtId(auctionArt.getId())).isEqualTo(1);

        // 입찰 2회
        processBid(auction, memberC, auction.getBidAmount() + 5_000);
        assertThat(auctionRecordRepository.getBidCountByArtId(auctionArt.getId())).isEqualTo(2);

        // 입찰 3회
        processBid(auction, memberB, auction.getBidAmount() + 5_000);
        assertThat(auctionRecordRepository.getBidCountByArtId(auctionArt.getId())).isEqualTo(3);
    }

    private void processBid(Auction auction, Member bidder, int bidAmount) {
        auction.applyNewBid(bidder, bidAmount);
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, bidder, bidAmount));
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

    private Art createAuctionArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayAgo, currentTime1DayLater)));
    }
}
