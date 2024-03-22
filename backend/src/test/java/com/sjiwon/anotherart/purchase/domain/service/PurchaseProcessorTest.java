package com.sjiwon.anotherart.purchase.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.service.AuctionReader;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.exception.MemberException;
import com.sjiwon.anotherart.point.domain.service.PointRecordWriter;
import com.sjiwon.anotherart.purchase.exception.PurchaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_1주전_종료;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_현재_진행;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.POINT_IS_NOT_ENOUGH;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.ALREADY_SOLD;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.ART_OWNER_CANNOT_PURCHASE_OWN;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.AUCTION_NOT_FINISHED;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.BUYER_IS_NOT_HIGHEST_BIDDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("Purchase -> PurchaseProcessor 테스트")
class PurchaseProcessorTest extends UnitTest {
    private final PurchaseProcessor sut = new PurchaseProcessor(
            new AuctionReader(auctionRepository),
            new PurchaseInspector(),
            new PurchaseWriter(purchaseRepository),
            new PointRecordWriter(pointRecordRepository),
            new AssociatedPointTransactionProcessor()
    );

    private static final int MEMBER_INIT_POINT = 100_000_000;

    private Member owner;
    private Member buyerA;
    private Member buyerB;

    @BeforeEach
    void setUp() {
        owner = MEMBER_A.toDomain(MEMBER_INIT_POINT).apply(1L);
        buyerA = MEMBER_B.toDomain(MEMBER_INIT_POINT).apply(2L);
        buyerB = MEMBER_C.toDomain(MEMBER_INIT_POINT).apply(3L);
    }

    @Nested
    @DisplayName("경매 작품 구매")
    class PurchaseAuctionArt {
        @Test
        @DisplayName("1. 경매가 진행중이면 구매할 수 없다")
        void throwExceptionByAuctionNotFinished() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_현재_진행.toDomain(art).apply(1L);
            given(auctionRepository.findByArtId(art.getId())).willReturn(Optional.of(auction));

            // when - then
            assertThatThrownBy(() -> sut.purchaseAuctionArt(art, owner, buyerA))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(AUCTION_NOT_FINISHED.getMessage());
        }

        @Test
        @DisplayName("2-1. 최고 입찰자가 아니면 구매할 수 없다 [최고 입찰자 X]")
        void throwExceptionByBuyerIsNotHighestBidderA() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);
            given(auctionRepository.findByArtId(art.getId())).willReturn(Optional.of(auction));

            // when - then
            assertThatThrownBy(() -> sut.purchaseAuctionArt(art, owner, buyerA))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(BUYER_IS_NOT_HIGHEST_BIDDER.getMessage());
        }

        @Test
        @DisplayName("2-2. 최고 입찰자가 아니면 구매할 수 없다 [최고 입찰자 O]")
        void throwExceptionByBuyerIsNotHighestBidderB() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);
            given(auctionRepository.findByArtId(art.getId())).willReturn(Optional.of(auction));

            final int bidPrice = auction.getHighestBidPrice() + 50_000;
            auction.updateHighestBid(buyerA, bidPrice);

            // when - then
            assertThatThrownBy(() -> sut.purchaseAuctionArt(art, owner, buyerB))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(BUYER_IS_NOT_HIGHEST_BIDDER.getMessage());
        }

        @Test
        @DisplayName("3. 이미 판매된 작품이면 구매할 수 없다")
        void throwExceptionByArtIsSold() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);
            given(auctionRepository.findByArtId(art.getId())).willReturn(Optional.of(auction));

            final int bidPrice = auction.getHighestBidPrice() + 50_000;
            auction.updateHighestBid(buyerA, bidPrice);
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> sut.purchaseAuctionArt(art, owner, buyerA))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(ALREADY_SOLD.getMessage());
        }

        @Test
        @DisplayName("4. 구매자의 포인트가 부족하면 구매를 진행할 수 없다")
        void throwExceptionByBuyerPointNotEnough() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);
            given(auctionRepository.findByArtId(art.getId())).willReturn(Optional.of(auction));

            final int bidPrice = auction.getHighestBidPrice() + 50_000;
            auction.updateHighestBid(buyerA, bidPrice);

            // when - then
            ReflectionTestUtils.setField(buyerA.getPoint(), "totalPoint", 0);
            ReflectionTestUtils.setField(buyerA.getPoint(), "availablePoint", 0);
            assertThatThrownBy(() -> sut.purchaseAuctionArt(art, owner, buyerA))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("5. 경매 작품 구매를 진행한다")
        void success() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);
            given(auctionRepository.findByArtId(art.getId())).willReturn(Optional.of(auction));

            final int bidPrice = auction.getHighestBidPrice() + 50_000;
            auction.updateHighestBid(buyerA, bidPrice);
            buyerA.decreaseAvailablePoint(bidPrice);

            // when
            sut.purchaseAuctionArt(art, owner, buyerA);

            // then
            assertAll(
                    () -> assertThat(art.isSold()).isTrue(),
                    () -> assertThat(owner.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT + bidPrice),
                    () -> assertThat(owner.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT + bidPrice),
                    () -> assertThat(buyerA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT - bidPrice),
                    () -> assertThat(buyerA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - bidPrice)
            );
        }
    }

    @Nested
    @DisplayName("일반 작품 구매")
    class PurchaseGeneralArt {
        @Test
        @DisplayName("1. 작품 소유자는 본인 작품을 구매할 수 없다")
        void throwExceptionByArtOwnerCannotPurchaseOwn() {
            // given
            final Art art = GENERAL_1.toDomain(owner).apply(1L);

            // when - then
            assertThatThrownBy(() -> sut.purchaseGeneralArt(art, owner, owner))
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
            assertThatThrownBy(() -> sut.purchaseGeneralArt(art, owner, buyerA))
                    .isInstanceOf(PurchaseException.class)
                    .hasMessage(ALREADY_SOLD.getMessage());
        }

        @Test
        @DisplayName("3. 구매자의 포인트가 부족하면 구매를 진행할 수 없다")
        void throwExceptionByBuyerPointNotEnough() {
            // given
            final Art art = GENERAL_1.toDomain(owner).apply(1L);

            // when - then
            ReflectionTestUtils.setField(buyerA.getPoint(), "totalPoint", 0);
            ReflectionTestUtils.setField(buyerA.getPoint(), "availablePoint", 0);
            assertThatThrownBy(() -> sut.purchaseGeneralArt(art, owner, buyerA))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("4. 일반 작품 구매를 진행한다")
        void success() {
            // given
            final Art art = GENERAL_1.toDomain(owner).apply(1L);

            // when
            sut.purchaseGeneralArt(art, owner, buyerA);

            // then
            assertAll(
                    () -> assertThat(art.isSold()).isTrue(),
                    () -> assertThat(owner.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT + art.getPrice()),
                    () -> assertThat(owner.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT + art.getPrice()),
                    () -> assertThat(buyerA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT - art.getPrice()),
                    () -> assertThat(buyerA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - art.getPrice())
            );
        }
    }
}
