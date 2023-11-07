package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.common.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction [Service Layer] -> BidService 테스트")
class BidServiceTest extends ServiceTest {
    @Autowired
    private BidService bidService;

    private static final int MEMBER_INIT_POINT = 1_000_000;

    private Member owner;
    private Member bidderA;
    private Member bidderB;
    private Auction auction;

    @BeforeEach
    void setUp() {
        owner = createMember(MEMBER_A);
        bidderA = createMember(MEMBER_B);
        bidderB = createMember(MEMBER_C);

        final Art art = artRepository.save(AUCTION_1.toArt(owner));
        auction = auctionRepository.save(AUCTION_OPEN_NOW.toAuction(art));
    }

    private Member createMember(final MemberFixture fixture) {
        final Member member = fixture.toMember();
        // TODO Point 도메인 분리 후 리팩토링
//        member.addPointRecords(CHARGE, MEMBER_INIT_POINT);

        return memberRepository.save(member);
    }

    @Nested
    @DisplayName("경매 작품 입찰")
    class bid {
        @Test
        @DisplayName("경매가 진행중이지 않으면 입찰을 할 수 없다")
        void throwExceptionByAuctionIsNotInProgess() {
            // Case 1) 시작 전
            ReflectionTestUtils.setField(auction.getPeriod(), "startDate", LocalDateTime.now().plusDays(1));
            ReflectionTestUtils.setField(auction.getPeriod(), "endDate", LocalDateTime.now().plusDays(2));

            assertThatThrownBy(() -> bidService.bid(auction.getId(), bidderA.getId(), auction.getHighestBidPrice()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.AUCTION_IS_NOT_IN_PROGRESS.getMessage());

            // Case 2) 종료
            ReflectionTestUtils.setField(auction.getPeriod(), "startDate", LocalDateTime.now().minusDays(4));
            ReflectionTestUtils.setField(auction.getPeriod(), "endDate", LocalDateTime.now().minusDays(1));

            assertThatThrownBy(() -> bidService.bid(auction.getId(), bidderA.getId(), auction.getHighestBidPrice()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.AUCTION_IS_NOT_IN_PROGRESS.getMessage());
        }

        @Test
        @DisplayName("경매 작품 소유자는 본인 작품에 입찰을 할 수 없다")
        void throwExceptionByArtOwnerCannotBid() {
            assertThatThrownBy(() -> bidService.bid(auction.getId(), owner.getId(), auction.getHighestBidPrice()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.ART_OWNER_CANNOT_BID.getMessage());
        }

        @Test
        @DisplayName("현재 최고 입찰자는 연속으로 다시 입찰을 진행할 수 없다")
        void throwExceptionByHighestBidderCannotBidAgain() {
            // given
            bidService.bid(auction.getId(), bidderA.getId(), auction.getHighestBidPrice());

            // when - then
            assertThatThrownBy(() -> bidService.bid(auction.getId(), bidderA.getId(), auction.getHighestBidPrice() + 50_000))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN.getMessage());
        }

        @Test
        @DisplayName("입찰가가 부족하면 입찰을 진행할 수 없다")
        void throwExceptionByBidPriceIsNotEnough() {
            // Case 1) HighestBidder가 존재하지 않는 경우
            assertThatThrownBy(() -> bidService.bid(auction.getId(), bidderA.getId(), auction.getHighestBidPrice() - 1))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());

            // Case 2) HighestBidder가 존재하는 경우
            bidService.bid(auction.getId(), bidderA.getId(), auction.getHighestBidPrice());

            assertThatThrownBy(() -> bidService.bid(auction.getId(), bidderB.getId(), auction.getHighestBidPrice()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("사용 가능한 포인트가 부족하면 입찰을 진행할 수 없다")
        void throwExceptionByPointIsNotEnough() {
            ReflectionTestUtils.setField(bidderA.getPoint(), "availablePoint", 0);

            assertThatThrownBy(() -> bidService.bid(auction.getId(), bidderA.getId(), auction.getHighestBidPrice()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("입찰을 성공한다 [최고 입찰자 존재 X]")
        void successCase1() {
            // when
            bidService.bid(auction.getId(), bidderA.getId(), auction.getHighestBidPrice());

            // then
            final Auction findAuction = auctionRepository.findById(auction.getId()).orElseThrow();
            assertAll(
                    // Bid Info
                    () -> assertThat(findAuction.getAuctionRecords()).hasSize(1),
                    () -> assertThat(findAuction.getAuctionRecords())
                            .map(AuctionRecord::getBidder)
                            .containsExactly(bidderA),
                    () -> assertThat(findAuction.getAuctionRecords())
                            .map(AuctionRecord::getBidPrice)
                            .containsExactly(auction.getHighestBidPrice()),
                    () -> assertThat(findAuction.getHighestBidder()).isEqualTo(bidderA),
                    () -> assertThat(findAuction.getHighestBidPrice()).isEqualTo(auction.getHighestBidPrice()),

                    // Bidders Info
                    () -> assertThat(bidderA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(bidderA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - auction.getHighestBidPrice())
            );
        }

        @Test
        @DisplayName("입찰을 성공한다  [최고 입찰자 존재 O]")
        void successCase2() {
            // given
            final int initBidPrice = auction.getHighestBidPrice();
            bidService.bid(auction.getId(), bidderA.getId(), initBidPrice);

            // when
            final int newBidPrice = auction.getHighestBidPrice() + 10_000;
            bidService.bid(auction.getId(), bidderB.getId(), newBidPrice);

            // then
            final Auction findAuction = auctionRepository.findById(auction.getId()).orElseThrow();
            assertAll(
                    // Bid Info
                    () -> assertThat(findAuction.getAuctionRecords()).hasSize(2),
                    () -> assertThat(findAuction.getAuctionRecords())
                            .map(AuctionRecord::getBidder)
                            .containsExactly(bidderA, bidderB),
                    () -> assertThat(findAuction.getAuctionRecords())
                            .map(AuctionRecord::getBidPrice)
                            .containsExactly(initBidPrice, newBidPrice),
                    () -> assertThat(findAuction.getHighestBidder()).isEqualTo(bidderB),
                    () -> assertThat(findAuction.getHighestBidPrice()).isEqualTo(newBidPrice),

                    // Bidders Info
                    () -> assertThat(bidderA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(bidderA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(bidderB.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(bidderB.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - newBidPrice)
            );
        }
    }
}
