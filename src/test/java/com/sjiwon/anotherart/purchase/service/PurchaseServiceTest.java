package com.sjiwon.anotherart.purchase.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberPointService 테스트")
@RequiredArgsConstructor
class PurchaseServiceTest extends ServiceIntegrateTest {
    private final PurchaseService purchaseService;
    private static final int NOT_ENOUGH_POINT = 10;
    private static final int ENOUGH_POINT = INIT_AVAILABLE_POINT;

    @Nested
    @DisplayName("일반 작품 구매")
    class generalArt {
        @Test
        @DisplayName("작품 소유자는 본인의 작품을 구매할 수 없고 예외가 발생한다")
        void test1() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art generalArt = createGeneralArt(owner);

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(generalArt.getId(), owner.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.INVALID_OWNER_PURCHASE.getMessage());
        }
        
        @Test
        @DisplayName("이미 판매된 작품은 구매할 수 없고 예외가 발생한다 (고의적인 API 호출)")
        void test2() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art generalArt = createGeneralArt(owner);
            makeArtSoldOut(generalArt);

            Member buyer = createMemberB(ENOUGH_POINT);

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(generalArt.getId(), buyer.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ART_ALREADY_SOLD_OUT.getMessage());
        }

        @Test
        @DisplayName("구매자는 포인트가 부족함에 따라 작품을 구매할 수 없고 예외가 발생한다")
        void test3() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art generalArt = createGeneralArt(owner);

            Member buyer = createMemberB(NOT_ENOUGH_POINT);

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(generalArt.getId(), buyer.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.INSUFFICIENT_AVAILABLE_POINT.getMessage());
        }

        @Test
        @DisplayName("일반 작품 구매에 성공한다")
        void test4() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art generalArt = createGeneralArt(owner);

            Member buyer = createMemberB(ENOUGH_POINT);

            // when
            purchaseService.purchaseArt(generalArt.getId(), buyer.getId());

            // then
            List<Purchase> purchases = purchaseRepository.findByBuyerId(buyer.getId());
            assertAll(
                    () -> assertThat(purchases.size()).isEqualTo(1),
                    () -> assertThat(purchases.get(0).getPurchasePrice()).isEqualTo(generalArt.getPrice()),
                    () -> assertThat(purchases.get(0).getArt().getId()).isEqualTo(generalArt.getId()),
                    // 작품 상태 변경
                    () -> assertThat(generalArt.isSoldOut()).isTrue()
            );

            List<PointDetail> ownerPointDetails = pointDetailRepository.findByMemberId(owner.getId());
            assertAll(
                    () -> assertThat(ownerPointDetails.size()).isEqualTo(2),
                    // 포인트 충전
                    () -> assertThat(ownerPointDetails.get(0).getPointType()).isEqualTo(PointType.CHARGE),
                    () -> assertThat(ownerPointDetails.get(0).getAmount()).isEqualTo(ENOUGH_POINT),
                    // 작품 판매
                    () -> assertThat(ownerPointDetails.get(1).getPointType()).isEqualTo(PointType.SOLD),
                    () -> assertThat(ownerPointDetails.get(1).getAmount()).isEqualTo(generalArt.getPrice()),
                    // 판매자 포인트
                    () -> assertThat(owner.getAvailablePoint()).isEqualTo(ENOUGH_POINT + generalArt.getPrice()),
                    () -> assertThat(memberRepository.getTotalPointsByMemberId(owner.getId())).isEqualTo(ENOUGH_POINT + generalArt.getPrice())
            );

            List<PointDetail> buyerPointDetails = pointDetailRepository.findByMemberId(buyer.getId());
            assertAll(
                    () -> assertThat(buyerPointDetails.size()).isEqualTo(2),
                    // 포인트 충전
                    () -> assertThat(buyerPointDetails.get(0).getPointType()).isEqualTo(PointType.CHARGE),
                    () -> assertThat(buyerPointDetails.get(0).getAmount()).isEqualTo(ENOUGH_POINT),
                    // 작품 구매
                    () -> assertThat(buyerPointDetails.get(1).getPointType()).isEqualTo(PointType.PURCHASE),
                    () -> assertThat(buyerPointDetails.get(1).getAmount()).isEqualTo(generalArt.getPrice()),
                    // 구매자 포인트
                    () -> assertThat(buyer.getAvailablePoint()).isEqualTo(ENOUGH_POINT - generalArt.getPrice()),
                    () -> assertThat(memberRepository.getTotalPointsByMemberId(buyer.getId())).isEqualTo(ENOUGH_POINT - generalArt.getPrice())
            );
        }
    }

    @Nested
    @DisplayName("경매 작품 구매")
    class auctionArt {
        @Test
        @DisplayName("작품 소유자는 본인의 작품을 구매할 수 없고 예외가 발생한다")
        void test1() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime1DayAgo);

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(auctionArt.getId(), owner.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.INVALID_OWNER_PURCHASE.getMessage());
        }

        @Test
        @DisplayName("현재 진행중인 경매 작품에 대해서는 구매 요청을 할 수 없고 예외가 발생한다")
        void test2() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

            Member bidder = createMemberB(ENOUGH_POINT);
            final int bidAmount = auction.getBidAmount() + 5_000;
            proceedingBid(auction, bidder, bidAmount);

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(auctionArt.getId(), bidder.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.AUCTION_NOT_FINISHED.getMessage());
        }
        
        @Test
        @DisplayName("최고 입찰자가 아닌 사용자는 경매 작품에 대한 최종 구매 요청을 할 수 없고 예외가 발생한다")
        void test3() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

            Member bidder = createMemberB(ENOUGH_POINT);
            final int bidAmount = auction.getBidAmount() + 5_000;
            proceedingBid(auction, bidder, bidAmount);
            terminateAuction(auction);

            Member anonymous = createMemberC(ENOUGH_POINT);

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(auctionArt.getId(), anonymous.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.INVALID_HIGHEST_BIDDER.getMessage());
        }

        @Test
        @DisplayName("이미 판매된 작품은 구매할 수 없고 예외가 발생한다 (고의적인 API 호출)")
        void test4() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

            Member bidder = createMemberB(ENOUGH_POINT);
            final int bidAmount = auction.getBidAmount() + 5_000;
            proceedingBid(auction, bidder, bidAmount);
            terminateAuction(auction);
            makeArtSoldOut(auctionArt);

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(auctionArt.getId(), bidder.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ART_ALREADY_SOLD_OUT.getMessage());
        }

        @Test
        @DisplayName("구매자는 포인트가 부족함에 따라 작품을 구매할 수 없고 예외가 발생한다")
        void test5() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

            Member bidder = createMemberB(ENOUGH_POINT);
            final int bidAmount = auction.getBidAmount() + 5_000;
            proceedingBid(auction, bidder, bidAmount);
            terminateAuction(auction);
            forceDecreaseBidderPoint(bidder);

            // when - then
            assertThatThrownBy(() -> purchaseService.purchaseArt(auctionArt.getId(), bidder.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.INSUFFICIENT_AVAILABLE_POINT.getMessage());
        }

        @Test
        @DisplayName("경매 작품 최종 구매에 성공한다")
        void test6() {
            // given
            Member owner = createMemberA(ENOUGH_POINT);
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt, currentTime3DayAgo, currentTime3DayLater);

            Member bidder = createMemberB(ENOUGH_POINT);
            final int bidAmount = auction.getBidAmount() + 5_000;
            proceedingBid(auction, bidder, bidAmount);
            terminateAuction(auction);

            // when
            purchaseService.purchaseArt(auctionArt.getId(), bidder.getId());

            // then
            List<Purchase> purchases = purchaseRepository.findByBuyerId(bidder.getId());
            assertAll(
                    () -> assertThat(purchases.size()).isEqualTo(1),
                    () -> assertThat(purchases.get(0).getPurchasePrice()).isEqualTo(auction.getBidAmount()),
                    () -> assertThat(purchases.get(0).getArt().getId()).isEqualTo(auctionArt.getId()),
                    // 작품 상태 변경
                    () -> assertThat(auctionArt.isSoldOut()).isTrue()
            );

            List<PointDetail> ownerPointDetails = pointDetailRepository.findByMemberId(owner.getId());
            assertAll(
                    () -> assertThat(ownerPointDetails.size()).isEqualTo(2),
                    // 포인트 충전
                    () -> assertThat(ownerPointDetails.get(0).getPointType()).isEqualTo(PointType.CHARGE),
                    () -> assertThat(ownerPointDetails.get(0).getAmount()).isEqualTo(ENOUGH_POINT),
                    // 작품 판매
                    () -> assertThat(ownerPointDetails.get(1).getPointType()).isEqualTo(PointType.SOLD),
                    () -> assertThat(ownerPointDetails.get(1).getAmount()).isEqualTo(auction.getBidAmount()),
                    // 판매자 포인트
                    () -> assertThat(owner.getAvailablePoint()).isEqualTo(ENOUGH_POINT + auction.getBidAmount()),
                    () -> assertThat(memberRepository.getTotalPointsByMemberId(owner.getId())).isEqualTo(ENOUGH_POINT + auction.getBidAmount())
            );

            List<PointDetail> buyerPointDetails = pointDetailRepository.findByMemberId(bidder.getId());
            assertAll(
                    () -> assertThat(buyerPointDetails.size()).isEqualTo(2),
                    // 포인트 충전
                    () -> assertThat(buyerPointDetails.get(0).getPointType()).isEqualTo(PointType.CHARGE),
                    () -> assertThat(buyerPointDetails.get(0).getAmount()).isEqualTo(ENOUGH_POINT),
                    // 작품 구매
                    () -> assertThat(buyerPointDetails.get(1).getPointType()).isEqualTo(PointType.PURCHASE),
                    () -> assertThat(buyerPointDetails.get(1).getAmount()).isEqualTo(auction.getBidAmount()),
                    // 구매자 포인트
                    () -> assertThat(bidder.getAvailablePoint()).isEqualTo(ENOUGH_POINT - auction.getBidAmount()),
                    () -> assertThat(memberRepository.getTotalPointsByMemberId(bidder.getId())).isEqualTo(ENOUGH_POINT - auction.getBidAmount())
            );
        }
    }

    private void makeArtSoldOut(Art art) {
        art.changeArtStatus(ArtStatus.SOLD_OUT);
    }

    private void proceedingBid(Auction auction, Member bidder, int bidAmount) {
        auction.applyNewBid(bidder, bidAmount);
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, bidder, bidAmount));
    }

    private void terminateAuction(Auction auction) {
        ReflectionTestUtils.setField(auction.getPeriod(), "startDate", currentTime3DayAgo);
        ReflectionTestUtils.setField(auction.getPeriod(), "endDate", currentTime1DayAgo);
    }

    private void forceDecreaseBidderPoint(Member bidder) {
        bidder.decreasePoint(bidder.getAvailablePoint());
    }

    private Member createMemberA(int chargeAmount) {
        Member member = memberRepository.save(MemberFixture.A.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
        return member;
    }

    private Member createMemberB(int chargeAmount) {
        Member member = memberRepository.save(MemberFixture.B.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
        return member;
    }

    private Member createMemberC(int chargeAmount) {
        Member member = memberRepository.save(MemberFixture.C.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
        return member;
    }

    private Art createGeneralArt(Member owner) {
        return artRepository.save(ArtFixture.B.toArt(owner, HASHTAGS));
    }

    private Art createAuctionArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art, LocalDateTime startDate, LocalDateTime endDate) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(startDate, endDate)));
    }
}