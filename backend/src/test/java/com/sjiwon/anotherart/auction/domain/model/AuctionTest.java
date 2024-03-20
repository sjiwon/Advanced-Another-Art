package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.exception.AuctionException;
import com.sjiwon.anotherart.auction.exception.AuctionExceptionCode;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_3;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.CLOSED_WEEK_1_AGO;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.CLOSED_WEEK_2_AGO;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.OPEN_NOW;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.OPEN_WEEK_1_LATER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction -> 도메인 [Auction] 테스트")
class AuctionTest {
    private static final int MEMBER_INIT_POINT = 1_000_000;

    private Member owner;
    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        owner = MEMBER_A.toMember().apply(1L);
        owner.increaseTotalPoint(MEMBER_INIT_POINT);

        memberA = MEMBER_B.toMember().apply(2L);
        memberA.increaseTotalPoint(MEMBER_INIT_POINT);

        memberB = MEMBER_C.toMember().apply(3L);
        memberB.increaseTotalPoint(MEMBER_INIT_POINT);
    }

    @Nested
    @DisplayName("Auction 생성")
    class Construct {
        @Test
        @DisplayName("경매 작품이 아니면 Auction을 생성할 수 없다")
        void throwExceptionByInvalidArtType() {
            // given
            final Art generalArt = GENERAL_1.toArt(owner);

            // when - then
            assertThatThrownBy(() -> Auction.createAuction(generalArt, OPEN_WEEK_1_LATER.toPeriod()))
                    .isInstanceOf(AuctionException.class)
                    .hasMessage(AuctionExceptionCode.INVALID_ART_TYPE.getMessage());
        }

        @Test
        @DisplayName("경매 작품에 대한 Auction을 생성한다")
        void success() {
            // given
            final Art auctionArt = AUCTION_1.toArt(owner);

            // when
            final Auction auction = Auction.createAuction(auctionArt, OPEN_WEEK_1_LATER.toPeriod());

            // then
            assertAll(
                    () -> assertThat(auction.getArt()).isEqualTo(auctionArt),
                    () -> assertThat(auction.getPeriod().getStartDate()).isEqualTo(OPEN_WEEK_1_LATER.getStartDate()),
                    () -> assertThat(auction.getPeriod().getEndDate()).isEqualTo(OPEN_WEEK_1_LATER.getEndDate()),
                    () -> assertThat(auction.getAuctionRecords()).hasSize(0),
                    () -> assertThat(auction.getHighestBidder()).isNull(),
                    () -> assertThat(auction.getHighestBidPrice()).isEqualTo(auctionArt.getPrice())
            );
        }
    }

    @Nested
    @DisplayName("경매 작품 입찰")
    class ApplyNewBid {
        private Art art;
        private Auction auction;

        @BeforeEach
        void setUp() {
            art = AUCTION_1.toArt(owner);
            auction = Auction.createAuction(art, OPEN_NOW.toPeriod());
        }

        @Test
        @DisplayName("경매가 진행되고 있지 않으면 입찰을 진행할 수 없다")
        void throwExceptionByAuctionIsNotInProgess() {
            // given
            final Auction auctionA = Auction.createAuction(art, CLOSED_WEEK_2_AGO.toPeriod());
            final Auction auctionB = Auction.createAuction(art, OPEN_WEEK_1_LATER.toPeriod());

            // when - then
            assertThatThrownBy(() -> auctionA.applyNewBid(memberA, auctionA.getHighestBidPrice() + 50_000))
                    .isInstanceOf(AuctionException.class)
                    .hasMessage(AuctionExceptionCode.AUCTION_IS_NOT_IN_PROGRESS.getMessage());
            assertThatThrownBy(() -> auctionB.applyNewBid(memberA, auctionB.getHighestBidPrice() + 50_000))
                    .isInstanceOf(AuctionException.class)
                    .hasMessage(AuctionExceptionCode.AUCTION_IS_NOT_IN_PROGRESS.getMessage());
        }

        @Test
        @DisplayName("작품 소유자는 입찰을 진행할 수 없다")
        void throwExceptionByArtOwnerCannotBid() {
            // given
            final int newBidPrice = auction.getHighestBidPrice() + 50_000;

            // when - then
            assertThatThrownBy(() -> auction.applyNewBid(owner, newBidPrice))
                    .isInstanceOf(AuctionException.class)
                    .hasMessage(AuctionExceptionCode.ART_OWNER_CANNOT_BID.getMessage());
        }

        @Test
        @DisplayName("최고 입찰자는 연속으로 입찰을 진행할 수 없다")
        void throwExceptionByHighestBidderCannotBidAgain() {
            // given
            final int newBidPrice = auction.getHighestBidPrice() + 50_000;
            auction.applyNewBid(memberA, newBidPrice);

            // when - then
            assertThatThrownBy(() -> auction.applyNewBid(memberA, newBidPrice + 50_000))
                    .isInstanceOf(AuctionException.class)
                    .hasMessage(AuctionExceptionCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN.getMessage());
        }

        @Test
        @DisplayName("입찰 금액이 부족하다면 입찰을 진행할 수 없다 -> 1) 최고 입찰자가 존재하는 경우")
        void throwExceptionByBidPriceIsNotEnoughCaseA() {
            // given
            final int newBidPrice = auction.getHighestBidPrice() + 50_000;
            auction.applyNewBid(memberA, newBidPrice);

            // when - then
            assertThatThrownBy(() -> auction.applyNewBid(memberB, newBidPrice - 10_000)) // 더 적은 금액
                    .isInstanceOf(AuctionException.class)
                    .hasMessage(AuctionExceptionCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
            assertThatThrownBy(() -> auction.applyNewBid(memberB, newBidPrice)) // 동일한 금액
                    .isInstanceOf(AuctionException.class)
                    .hasMessage(AuctionExceptionCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("입찰 금액이 부족하다면 입찰을 진행할 수 없다 -> 2) 최고 입찰자가 존재하지 않는 경우")
        void throwExceptionByBidPriceIsNotEnoughCaseB() {
            assertThatThrownBy(() -> auction.applyNewBid(memberB, auction.getHighestBidPrice() - 10_000))
                    .isInstanceOf(AuctionException.class)
                    .hasMessage(AuctionExceptionCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("입찰을 성공한다 [최고 입찰자 존재 X]")
        void successA() {
            // when
            final int newBidPrice = auction.getHighestBidPrice() + 50_000;
            auction.applyNewBid(memberA, newBidPrice);

            // then
            assertAll(
                    // HighestBid Info
                    () -> assertThat(auction.getAuctionRecords()).hasSize(1),
                    () -> assertThat(auction.getAuctionRecords())
                            .map(AuctionRecord::getBidder)
                            .containsExactly(memberA),
                    () -> assertThat(auction.getAuctionRecords())
                            .map(AuctionRecord::getBidPrice)
                            .containsExactly(newBidPrice),
                    () -> assertThat(auction.getHighestBidder()).isEqualTo(memberA),
                    () -> assertThat(auction.getHighestBidPrice()).isEqualTo(newBidPrice),

                    // Bidders Info
                    () -> assertThat(memberA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - newBidPrice)
            );
        }

        @Test
        @DisplayName("입찰을 성공한다 [최고 입찰자 존재 O]")
        void successB() {
            // given
            final int previousBidPrice = auction.getHighestBidPrice() + 50_000;
            auction.applyNewBid(memberA, previousBidPrice);

            // when
            final int newBidPrice = previousBidPrice + 50_000;
            auction.applyNewBid(memberB, newBidPrice);

            // then
            assertAll(
                    // Bid Info
                    () -> assertThat(auction.getAuctionRecords()).hasSize(2),
                    () -> assertThat(auction.getAuctionRecords())
                            .map(AuctionRecord::getBidder)
                            .containsExactly(memberA, memberB),
                    () -> assertThat(auction.getAuctionRecords())
                            .map(AuctionRecord::getBidPrice)
                            .containsExactly(previousBidPrice, newBidPrice),
                    () -> assertThat(auction.getHighestBidder()).isEqualTo(memberB),
                    () -> assertThat(auction.getHighestBidPrice()).isEqualTo(newBidPrice),

                    // Bidders Info
                    () -> assertThat(memberA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - previousBidPrice + previousBidPrice),
                    () -> assertThat(memberB.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberB.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - newBidPrice)
            );
        }
    }

    @Test
    @DisplayName("최고 입찰자인지 판별한다")
    void isHighestBidder() {
        // given
        final Auction auction = Auction.createAuction(AUCTION_1.toArt(owner), OPEN_NOW.toPeriod());
        auction.applyNewBid(memberA, auction.getHighestBidPrice());

        // when
        final boolean actual1 = auction.isHighestBidder(owner);
        final boolean actual2 = auction.isHighestBidder(memberA);
        final boolean actual3 = auction.isHighestBidder(memberB);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse()
        );
    }

    @Test
    @DisplayName("경매가 진행중인지 확인한다")
    void isInProgress() {
        // given
        final Auction auctionA = Auction.createAuction(AUCTION_1.toArt(owner), CLOSED_WEEK_1_AGO.toPeriod());
        final Auction auctionB = Auction.createAuction(AUCTION_2.toArt(owner), OPEN_NOW.toPeriod());
        final Auction auctionC = Auction.createAuction(AUCTION_3.toArt(owner), OPEN_WEEK_1_LATER.toPeriod());

        // when
        final boolean actual1 = auctionA.isInProgress();
        final boolean actual2 = auctionB.isInProgress();
        final boolean actual3 = auctionC.isInProgress();

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse()
        );
    }
}
