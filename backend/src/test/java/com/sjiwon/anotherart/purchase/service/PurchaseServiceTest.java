package com.sjiwon.anotherart.purchase.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_C;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Purchase [Service Layer] -> PurchaseService 테스트")
class PurchaseServiceTest extends ServiceTest {
    @Autowired
    private PurchaseService purchaseService;

    private Member owner;
    private Member bidder;
    private Member buyer;
    private Art generalArt;
    private Art auctionArt;
    private Auction auction;

    @BeforeEach
    void setUp() {
        owner = createMember(MEMBER_A);
        bidder = createMember(MEMBER_B);
        buyer = createMember(MEMBER_C);
        generalArt = artRepository.save(GENERAL_1.toArt(owner));
        auctionArt = artRepository.save(AUCTION_1.toArt(owner));
        auction = auctionRepository.save(AUCTION_OPEN_NOW.toAuction(auctionArt));
    }

    private Member createMember(final MemberFixture fixture) {
        final Member member = fixture.toMember();
        member.addPointRecords(CHARGE, 100_000_000);

        return memberRepository.save(member);
    }

    @Nested
    @DisplayName("일반 작품 구매")
    class purchaseGeneralArt {
        @Test
        @DisplayName("작품 소유자는 본인 작품을 구매할 수 없다")
        void throwExceptionByArtOwnerCannotPurchaseOwn() {
            assertThatThrownBy(() -> purchaseService.purchaseArt(generalArt.getId(), owner.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ART_OWNER_CANNOT_PURCHASE_OWN.getMessage());
        }

        @Test
        @DisplayName("작품이 이미 판매되었으면 구매할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            generalArt.closeSale();

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(generalArt.getId(), buyer.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ALREADY_SOLD.getMessage());
        }

        @Test
        @DisplayName("작품을 구매한다")
        void success() {
            // when
            final Long purchaseId = purchaseService.purchaseArt(generalArt.getId(), buyer.getId());

            // then
            final Purchase findPurchase = purchaseRepository.findById(purchaseId).orElseThrow();
            assertAll(
                    () -> assertThat(findPurchase.getArt()).isEqualTo(generalArt),
                    () -> assertThat(findPurchase.getBuyer()).isEqualTo(buyer),
                    () -> assertThat(findPurchase.getPrice()).isEqualTo(generalArt.getPrice())
            );
        }
    }

    @Nested
    @DisplayName("경매 작품 구매")
    class purchaseAuctionArt {
        @Test
        @DisplayName("경매가 종료되지 않았다면 구매할 수 없다")
        void throwExceptionByAuctionNotFinished() {
            assertThatThrownBy(() -> purchaseService.purchaseArt(auctionArt.getId(), buyer.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.AUCTION_NOT_FINISHED.getMessage());
        }

        @Test
        @DisplayName("낙찰자가 아니면 구매할 수 없다")
        void throwExceptionByBuyerIsNotHighestBidder() {
            // given
            bid(bidder, auction.getHighestBidPrice() + 50_000);
            makeAuctionFinish();

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(auctionArt.getId(), buyer.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.BUYER_IS_NOT_HIGHEST_BIDDER.getMessage());
        }

        @Test
        @DisplayName("작품이 이미 판매되었으면 구매할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            bid(bidder, auctionArt.getPrice() + 50_000);
            bid(buyer, auctionArt.getPrice() + 100_000);
            makeAuctionFinish();
            auctionArt.closeSale();

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(auctionArt.getId(), buyer.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ALREADY_SOLD.getMessage());
        }

        @Test
        @DisplayName("작품을 구매한다")
        void success() {
            // given
            bid(bidder, auctionArt.getPrice() + 50_000);
            bid(buyer, auctionArt.getPrice() + 100_000);
            makeAuctionFinish();

            // when
            final Long purchaseId = purchaseService.purchaseArt(auctionArt.getId(), buyer.getId());

            // then
            final Purchase findPurchase = purchaseRepository.findById(purchaseId).orElseThrow();
            assertAll(
                    () -> assertThat(findPurchase.getArt()).isEqualTo(auctionArt),
                    () -> assertThat(findPurchase.getBuyer()).isEqualTo(buyer),
                    () -> assertThat(findPurchase.getPrice()).isEqualTo(auctionArt.getPrice() + 100_000)
            );
        }

        private void bid(final Member bidder, final int bidPrice) {
            auction.applyNewBid(bidder, bidPrice);
        }

        private void makeAuctionFinish() {
            ReflectionTestUtils.setField(auction.getPeriod(), "startDate", LocalDateTime.now().minusDays(4));
            ReflectionTestUtils.setField(auction.getPeriod(), "endDate", LocalDateTime.now().minusDays(1));
        }
    }
}
