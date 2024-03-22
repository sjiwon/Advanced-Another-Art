package com.sjiwon.anotherart.purchase.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.purchase.exception.PurchaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_1주전_종료;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_현재_진행;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.ALREADY_SOLD;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.ART_OWNER_CANNOT_PURCHASE_OWN;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.AUCTION_NOT_FINISHED;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.BUYER_IS_NOT_HIGHEST_BIDDER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Purchase -> PurchaseInspector 테스트")
class PurchaseInspectorTest extends UnitTest {
    private final PurchaseInspector sut = new PurchaseInspector();

    private final Member owner = MEMBER_A.toDomain().apply(1L);
    private final Member buyerA = MEMBER_A.toDomain().apply(2L);
    private final Member buyerB = MEMBER_A.toDomain().apply(3L);

    @Nested
    @DisplayName("경매 작품 구매 전 검사")
    class CheckAuctionArt {
        @Test
        @DisplayName("1. 경매가 진행중이면 구매할 수 없다")
        void throwExceptionByAuctionNotFinished() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_현재_진행.toDomain(art).apply(1L);

            // when - then
            assertThatThrownBy(() -> sut.checkAuctionArt(auction, art, buyerA))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(AUCTION_NOT_FINISHED.getMessage());
        }

        @Test
        @DisplayName("2-1. 최고 입찰자가 아니면 구매할 수 없다 [최고 입찰자 X]")
        void throwExceptionByBuyerIsNotHighestBidderA() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);

            // when - then
            assertThatThrownBy(() -> sut.checkAuctionArt(auction, art, buyerA))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(BUYER_IS_NOT_HIGHEST_BIDDER.getMessage());
        }

        @Test
        @DisplayName("2-2. 최고 입찰자가 아니면 구매할 수 없다 [최고 입찰자 O]")
        void throwExceptionByBuyerIsNotHighestBidderB() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);
            auction.updateHighestBid(buyerA, auction.getHighestBidPrice());

            // when - then
            assertThatThrownBy(() -> sut.checkAuctionArt(auction, art, buyerB))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(BUYER_IS_NOT_HIGHEST_BIDDER.getMessage());
        }

        @Test
        @DisplayName("3. 이미 판매된 작품이면 구매할 수 없다")
        void throwExceptionByArtIsSold() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);
            auction.updateHighestBid(buyerA, auction.getHighestBidPrice());
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> sut.checkAuctionArt(auction, art, buyerA))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(ALREADY_SOLD.getMessage());
        }

        @Test
        @DisplayName("4. 검사가 완료되었고 구매 가능한 상태이다")
        void success() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);
            auction.updateHighestBid(buyerA, auction.getHighestBidPrice());

            // when - then
            assertDoesNotThrow(() -> sut.checkAuctionArt(auction, art, buyerA));
        }
    }

    @Nested
    @DisplayName("일반 작품 구매 전 검사")
    class CheckGeneralArt {
        @Test
        @DisplayName("1. 작품 소유자는 본인 작품을 구매할 수 없다")
        void throwExceptionByArtOwnerCannotPurchaseOwn() {
            // given
            final Art art = GENERAL_1.toDomain(owner).apply(1L);

            // when - then
            assertThatThrownBy(() -> sut.checkGeneralArt(art, owner))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(ART_OWNER_CANNOT_PURCHASE_OWN.getMessage());
        }

        @Test
        @DisplayName("2. 이미 판매된 작품이면 구매할 수 없다")
        void throwExceptionByArtIsSold() {
            // given
            final Art art = GENERAL_1.toDomain(owner).apply(1L);
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> sut.checkGeneralArt(art, buyerA))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(ALREADY_SOLD.getMessage());
        }

        @Test
        @DisplayName("3. 검사가 완료되었고 구매 가능한 상태이다")
        void success() {
            // given
            final Art art = GENERAL_1.toDomain(owner).apply(1L);

            // when - then
            assertDoesNotThrow(() -> sut.checkGeneralArt(art, buyerA));
        }
    }
}
