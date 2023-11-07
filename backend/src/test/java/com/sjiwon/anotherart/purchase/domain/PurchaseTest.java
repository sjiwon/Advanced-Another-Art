package com.sjiwon.anotherart.purchase.domain;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.OPEN_NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 테스트")
class PurchaseTest {
    private static final int MEMBER_INIT_POINT = 1_000_000;

    private Member owner;
    private Member member;
    private Art generalArt;
    private Art auctionArt;

    @BeforeEach
    void setUp() {
        owner = MEMBER_A.toMember().apply(1L);
        // TODO Point 도메인 분리 후 리팩토링
//        owner.addPointRecords(CHARGE, MEMBER_INIT_POINT);
        member = MEMBER_B.toMember().apply(2L);
//        member.addPointRecords(CHARGE, MEMBER_INIT_POINT);

        generalArt = GENERAL_1.toArt(owner).apply(1L);
        auctionArt = AUCTION_1.toArt(owner).apply(2L);
    }

    @Nested
    @DisplayName("일반 작품 구매")
    class purchaseGeneralArt {
        @Test
        @DisplayName("본인 작품은 구매할 수 없다")
        void throwExceptionByArtOwnerCannotPurchaseOwn() {
            assertThatThrownBy(() -> Purchase.purchaseGeneralArt(generalArt, owner))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ART_OWNER_CANNOT_PURCHASE_OWN.getMessage());
        }

        @Test
        @DisplayName("판매중이 아니라면 구매할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            generalArt.closeSale();

            // when - then
            assertThatThrownBy(() -> Purchase.purchaseGeneralArt(generalArt, member))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ALREADY_SOLD.getMessage());
        }

        @Test
        @DisplayName("사용 가능한 포인트가 부족함에 따라 구매할 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            ReflectionTestUtils.setField(member.getPoint(), "availablePoint", 0);

            // when - then
            assertThatThrownBy(() -> Purchase.purchaseGeneralArt(generalArt, member))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("일반 작품을 구매한다")
        void success() {
            // when
            final Purchase purchase = Purchase.purchaseGeneralArt(generalArt, member);

            // then
            assertAll(
                    // Purchase Info
                    () -> assertThat(purchase.getArt()).isEqualTo(generalArt),
                    () -> assertThat(purchase.getArt().isSold()).isTrue(),
                    () -> assertThat(purchase.getBuyer()).isEqualTo(member),
                    () -> assertThat(purchase.getPrice()).isEqualTo(generalArt.getPrice()),

                    // Owner - Buyer
                    () -> assertThat(owner.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT + generalArt.getPrice()),
                    () -> assertThat(owner.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT + generalArt.getPrice()),
                    () -> assertThat(member.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT - generalArt.getPrice()),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - generalArt.getPrice())
            );
        }
    }

    @Nested
    @DisplayName("경매 작품 구매")
    class purchaseAuctionArt {
        private Auction auction;

        @BeforeEach
        void setUp() {
            auction = Auction.createAuction(auctionArt, OPEN_NOW.toPeriod());
            auction.applyNewBid(member, auction.getHighestBidPrice());
        }

        @Test
        @DisplayName("판매중이 아니라면 구매할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            auctionArt.closeSale();

            // when - then
            assertThatThrownBy(() -> Purchase.purchaseAuctionArt(auctionArt, member, auction.getHighestBidPrice()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ALREADY_SOLD.getMessage());
        }

        @Test
        @DisplayName("사용 가능한 포인트가 부족함에 따라 구매할 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            ReflectionTestUtils.setField(member.getPoint(), "availablePoint", 0);

            // when - then
            assertThatThrownBy(() -> Purchase.purchaseAuctionArt(auctionArt, member, auction.getHighestBidPrice()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("경매 작품을 구매한다")
        void success() {
            // when
            final Purchase purchase = Purchase.purchaseAuctionArt(auctionArt, member, auction.getHighestBidPrice());

            // then
            assertAll(
                    // Purchase Info
                    () -> assertThat(purchase.getArt()).isEqualTo(auctionArt),
                    () -> assertThat(purchase.getArt().isSold()).isTrue(),
                    () -> assertThat(purchase.getBuyer()).isEqualTo(member),
                    () -> assertThat(purchase.getPrice()).isEqualTo(auction.getHighestBidPrice()),

                    // Owner - Buyer
                    () -> assertThat(owner.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT + auction.getHighestBidPrice()),
                    () -> assertThat(owner.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT + auction.getHighestBidPrice()),
                    () -> assertThat(member.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT - auction.getHighestBidPrice()),
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - auction.getHighestBidPrice())
            );
        }
    }
}
