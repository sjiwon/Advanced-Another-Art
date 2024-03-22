package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.exception.AuctionException;
import com.sjiwon.anotherart.auction.exception.AuctionExceptionCode;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_3;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_1주뒤_오픈;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_1주전_종료;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_현재_진행;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.OPEN_WEEK_1_LATER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction -> 도메인 [Auction] 테스트")
class AuctionTest extends UnitTest {
    private static final int MEMBER_INIT_POINT = 1_000_000;

    private Member owner;
    private Member[] members;
    private Art[] auctionArts;

    @BeforeEach
    void setUp() {
        owner = MEMBER_A.toDomain(MEMBER_INIT_POINT).apply(1L);
        members = List.of(
                MEMBER_B.toDomain(MEMBER_INIT_POINT).apply(2L),
                MEMBER_C.toDomain(MEMBER_INIT_POINT).apply(3L)
        ).toArray(Member[]::new);

        auctionArts = List.of(
                AUCTION_1.toDomain(owner),
                AUCTION_2.toDomain(owner),
                AUCTION_3.toDomain(owner)
        ).toArray(Art[]::new);
    }

    @Nested
    @DisplayName("Auction 생성")
    class Construct {
        @Test
        @DisplayName("경매 작품이 아니면 Auction을 생성할 수 없다")
        void throwExceptionByInvalidArtType() {
            // given
            final Art generalArt = GENERAL_1.toDomain(owner);

            // when - then
            assertThatThrownBy(() -> Auction.createAuction(generalArt, OPEN_WEEK_1_LATER.toDomain()))
                    .isInstanceOf(AuctionException.class)
                    .hasMessage(AuctionExceptionCode.INVALID_ART_TYPE.getMessage());
        }

        @Test
        @DisplayName("경매 작품에 대한 Auction을 생성한다")
        void success() {
            // given
            final Art auctionArt = AUCTION_1.toDomain(owner);

            // when
            final Auction auction = Auction.createAuction(auctionArt, OPEN_WEEK_1_LATER.toDomain());

            // then
            assertAll(
                    () -> assertThat(auction.getArtId()).isEqualTo(auctionArt.getId()),
                    () -> assertThat(auction.getPeriod().getStartDate()).isEqualTo(OPEN_WEEK_1_LATER.getStartDate()),
                    () -> assertThat(auction.getPeriod().getEndDate()).isEqualTo(OPEN_WEEK_1_LATER.getEndDate()),
                    () -> assertThat(auction.getHighestBidderId()).isNull(),
                    () -> assertThat(auction.getHighestBidPrice()).isEqualTo(auctionArt.getPrice()),
                    () -> assertThat(auction.getAuctionRecords()).hasSize(0)
            );
        }
    }

    @Test
    @DisplayName("경매가 진행중인지 확인한다")
    void isInProgress() {
        // given
        final Auction auction1 = 경매_1주전_종료.toDomain(auctionArts[0]);
        final Auction auction2 = 경매_현재_진행.toDomain(auctionArts[0]);
        final Auction auction3 = 경매_1주뒤_오픈.toDomain(auctionArts[0]);

        // when
        final boolean actual1 = auction1.isInProgress();
        final boolean actual2 = auction2.isInProgress();
        final boolean actual3 = auction3.isInProgress();

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse()
        );
    }

    @Test
    @DisplayName("최고 입찰자인지 판별한다")
    void isHighestBidder() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(auctionArts[0]);
        auction.updateHighestBid(members[0], auction.getHighestBidPrice());

        // when
        final boolean actual1 = auction.isHighestBidder(owner);
        final boolean actual2 = auction.isHighestBidder(members[0]);
        final boolean actual3 = auction.isHighestBidder(members[1]);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isFalse()
        );
    }

    @Test
    @DisplayName("새로운 입찰가가 Acceptable한지 검증한다 [최고 입찰자 X]")
    void isNewBidPriceAcceptableA() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(auctionArts[0]);

        // when
        final boolean actual1 = auction.isNewBidPriceAcceptable(auction.getHighestBidPrice() - 500);
        final boolean actual2 = auction.isNewBidPriceAcceptable(auction.getHighestBidPrice());
        final boolean actual3 = auction.isNewBidPriceAcceptable(auction.getHighestBidPrice() + 500);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue()
        );
    }

    @Test
    @DisplayName("새로운 입찰가가 Acceptable한지 검증한다 [최고 입찰자 O]")
    void isNewBidPriceAcceptableB() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(auctionArts[0]);
        auction.updateHighestBid(members[0], auction.getHighestBidPrice());

        // when
        final boolean actual1 = auction.isNewBidPriceAcceptable(auction.getHighestBidPrice() - 500);
        final boolean actual2 = auction.isNewBidPriceAcceptable(auction.getHighestBidPrice());
        final boolean actual3 = auction.isNewBidPriceAcceptable(auction.getHighestBidPrice() + 500);

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isFalse(),
                () -> assertThat(actual3).isTrue()
        );
    }

    @Test
    @DisplayName("최고 입찰 관련 정보를 갱신한다")
    void updateHighestBid() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(auctionArts[0]);
        assertAll(
                () -> assertThat(auction.getHighestBidderId()).isNull(),
                () -> assertThat(auction.getHighestBidPrice()).isEqualTo(auctionArts[0].getPrice())
        );

        /* member0 */
        final int member0BidPrice = auction.getHighestBidPrice() + 5_000;
        auction.updateHighestBid(members[0], member0BidPrice);
        assertAll(
                () -> assertThat(auction.getHighestBidderId()).isEqualTo(members[0].getId()),
                () -> assertThat(auction.getHighestBidPrice()).isEqualTo(member0BidPrice)
        );

        /* member1 */
        final int member1BidPrice = member0BidPrice + 5_000;
        auction.updateHighestBid(members[1], member1BidPrice);
        assertAll(
                () -> assertThat(auction.getHighestBidderId()).isEqualTo(members[1].getId()),
                () -> assertThat(auction.getHighestBidPrice()).isEqualTo(member1BidPrice)
        );
    }
}
