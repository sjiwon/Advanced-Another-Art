package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction [Service Layer] -> AuctionFindService 테스트")
@RequiredArgsConstructor
class BidServiceTest extends ServiceIntegrateTest {
    private final BidService bidService;
    private static final int NOT_ENOUGH_POINT = 10;
    private static final int ENOUGH_POINT = INIT_AVAILABLE_POINT;

    @Test
    @DisplayName("아직 시작하지 않았거나 이미 종료된 경매에 대해서 입찰을 진행하면 예외가 발생한다")
    void test1() {
        // given
        Member owner = createMemberA(ENOUGH_POINT);
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime1DayAgo); // 이미 종료

        Member bidder = createMemberB(ENOUGH_POINT);
        final int bidAmount = auction.getBidAmount() + 5_000;

        // when - then
        assertThatThrownBy(() -> bidService.bid(auction.getId(), bidder.getId(), bidAmount))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.AUCTION_NOT_START_OR_ALREADY_FINISHED.getMessage());
    }
    
    @Test
    @DisplayName("경매 작품 소유자는 본인 작품에 입찰할 수 없고 예외가 발생한다")
    void test2() {
        // given
        Member owner = createMemberA(ENOUGH_POINT);
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

        final int bidAmount = auction.getBidAmount() + 5_000;

        // when - then
        assertThatThrownBy(() -> bidService.bid(auction.getId(), owner.getId(), bidAmount))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.INVALID_OWNER_BID.getMessage());
    }

    @Test
    @DisplayName("입찰 금액이 부족해서 예외가 발생한다")
    void test3() {
        // given
        Member owner = createMemberA(ENOUGH_POINT);
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

        Member bidder = createMemberB(ENOUGH_POINT);
        final int bidAmount = auction.getBidAmount();

        // when - then
        assertThatThrownBy(() -> bidService.bid(auction.getId(), bidder.getId(), bidAmount))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.INVALID_BID_PRICE.getMessage());
    }

    @Test
    @DisplayName("최고 입찰자는 연속으로 입찰을 진행할 수 없고 예외가 발생한다")
    void test4() {
        // given
        Member owner = createMemberA(ENOUGH_POINT);
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

        Member bidder = createMemberB(ENOUGH_POINT);
        final int bidAmount = auction.getBidAmount() + 5_000;
        proceedingBid(auction, bidder, bidAmount);

        // when - then
        assertThatThrownBy(() -> bidService.bid(auction.getId(), bidder.getId(), bidAmount + 5_000))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.INVALID_DUPLICATE_BID.getMessage());
    }

    @Test
    @DisplayName("입찰에 성공한다 - 1회")
    void test5() {
        // given
        Member owner = createMemberA(ENOUGH_POINT);
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

        // when
        Member bidder = createMemberB(ENOUGH_POINT);
        final int bidAmount = auction.getBidAmount() + 5_000;
        bidService.bid(auction.getId(), bidder.getId(), bidAmount);

        // then
        List<AuctionRecord> records = auctionRecordRepository.findByAuctionId(auction.getId());
        assertAll(
                // 입찰 기록
                () -> assertThat(records.size()).isEqualTo(1),
                () -> assertThat(records.get(0).getBidder().getId()).isEqualTo(bidder.getId()),
                () -> assertThat(records.get(0).getBidAmount()).isEqualTo(bidAmount),
                // 경매 정보
                () -> assertThat(auction.getBidder()).isNotNull(),
                () -> assertThat(auction.getBidder().getId()).isEqualTo(bidder.getId()),
                () -> assertThat(auction.getBidAmount()).isEqualTo(bidAmount),
                // 현재 최고 입찰자 포인트
                () -> assertThat(bidder.getAvailablePoint()).isEqualTo(ENOUGH_POINT - bidAmount),
                () -> assertThat(memberRepository.getTotalPointsByMemberId(bidder.getId())).isEqualTo(ENOUGH_POINT)
        );
    }

    @Test
    @DisplayName("입찰에 성공한다 - 2회")
    void test6() {
        // given
        Member owner = createMemberA(ENOUGH_POINT);
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

        // when - then
        // 입찰 1회
        Member bidder1 = createMemberB(ENOUGH_POINT);
        final int bidAmount1 = auction.getBidAmount() + 5_000;
        bidService.bid(auction.getId(), bidder1.getId(), bidAmount1);

        List<AuctionRecord> records1 = auctionRecordRepository.findByAuctionId(auction.getId());
        assertAll(
                // 입찰 기록
                () -> assertThat(records1.size()).isEqualTo(1),
                () -> assertThat(records1.get(0).getBidder().getId()).isEqualTo(bidder1.getId()),
                () -> assertThat(records1.get(0).getBidAmount()).isEqualTo(bidAmount1),
                // 경매 정보
                () -> assertThat(auction.getBidder()).isNotNull(),
                () -> assertThat(auction.getBidder().getId()).isEqualTo(bidder1.getId()),
                () -> assertThat(auction.getBidAmount()).isEqualTo(bidAmount1),
                // 현재 최고 입찰자 포인트
                () -> assertThat(bidder1.getAvailablePoint()).isEqualTo(ENOUGH_POINT - bidAmount1),
                () -> assertThat(memberRepository.getTotalPointsByMemberId(bidder1.getId())).isEqualTo(ENOUGH_POINT)
        );

        // 입찰 2회
        Member bidder2 = createMemberC(ENOUGH_POINT);
        final int bidAmount2 = bidAmount1 + 5_000;
        bidService.bid(auction.getId(), bidder2.getId(), bidAmount2);

        List<AuctionRecord> records2 = auctionRecordRepository.findByAuctionId(auction.getId());
        assertAll(
                // 입찰 기록
                () -> assertThat(records2.size()).isEqualTo(2),
                () -> assertThat(records2.get(0).getBidder().getId()).isEqualTo(bidder1.getId()),
                () -> assertThat(records2.get(0).getBidAmount()).isEqualTo(bidAmount1),
                () -> assertThat(records2.get(1).getBidder().getId()).isEqualTo(bidder2.getId()),
                () -> assertThat(records2.get(1).getBidAmount()).isEqualTo(bidAmount2),
                // 경매 정보
                () -> assertThat(auction.getBidder()).isNotNull(),
                () -> assertThat(auction.getBidder().getId()).isEqualTo(bidder2.getId()),
                () -> assertThat(auction.getBidAmount()).isEqualTo(bidAmount2),
                // 이전 최고 입찰자 포인트
                () -> assertThat(bidder1.getAvailablePoint()).isEqualTo(ENOUGH_POINT),
                () -> assertThat(memberRepository.getTotalPointsByMemberId(bidder1.getId())).isEqualTo(ENOUGH_POINT),
                // 현재 최고 입찰자 포인트
                () -> assertThat(bidder2.getAvailablePoint()).isEqualTo(ENOUGH_POINT - bidAmount2),
                () -> assertThat(memberRepository.getTotalPointsByMemberId(bidder2.getId())).isEqualTo(ENOUGH_POINT)
        );
    }

    private void proceedingBid(Auction auction, Member bidder, int bidAmount) {
        auction.applyNewBid(bidder, bidAmount);
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, bidder, bidAmount));
    }

    private Member createMemberA(int chargeAmount) {
        Member member = memberRepository.save(MemberFixture.A.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
        return member;
    }

    private Member createMemberB(int chargeAmount) {
        Member member = memberRepository.save(MemberFixture.B.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
        return member;
    }

    private Member createMemberC(int chargeAmount) {
        Member member = memberRepository.save(MemberFixture.C.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
        return member;
    }

    private Art createAuctionArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art, LocalDateTime startDate, LocalDateTime endDate) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(startDate, endDate)));
    }
}