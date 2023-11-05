package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction 도메인 {Bidders VO} 테스트")
class BiddersTest {
    private static final int MEMBER_INIT_POINT = 1_000_000;
    private static final int AUCTION_INIT_PRICE = 100_000;

    private Member memberA;
    private Member memberB;
    private Art art;

    @BeforeEach
    void setUp() {
        memberA = createMember(MEMBER_A, 1L);
        memberB = createMember(MEMBER_B, 2L);

        final Member owner = MEMBER_C.toMember();
        art = AUCTION_1.toArt(owner);
    }

    private Member createMember(final MemberFixture fixture, final Long id) {
        final Member member = fixture.toMember().apply(id);
        member.addPointRecords(CHARGE, MEMBER_INIT_POINT);
        return member;
    }

    @Test
    @DisplayName("Bidders를 생성한다")
    void construct() {
        final Bidders bidders = Bidders.init(AUCTION_INIT_PRICE);

        assertAll(
                () -> assertThat(bidders.getAuctionRecords()).hasSize(0),
                () -> assertThat(bidders.getHighestBidder()).isNull(),
                () -> assertThat(bidders.getHighestBidPrice()).isEqualTo(AUCTION_INIT_PRICE)
        );
    }

    @Nested
    @DisplayName("입찰 진행")
    class applyNewBid {
        private Auction auction;
        private Bidders bidders;

        @BeforeEach
        void setup() {
            auction = AUCTION_OPEN_NOW.toAuction(art);
            bidders = Bidders.init(AUCTION_INIT_PRICE);
        }

        @Test
        @DisplayName("최고 입찰자는 연속으로 입찰을 진행할 수 없다")
        void throwExceptionByHighestBidderCannotBidAgain() {
            // given
            bidders.applyNewBid(auction, memberA, AUCTION_INIT_PRICE + 50_000);

            // when - then
            assertThatThrownBy(() -> bidders.applyNewBid(auction, memberA, AUCTION_INIT_PRICE + 100_000))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN.getMessage());
        }

        @Test
        @DisplayName("입찰 금액이 부족하다면 입찰을 진행할 수 없다")
        void throwExceptionByBidPriceIsNotEnough() {
            // Case 1) HighestBidder가 존재하지 않는 경우
            assertThatThrownBy(() -> bidders.applyNewBid(auction, memberA, AUCTION_INIT_PRICE - 1))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());

            // Case 2) HighestBidder가 존재하는 경우
            bidders.applyNewBid(auction, memberA, AUCTION_INIT_PRICE + 10_000);

            assertThatThrownBy(() -> bidders.applyNewBid(auction, memberB, AUCTION_INIT_PRICE + 10_000))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("입찰을 성공한다 [최고 입찰자 존재 X]")
        void successCase1() {
            // when
            bidders.applyNewBid(auction, memberA, AUCTION_INIT_PRICE);

            // then
            assertAll(
                    // Bid Info
                    () -> assertThat(bidders.getAuctionRecords()).hasSize(1),
                    () -> assertThat(bidders.getAuctionRecords())
                            .map(AuctionRecord::getBidder)
                            .containsExactly(memberA),
                    () -> assertThat(bidders.getAuctionRecords())
                            .map(AuctionRecord::getBidPrice)
                            .containsExactly(AUCTION_INIT_PRICE),
                    () -> assertThat(bidders.getHighestBidder()).isEqualTo(memberA),
                    () -> assertThat(bidders.getHighestBidPrice()).isEqualTo(AUCTION_INIT_PRICE),

                    // Bidders Info
                    () -> assertThat(memberA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - AUCTION_INIT_PRICE)
            );
        }

        @Test
        @DisplayName("입찰을 성공한다 [최고 입찰자 존재 O]")
        void successCase2() {
            // given
            bidders.applyNewBid(auction, memberA, AUCTION_INIT_PRICE);

            // when
            final int newBidPrice = AUCTION_INIT_PRICE + 50_000;
            bidders.applyNewBid(auction, memberB, newBidPrice);

            // then
            assertAll(
                    // Bid Info
                    () -> assertThat(bidders.getAuctionRecords()).hasSize(2),
                    () -> assertThat(bidders.getAuctionRecords())
                            .map(AuctionRecord::getBidder)
                            .containsExactly(memberA, memberB),
                    () -> assertThat(bidders.getAuctionRecords())
                            .map(AuctionRecord::getBidPrice)
                            .containsExactly(AUCTION_INIT_PRICE, newBidPrice),
                    () -> assertThat(bidders.getHighestBidder()).isEqualTo(memberB),
                    () -> assertThat(bidders.getHighestBidPrice()).isEqualTo(newBidPrice),

                    // Bidders Info
                    () -> assertThat(memberA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberB.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberB.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - newBidPrice)
            );
        }
    }
}
