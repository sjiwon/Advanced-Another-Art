package com.sjiwon.anotherart.auction.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.exception.AuctionException;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.ART_OWNER_CANNOT_BID;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.AUCTION_IS_NOT_IN_PROGRESS;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.BID_PRICE_IS_NOT_ENOUGH;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_1주뒤_오픈;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_1주전_종료;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_현재_진행;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Auction -> BidInspector 테스트")
class BidInspectorTest extends UnitTest {
    private final BidInspector sut = new BidInspector();

    private final Member owner = MEMBER_A.toDomain().apply(1L);
    private final Member bidderA = MEMBER_B.toDomain().apply(2L);
    private final Member bidderB = MEMBER_C.toDomain().apply(3L);
    private final Art art = AUCTION_1.toDomain(owner).apply(1L);

    @Test
    @DisplayName("작품 소유자는 입찰을 시도할 수 없다")
    void throwExceptionByArtOwnerCannotBid() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(art);

        // when - then
        assertThatThrownBy(() -> sut.checkBidCanBeProceed(auction, art, owner, auction.getHighestBidPrice()))
                .isInstanceOf(AuctionException.class)
                .hasMessage(ART_OWNER_CANNOT_BID.getMessage());
    }

    @Test
    @DisplayName("경매 진행 기간이 아니면 입찰을 진행할 수 없다")
    void throwExceptionByAuctionIsNotInProgress() {
        // given
        final Auction auctionA = 경매_1주전_종료.toDomain(art);
        final Auction auctionB = 경매_1주뒤_오픈.toDomain(art);

        // when - then
        assertAll(
                () -> assertThatThrownBy(() -> sut.checkBidCanBeProceed(auctionA, art, bidderA, auctionA.getHighestBidPrice()))
                        .isInstanceOf(AuctionException.class)
                        .hasMessage(AUCTION_IS_NOT_IN_PROGRESS.getMessage()),
                () -> assertThatThrownBy(() -> sut.checkBidCanBeProceed(auctionB, art, bidderA, auctionB.getHighestBidPrice()))
                        .isInstanceOf(AuctionException.class)
                        .hasMessage(AUCTION_IS_NOT_IN_PROGRESS.getMessage())
        );
    }

    @Test
    @DisplayName("최고 입찰자는 연속으로 입찰을 진행할 수 없다")
    void throwExceptionByHighestBidderCannotBidAgain() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(art);
        auction.updateHighestBid(bidderA, auction.getHighestBidPrice());

        // when - then
        assertThatThrownBy(() -> sut.checkBidCanBeProceed(auction, art, bidderA, auction.getHighestBidPrice() + 5_000))
                .isInstanceOf(AuctionException.class)
                .hasMessage(HIGHEST_BIDDER_CANNOT_BID_AGAIN.getMessage());
    }

    @Test
    @DisplayName("입찰가가 부족하면 입찰을 진행할 수 없다 [최고 입찰자 존재 X]")
    void throwExceptionByBidPriceIsNotEnoughA() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(art);

        // when - then
        assertThatThrownBy(() -> sut.checkBidCanBeProceed(auction, art, bidderA, auction.getHighestBidPrice() - 500))
                .isInstanceOf(AuctionException.class)
                .hasMessage(BID_PRICE_IS_NOT_ENOUGH.getMessage());
    }

    @Test
    @DisplayName("입찰가가 부족하면 입찰을 진행할 수 없다 [최고 입찰자 존재 O]")
    void throwExceptionByBidPriceIsNotEnoughB() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(art);
        auction.updateHighestBid(bidderA, auction.getHighestBidPrice());

        // when - then
        assertAll(
                () -> assertThatThrownBy(() -> sut.checkBidCanBeProceed(auction, art, bidderB, auction.getHighestBidPrice() - 500))
                        .isInstanceOf(AuctionException.class)
                        .hasMessage(BID_PRICE_IS_NOT_ENOUGH.getMessage()),
                () -> assertThatThrownBy(() -> sut.checkBidCanBeProceed(auction, art, bidderB, auction.getHighestBidPrice()))
                        .isInstanceOf(AuctionException.class)
                        .hasMessage(BID_PRICE_IS_NOT_ENOUGH.getMessage())
        );
    }

    @Test
    @DisplayName("모든 검증을 통과하고 입찰을 진행할 준비를 마친다 [최고 입찰자 존재 X]")
    void successA() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(art);

        // when - then
        assertDoesNotThrow(() -> sut.checkBidCanBeProceed(auction, art, bidderA, auction.getHighestBidPrice()));
    }

    @Test
    @DisplayName("모든 검증을 통과하고 입찰을 진행할 준비를 마친다 [최고 입찰자 존재 O]")
    void successB() {
        // given
        final Auction auction = 경매_현재_진행.toDomain(art);
        auction.updateHighestBid(bidderA, auction.getHighestBidPrice());

        // when - then
        assertDoesNotThrow(() -> sut.checkBidCanBeProceed(auction, art, bidderB, auction.getHighestBidPrice() + 500));
    }
}
