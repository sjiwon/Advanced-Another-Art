package com.sjiwon.anotherart.purchase.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.UseCaseTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.point.domain.repository.PointRecordRepository;
import com.sjiwon.anotherart.purchase.application.usecase.command.PurchaseArtCommand;
import com.sjiwon.anotherart.purchase.domain.model.Purchase;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.CLOSED_WEEK_1_AGO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Purchase -> PurchaseArtUseCase 테스트")
class PurchaseArtUseCaseTest extends UseCaseTest {
    private final ArtRepository artRepository = mock(ArtRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final AuctionRepository auctionRepository = mock(AuctionRepository.class);
    private final PointRecordRepository pointRecordRepository = mock(PointRecordRepository.class);
    private final PurchaseRepository purchaseRepository = mock(PurchaseRepository.class);
    private final PurchaseArtUseCase sut = new PurchaseArtUseCase(
            artRepository,
            memberRepository,
            auctionRepository,
            pointRecordRepository,
            purchaseRepository
    );

    private static final int MEMBER_INIT_POINT = 1_000_000;
    private Member owner;
    private Member buyer;
    private Member anonymous;
    private Art generalArt;
    private Art auctionArt;

    @BeforeEach
    void setUp() {
        owner = MEMBER_A.toMember().apply(1L);
        owner.increaseTotalPoint(MEMBER_INIT_POINT);

        buyer = MEMBER_B.toMember().apply(2L);
        buyer.increaseTotalPoint(MEMBER_INIT_POINT);

        anonymous = MEMBER_C.toMember().apply(3L);
        anonymous.increaseTotalPoint(MEMBER_INIT_POINT);

        generalArt = GENERAL_1.toArt(owner).apply(1L);
        auctionArt = AUCTION_1.toArt(owner).apply(1L);
    }

    @Nested
    @DisplayName("일반 작품 구매")
    class PurchaseGeneralArt {
        @Test
        @DisplayName("본인 작품은 구매할 수 없다")
        void throwExceptionByArtOwnerCannotPurchaseOwn() {
            // given
            final PurchaseArtCommand command = new PurchaseArtCommand(owner.getId(), generalArt.getId());
            given(artRepository.getByIdWithFetchOwner(command.artId())).willReturn(generalArt);
            given(memberRepository.getById(command.memberId())).willReturn(owner);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ART_OWNER_CANNOT_PURCHASE_OWN.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getByIdWithFetchOwner(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(auctionRepository, times(0)).getByArtId(command.artId()),
                    () -> verify(purchaseRepository, times(0)).save(any(Purchase.class)),
                    () -> verify(pointRecordRepository, times(0)).saveAll(any())
            );
        }

        @Test
        @DisplayName("판매중이 아니라면 구매할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            generalArt.closeSale();

            final PurchaseArtCommand command = new PurchaseArtCommand(buyer.getId(), generalArt.getId());
            given(artRepository.getByIdWithFetchOwner(command.artId())).willReturn(generalArt);
            given(memberRepository.getById(command.memberId())).willReturn(buyer);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ALREADY_SOLD.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getByIdWithFetchOwner(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(auctionRepository, times(0)).getByArtId(command.artId()),
                    () -> verify(purchaseRepository, times(0)).save(any(Purchase.class)),
                    () -> verify(pointRecordRepository, times(0)).saveAll(any())
            );
        }

        @Test
        @DisplayName("사용 가능한 포인트가 부족함에 따라 구매할 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            buyer.decreaseAvailablePoint(MEMBER_INIT_POINT);

            final PurchaseArtCommand command = new PurchaseArtCommand(buyer.getId(), generalArt.getId());
            given(artRepository.getByIdWithFetchOwner(command.artId())).willReturn(generalArt);
            given(memberRepository.getById(command.memberId())).willReturn(buyer);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.POINT_IS_NOT_ENOUGH.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getByIdWithFetchOwner(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(auctionRepository, times(0)).getByArtId(command.artId()),
                    () -> verify(purchaseRepository, times(0)).save(any(Purchase.class)),
                    () -> verify(pointRecordRepository, times(0)).saveAll(any())
            );
        }

        @Test
        @DisplayName("일반 작품을 구매한다")
        void success() {
            // given
            final PurchaseArtCommand command = new PurchaseArtCommand(buyer.getId(), generalArt.getId());
            given(artRepository.getByIdWithFetchOwner(command.artId())).willReturn(generalArt);
            given(memberRepository.getById(command.memberId())).willReturn(buyer);

            // when
            sut.invoke(command);

            // then
            assertAll(
                    // Purchase Progress
                    () -> verify(artRepository, times(1)).getByIdWithFetchOwner(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(auctionRepository, times(0)).getByArtId(command.artId()),
                    () -> verify(purchaseRepository, times(1)).save(any(Purchase.class)),
                    () -> verify(pointRecordRepository, times(1)).saveAll(any()),

                    // Owner & buyer
                    () -> assertThat(owner.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT + generalArt.getPrice()),
                    () -> assertThat(owner.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT + generalArt.getPrice()),
                    () -> assertThat(buyer.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT - generalArt.getPrice()),
                    () -> assertThat(buyer.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - generalArt.getPrice())
            );
        }
    }

    @Nested
    @DisplayName("경매 작품 구매")
    class PurchaseAuctionArt {
        private Auction closedAuction;
        private Auction inProgressAuction;
        private int bidPrice;

        @BeforeEach
        void setUp() {
            bidPrice = auctionArt.getPrice() + 50_000;

            closedAuction = AUCTION_OPEN_NOW.toAuction(auctionArt).apply(1L);
            closedAuction.applyNewBid(buyer, bidPrice);
            closeAuction();

            inProgressAuction = AUCTION_OPEN_NOW.toAuction(auctionArt).apply(2L);
        }

        private void closeAuction() {
            ReflectionTestUtils.setField(closedAuction.getPeriod(), "startDate", CLOSED_WEEK_1_AGO.getStartDate());
            ReflectionTestUtils.setField(closedAuction.getPeriod(), "endDate", CLOSED_WEEK_1_AGO.getEndDate());
        }

        @Test
        @DisplayName("경매가 종료되지 않았다면 구매할 수 없다")
        void throwExceptionByAuctionNotFinished() {
            // given
            final PurchaseArtCommand command = new PurchaseArtCommand(buyer.getId(), auctionArt.getId());
            given(artRepository.getByIdWithFetchOwner(command.artId())).willReturn(auctionArt);
            given(memberRepository.getById(command.memberId())).willReturn(buyer);
            given(auctionRepository.getByArtId(auctionArt.getId())).willReturn(inProgressAuction);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.AUCTION_NOT_FINISHED.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getByIdWithFetchOwner(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(auctionRepository, times(1)).getByArtId(command.artId()),
                    () -> verify(purchaseRepository, times(0)).save(any(Purchase.class)),
                    () -> verify(pointRecordRepository, times(0)).saveAll(any())
            );
        }

        @Test
        @DisplayName("낙찰자가 아니면 구매할 수 없다")
        void throwExceptionByBuyerIsNotHighestBidder() {
            // given
            final PurchaseArtCommand command = new PurchaseArtCommand(anonymous.getId(), auctionArt.getId());
            given(artRepository.getByIdWithFetchOwner(command.artId())).willReturn(auctionArt);
            given(memberRepository.getById(command.memberId())).willReturn(anonymous);
            given(auctionRepository.getByArtId(auctionArt.getId())).willReturn(closedAuction);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.BUYER_IS_NOT_HIGHEST_BIDDER.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getByIdWithFetchOwner(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(auctionRepository, times(1)).getByArtId(command.artId()),
                    () -> verify(purchaseRepository, times(0)).save(any(Purchase.class)),
                    () -> verify(pointRecordRepository, times(0)).saveAll(any())
            );
        }

        @Test
        @DisplayName("판매중이 아니라면 구매할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            auctionArt.closeSale();

            final PurchaseArtCommand command = new PurchaseArtCommand(buyer.getId(), auctionArt.getId());
            given(artRepository.getByIdWithFetchOwner(command.artId())).willReturn(auctionArt);
            given(memberRepository.getById(command.memberId())).willReturn(buyer);
            given(auctionRepository.getByArtId(auctionArt.getId())).willReturn(closedAuction);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(PurchaseErrorCode.ALREADY_SOLD.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getByIdWithFetchOwner(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(auctionRepository, times(1)).getByArtId(command.artId()),
                    () -> verify(purchaseRepository, times(0)).save(any(Purchase.class)),
                    () -> verify(pointRecordRepository, times(0)).saveAll(any())
            );
        }

        @Test
        @DisplayName("사용 가능한 포인트가 부족함에 따라 구매할 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            buyer.decreaseAvailablePoint(MEMBER_INIT_POINT - bidPrice);

            final PurchaseArtCommand command = new PurchaseArtCommand(buyer.getId(), auctionArt.getId());
            given(artRepository.getByIdWithFetchOwner(command.artId())).willReturn(auctionArt);
            given(memberRepository.getById(command.memberId())).willReturn(buyer);
            given(auctionRepository.getByArtId(auctionArt.getId())).willReturn(closedAuction);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.POINT_IS_NOT_ENOUGH.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getByIdWithFetchOwner(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(auctionRepository, times(1)).getByArtId(command.artId()),
                    () -> verify(purchaseRepository, times(0)).save(any(Purchase.class)),
                    () -> verify(pointRecordRepository, times(0)).saveAll(any())
            );
        }

        @Test
        @DisplayName("경매 작품을 구매한다")
        void success() {
            // given
            final PurchaseArtCommand command = new PurchaseArtCommand(buyer.getId(), auctionArt.getId());
            given(artRepository.getByIdWithFetchOwner(command.artId())).willReturn(auctionArt);
            given(memberRepository.getById(command.memberId())).willReturn(buyer);
            given(auctionRepository.getByArtId(auctionArt.getId())).willReturn(closedAuction);

            // when
            sut.invoke(command);

            // then
            assertAll(
                    // Purchase Progress
                    () -> verify(artRepository, times(1)).getByIdWithFetchOwner(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(auctionRepository, times(1)).getByArtId(command.artId()),
                    () -> verify(purchaseRepository, times(1)).save(any(Purchase.class)),
                    () -> verify(pointRecordRepository, times(1)).saveAll(any()),

                    // Owner & buyer
                    () -> assertThat(owner.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT + bidPrice),
                    () -> assertThat(owner.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT + bidPrice),
                    () -> assertThat(buyer.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT - bidPrice),
                    () -> assertThat(buyer.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - bidPrice)
            );
        }
    }
}
