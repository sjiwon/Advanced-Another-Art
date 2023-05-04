package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_A;
import static com.sjiwon.anotherart.fixture.ArtFixture.GENERAL_A;
import static com.sjiwon.anotherart.fixture.MemberFixture.*;
import static com.sjiwon.anotherart.fixture.PeriodFixture.*;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
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
        owner = createMember(MEMBER_A, 1L);
        memberA = createMember(MEMBER_B, 2L);
        memberB = createMember(MEMBER_C, 3L);
    }

    private Member createMember(MemberFixture fixture, Long id) {
        Member member = fixture.toMember();
        member.addPointRecords(CHARGE, MEMBER_INIT_POINT);
        ReflectionTestUtils.setField(member, "id", id);

        return member;
    }

    @Nested
    @DisplayName("Auction 생성")
    class construct {
        @Test
        @DisplayName("경매 작품이 아니면 Auction을 생성할 수 없다")
        void throwExceptionByInvalidArtType() {
            // given
            Art generalArt = GENERAL_A.toArt(owner);

            // when - then
            assertThatThrownBy(() -> Auction.createAuction(generalArt, OPEN_WEEK_1_LATER.toPeriod()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.INVALID_ART_TYPE.getMessage());
        }

        @Test
        @DisplayName("경매 작품에 대한 Auction을 생성한다")
        void success() {
            // given
            Art auctionArt = AUCTION_A.toArt(owner);

            // when
            Auction auction = Auction.createAuction(auctionArt, OPEN_WEEK_1_LATER.toPeriod());

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
            art = AUCTION_A.toArt(owner);
            auction = Auction.createAuction(art, OPEN_NOW.toPeriod());
            initBidPrice = art.getPrice();
        }

        @Test
        @DisplayName("경매가 진행되고 있지 않으면 입찰을 진행할 수 없다")
        void throwExceptionByAuctionIsNotInProgess() {
            // given
            Auction auctionA = Auction.createAuction(art, CLOSED_WEEK_2_AGO.toPeriod());
            Auction auctionB = Auction.createAuction(art, OPEN_WEEK_1_LATER.toPeriod());

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
}
