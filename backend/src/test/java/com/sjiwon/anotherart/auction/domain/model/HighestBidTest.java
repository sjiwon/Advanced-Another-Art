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
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction -> 도메인 [HighestBid VO] 테스트")
class HighestBidTest {
    private static final int MEMBER_INIT_POINT = 1_000_000;

    private Member memberA;
    private Member memberB;
    private Art art;

    @BeforeEach
    void setUp() {
        memberA = MEMBER_A.toMember().apply(1L);
        memberA.increaseTotalPoint(MEMBER_INIT_POINT);

        memberB = MEMBER_B.toMember().apply(2L);
        memberB.increaseTotalPoint(MEMBER_INIT_POINT);

        final Member owner = MEMBER_C.toMember();
        art = AUCTION_1.toArt(owner);
    }

    @Test
    @DisplayName("Bidders를 생성한다")
    void init() {
        final HighestBid highestBid = HighestBid.init(art.getPrice());

        assertAll(
                () -> assertThat(highestBid.getBidder()).isNull(),
                () -> assertThat(highestBid.getBidPrice()).isEqualTo(art.getPrice())
        );
    }

    @Nested
    @DisplayName("입찰 진행")
    class ApplyNewBid {
        private Auction auction;
        private HighestBid highestBid;

        @BeforeEach
        void setup() {
            auction = AUCTION_OPEN_NOW.toAuction(art);
            highestBid = auction.getHighestBid();
        }

        @Test
        @DisplayName("최고 입찰자는 연속으로 입찰을 진행할 수 없다")
        void throwExceptionByHighestBidderCannotBidAgain() {
            // given
            final int newBidPrice = auction.getHighestBidPrice() + 50_000;
            highestBid.applyNewBid(memberA, newBidPrice);

            // when - then
            assertThatThrownBy(() -> highestBid.applyNewBid(memberA, newBidPrice + 50_000))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN.getMessage());
        }

        @Test
        @DisplayName("입찰 금액이 부족하다면 입찰을 진행할 수 없다 -> 1) 최고 입찰자가 존재하는 경우")
        void throwExceptionByBidPriceIsNotEnoughCaseA() {
            // given
            final int newBidPrice = auction.getHighestBidPrice() + 50_000;
            highestBid.applyNewBid(memberA, newBidPrice);

            // when - then
            assertThatThrownBy(() -> highestBid.applyNewBid(memberB, newBidPrice - 10_000)) // 더 적은 금액
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
            assertThatThrownBy(() -> highestBid.applyNewBid(memberB, newBidPrice)) // 동일한 금액
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("입찰 금액이 부족하다면 입찰을 진행할 수 없다 -> 2) 최고 입찰자가 존재하지 않는 경우")
        void throwExceptionByBidPriceIsNotEnoughCaseB() {
            assertThatThrownBy(() -> highestBid.applyNewBid(memberA, auction.getHighestBidPrice() - 10_000))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("입찰을 성공한다 [최고 입찰자 존재 X]")
        void succesA() {
            // when
            final int newBidPrice = auction.getHighestBidPrice() + 50_000;
            highestBid.applyNewBid(memberA, newBidPrice);

            // then
            assertAll(
                    // HighestBid Info
                    () -> assertThat(highestBid.getBidder()).isEqualTo(memberA),
                    () -> assertThat(highestBid.getBidPrice()).isEqualTo(newBidPrice),

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
            highestBid.applyNewBid(memberA, previousBidPrice);

            // when
            final int newBidPrice = previousBidPrice + 50_000;
            highestBid.applyNewBid(memberB, newBidPrice);

            // then
            assertAll(
                    // HighestBid Info
                    () -> assertThat(highestBid.getBidder()).isEqualTo(memberB),
                    () -> assertThat(highestBid.getBidPrice()).isEqualTo(newBidPrice),

                    // Bidders Info
                    () -> assertThat(memberA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - previousBidPrice + previousBidPrice),
                    () -> assertThat(memberB.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                    () -> assertThat(memberB.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - newBidPrice)
            );
        }
    }
}
