package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
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

@DisplayName("Auction 도메인 테스트")
class AuctionTest {
    private static final int MEMBER_INIT_POINT = 1_000_000;

    private Member owner;
    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        // TODO Point 도메인 분리 후 리팩토링
        owner = MEMBER_A.toMember().apply(1L);
//        owner.addPointRecords(CHARGE, MEMBER_INIT_POINT);

        memberA = MEMBER_B.toMember().apply(2L);
//        memberA.addPointRecords(CHARGE, MEMBER_INIT_POINT);

        memberB = MEMBER_C.toMember().apply(3L);
//        memberB.addPointRecords(CHARGE, MEMBER_INIT_POINT);
    }

    @Nested
    @DisplayName("Auction 생성")
    class construct {
        @Test
        @DisplayName("경매 작품이 아니면 Auction을 생성할 수 없다")
        void throwExceptionByInvalidArtType() {
            // given
            final Art generalArt = GENERAL_1.toArt(owner);

            // when - then
            assertThatThrownBy(() -> Auction.createAuction(generalArt, OPEN_WEEK_1_LATER.toPeriod()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.INVALID_ART_TYPE.getMessage());
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
    class applyNewBid {
        private Art art;
        private Auction auction;
        private int initBidPrice;

        @BeforeEach
        void setUp() {
            art = AUCTION_1.toArt(owner);
            auction = Auction.createAuction(art, OPEN_NOW.toPeriod());
            initBidPrice = art.getPrice();
        }

        @Test
        @DisplayName("경매가 진행되고 있지 않으면 입찰을 진행할 수 없다")
        void throwExceptionByAuctionIsNotInProgess() {
            // given
            final Auction auctionA = Auction.createAuction(art, CLOSED_WEEK_2_AGO.toPeriod());
            final Auction auctionB = Auction.createAuction(art, OPEN_WEEK_1_LATER.toPeriod());

            // when - then
            assertThatThrownBy(() -> auctionA.applyNewBid(memberA, initBidPrice))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.AUCTION_IS_NOT_IN_PROGRESS.getMessage());
            assertThatThrownBy(() -> auctionB.applyNewBid(memberA, initBidPrice))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.AUCTION_IS_NOT_IN_PROGRESS.getMessage());
        }

        @Test
        @DisplayName("작품 소유자는 입찰을 진행할 수 없다")
        void throwExceptionByArtOwnerCannotBid() {
            assertThatThrownBy(() -> auction.applyNewBid(owner, initBidPrice))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.ART_OWNER_CANNOT_BID.getMessage());
        }

        @Test
        @DisplayName("최고 입찰자는 연속으로 입찰을 진행할 수 없다")
        void throwExceptionByHighestBidderCannotBidAgain() {
            // given
            auction.applyNewBid(memberA, initBidPrice);

            // when - then
            assertThatThrownBy(() -> auction.applyNewBid(memberA, initBidPrice + 50_000))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN.getMessage());
        }

        @Test
        @DisplayName("입찰 금액이 부족하다면 입찰을 진행할 수 없다")
        void throwExceptionByBidPriceIsNotEnough() {
            // Case 1) HighestBidder가 존재하지 않는 경우
            assertThatThrownBy(() -> auction.applyNewBid(memberA, initBidPrice - 1))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());

            // Case 2) HighestBidder가 존재하는 경우
            auction.applyNewBid(memberA, initBidPrice);
            assertThatThrownBy(() -> auction.applyNewBid(memberB, initBidPrice))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("입찰을 성공한다 [최고 입찰자 존재 X]")
        void successCase1() {
            // when
            auction.applyNewBid(memberA, initBidPrice);

            // then
            assertAll(
                    // Bid Info
                    () -> assertThat(auction.getAuctionRecords()).hasSize(1),
                    () -> assertThat(auction.getAuctionRecords())
                            .map(AuctionRecord::getBidder)
                            .containsExactly(memberA),
                    () -> assertThat(auction.getAuctionRecords())
                            .map(AuctionRecord::getBidPrice)
                            .containsExactly(initBidPrice),
                    () -> assertThat(auction.getHighestBidder()).isEqualTo(memberA),
                    () -> assertThat(auction.getHighestBidPrice()).isEqualTo(initBidPrice),

                    // Bidders Info
                    () -> assertThat(memberA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - initBidPrice)
            );
        }

        @Test
        @DisplayName("입찰을 성공한다 [최고 입찰자 존재 O]")
        void successCase2() {
            // given
            auction.applyNewBid(memberA, initBidPrice);

            // when
            final int newBidPrice = initBidPrice + 50_000;
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
                            .containsExactly(initBidPrice, newBidPrice),
                    () -> assertThat(auction.getHighestBidder()).isEqualTo(memberB),
                    () -> assertThat(auction.getHighestBidPrice()).isEqualTo(newBidPrice),

                    // Bidders Info
                    () -> assertThat(memberA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberB.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberB.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - newBidPrice)
            );
        }
    }

    @Test
    @DisplayName("최고 입찰자인지 판별한다")
    void isHighestBidder() {
        // given
        final Art art = AUCTION_1.toArt(owner);
        final Auction auction = Auction.createAuction(art, OPEN_NOW.toPeriod());
        auction.applyNewBid(memberA, art.getPrice());

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
    @DisplayName("경매가 종료되었는지 확인한다")
    void isAuctionFinished() {
        // given
        final Art artA = AUCTION_1.toArt(owner);
        final Art artB = AUCTION_2.toArt(owner);

        final Auction auctionA = Auction.createAuction(artA, CLOSED_WEEK_1_AGO.toPeriod());
        final Auction auctionB = Auction.createAuction(artB, OPEN_NOW.toPeriod());

        // when
        final boolean actual1 = auctionA.isFinished();
        final boolean actual2 = auctionB.isFinished();

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
