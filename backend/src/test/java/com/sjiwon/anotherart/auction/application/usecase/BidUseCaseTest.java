package com.sjiwon.anotherart.auction.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.auction.application.usecase.command.BidCommand;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.model.AuctionRecord;
import com.sjiwon.anotherart.auction.domain.service.AuctionReader;
import com.sjiwon.anotherart.auction.domain.service.AuctionWriter;
import com.sjiwon.anotherart.auction.domain.service.BidInspector;
import com.sjiwon.anotherart.auction.domain.service.BidProcessor;
import com.sjiwon.anotherart.auction.exception.AuctionException;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("Auction -> BidUseCase 테스트")
class BidUseCaseTest extends UnitTest {
    private final BidUseCase sut = new BidUseCase(
            new AuctionReader(auctionRepository),
            new ArtReader(artRepository),
            new MemberReader(memberRepository),
            new BidInspector(),
            new BidProcessor(
                    new MemberReader(memberRepository),
                    new AuctionWriter(auctionRepository, auctionRecordRepository)
            )
    );

    private static final int INIT_POINT = 1_000_000_000;

    private Member owner;
    private Member bidderA;
    private Member bidderB;
    private Art art;
    private Auction auction;

    @BeforeEach
    void setUp() {
        owner = MEMBER_A.toDomain(INIT_POINT).apply(1L);
        bidderA = MEMBER_B.toDomain(INIT_POINT).apply(2L);
        bidderB = MEMBER_C.toDomain(INIT_POINT).apply(3L);
        art = AUCTION_1.toDomain(owner).apply(1L);
        auction = 경매_현재_진행.toDomain(art).apply(1L);
    }

    @Test
    @DisplayName("1. 작품 소유자는 입찰을 시도할 수 없다")
    void throwExceptionByArtOwnerCannotBid() {
        // given
        final int newBidPrice = auction.getHighestBidPrice();
        final BidCommand command = new BidCommand(
                owner.getId(),
                auction.getId(),
                newBidPrice
        );
        given(auctionRepository.findByIdWithRecords(command.auctionId())).willReturn(Optional.of(auction));
        given(artRepository.findById(auction.getArtId())).willReturn(Optional.of(art));
        given(memberRepository.findById(command.memberId())).willReturn(Optional.of(owner));

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AuctionException.class)
                .hasMessage(ART_OWNER_CANNOT_BID.getMessage());
    }

    @Test
    @DisplayName("2-1. 경매 진행 기간이 아니면 입찰을 진행할 수 없다 [이미 종료]")
    void throwExceptionByAuctionIsNotInProgressA() {
        // given
        final Auction auction = 경매_1주전_종료.toDomain(art).apply(1L);
        final int newBidPrice = auction.getHighestBidPrice();
        final BidCommand command = new BidCommand(
                bidderA.getId(),
                auction.getId(),
                newBidPrice
        );
        given(auctionRepository.findByIdWithRecords(command.auctionId())).willReturn(Optional.of(auction));
        given(artRepository.findById(auction.getArtId())).willReturn(Optional.of(art));
        given(memberRepository.findById(command.memberId())).willReturn(Optional.of(bidderA));

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AuctionException.class)
                .hasMessage(AUCTION_IS_NOT_IN_PROGRESS.getMessage());
    }

    @Test
    @DisplayName("2-2. 경매 진행 기간이 아니면 입찰을 진행할 수 없다 [아직 시작 X]")
    void throwExceptionByAuctionIsNotInProgressB() {
        // given
        final Auction auction = 경매_1주뒤_오픈.toDomain(art).apply(1L);
        final int newBidPrice = auction.getHighestBidPrice();
        final BidCommand command = new BidCommand(
                bidderA.getId(),
                auction.getId(),
                newBidPrice
        );
        given(auctionRepository.findByIdWithRecords(command.auctionId())).willReturn(Optional.of(auction));
        given(artRepository.findById(auction.getArtId())).willReturn(Optional.of(art));
        given(memberRepository.findById(command.memberId())).willReturn(Optional.of(bidderA));

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AuctionException.class)
                .hasMessage(AUCTION_IS_NOT_IN_PROGRESS.getMessage());
    }

    @Test
    @DisplayName("3. 최고 입찰자는 연속으로 입찰을 진행할 수 없다")
    void throwExceptionByHighestBidderCannotBidAgain() {
        // given
        auction.updateHighestBid(bidderA, auction.getHighestBidPrice());

        final int newBidPrice = auction.getHighestBidPrice() + 5_000;
        final BidCommand command = new BidCommand(
                bidderA.getId(),
                auction.getId(),
                newBidPrice
        );
        given(auctionRepository.findByIdWithRecords(command.auctionId())).willReturn(Optional.of(auction));
        given(artRepository.findById(auction.getArtId())).willReturn(Optional.of(art));
        given(memberRepository.findById(command.memberId())).willReturn(Optional.of(bidderA));

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AuctionException.class)
                .hasMessage(HIGHEST_BIDDER_CANNOT_BID_AGAIN.getMessage());
    }

    @Test
    @DisplayName("4-1. 입찰가가 부족하면 입찰을 진행할 수 없다 [최고 입찰자 존재 X]")
    void throwExceptionByBidPriceIsNotEnoughA() {
        // given
        final int newBidPrice = auction.getHighestBidPrice() - 5_000;
        final BidCommand command = new BidCommand(
                bidderA.getId(),
                auction.getId(),
                newBidPrice
        );
        given(auctionRepository.findByIdWithRecords(command.auctionId())).willReturn(Optional.of(auction));
        given(artRepository.findById(auction.getArtId())).willReturn(Optional.of(art));
        given(memberRepository.findById(command.memberId())).willReturn(Optional.of(bidderA));

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AuctionException.class)
                .hasMessage(BID_PRICE_IS_NOT_ENOUGH.getMessage());
    }

    @Test
    @DisplayName("4-2. 입찰가가 부족하면 입찰을 진행할 수 없다 [최고 입찰자 존재 O - 낮은 입찰가]")
    void throwExceptionByBidPriceIsNotEnoughB() {
        // given
        auction.updateHighestBid(bidderA, auction.getHighestBidPrice());

        final int newBidPrice = auction.getHighestBidPrice() - 5_000;
        final BidCommand command = new BidCommand(
                bidderB.getId(),
                auction.getId(),
                newBidPrice
        );
        given(auctionRepository.findByIdWithRecords(command.auctionId())).willReturn(Optional.of(auction));
        given(artRepository.findById(auction.getArtId())).willReturn(Optional.of(art));
        given(memberRepository.findById(command.memberId())).willReturn(Optional.of(bidderB));

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AuctionException.class)
                .hasMessage(BID_PRICE_IS_NOT_ENOUGH.getMessage());
    }

    @Test
    @DisplayName("4-3. 입찰가가 부족하면 입찰을 진행할 수 없다 [최고 입찰자 존재 O - 동일 입찰가]")
    void throwExceptionByBidPriceIsNotEnoughC() {
        // given
        auction.updateHighestBid(bidderA, auction.getHighestBidPrice());

        final int newBidPrice = auction.getHighestBidPrice();
        final BidCommand command = new BidCommand(
                bidderB.getId(),
                auction.getId(),
                newBidPrice
        );
        given(auctionRepository.findByIdWithRecords(command.auctionId())).willReturn(Optional.of(auction));
        given(artRepository.findById(auction.getArtId())).willReturn(Optional.of(art));
        given(memberRepository.findById(command.memberId())).willReturn(Optional.of(bidderB));

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AuctionException.class)
                .hasMessage(BID_PRICE_IS_NOT_ENOUGH.getMessage());
    }

    @Test
    @DisplayName("5-1. 입찰 프로세스를 진행한다 [최고 입찰자 X]")
    void executeA() {
        // given
        assertAll(
                // Auction - Record
                () -> assertThat(auction.getHighestBidderId()).isNull(),
                () -> assertThat(auction.getHighestBidPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(auction.getAuctionRecords()).hasSize(0),

                // Bidders
                () -> assertThat(bidderA.getTotalPoint()).isEqualTo(INIT_POINT),
                () -> assertThat(bidderA.getAvailablePoint()).isEqualTo(INIT_POINT)
        );

        final int newBidPrice = auction.getHighestBidPrice();
        final BidCommand command = new BidCommand(
                bidderA.getId(),
                auction.getId(),
                newBidPrice
        );
        given(auctionRepository.findByIdWithRecords(command.auctionId())).willReturn(Optional.of(auction));
        given(artRepository.findById(auction.getArtId())).willReturn(Optional.of(art));
        given(memberRepository.findById(command.memberId())).willReturn(Optional.of(bidderA));

        // when
        sut.invoke(command);

        // then
        assertAll(
                // Auction - Record
                () -> assertThat(auction.getHighestBidderId()).isEqualTo(bidderA.getId()),
                () -> assertThat(auction.getHighestBidPrice()).isEqualTo(newBidPrice),
                () -> assertThat(auction.getAuctionRecords()).hasSize(1),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidderId)
                        .containsExactly(bidderA.getId()),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidPrice)
                        .containsExactly(newBidPrice),

                // Bidders
                () -> assertThat(bidderA.getTotalPoint()).isEqualTo(INIT_POINT),
                () -> assertThat(bidderA.getAvailablePoint()).isEqualTo(INIT_POINT - newBidPrice)
        );
    }

    @Test
    @DisplayName("5-2. 입찰 프로세스를 진행한다 [최고 입찰자 O]")
    void executeB() {
        // given
        final int previousBidPrice = auction.getHighestBidPrice() + 5_000;
        new BidProcessor(
                new MemberReader(memberRepository),
                new AuctionWriter(auctionRepository, auctionRecordRepository)
        ).execute(auction, bidderA, previousBidPrice);
        assertAll(
                // Auction - Record
                () -> assertThat(auction.getHighestBidderId()).isEqualTo(bidderA.getId()),
                () -> assertThat(auction.getHighestBidPrice()).isEqualTo(previousBidPrice),
                () -> assertThat(auction.getAuctionRecords()).hasSize(1),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidderId)
                        .containsExactly(bidderA.getId()),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidPrice)
                        .containsExactly(previousBidPrice),

                // Bidders
                () -> assertThat(bidderA.getTotalPoint()).isEqualTo(INIT_POINT),
                () -> assertThat(bidderA.getAvailablePoint()).isEqualTo(INIT_POINT - previousBidPrice),
                () -> assertThat(bidderB.getTotalPoint()).isEqualTo(INIT_POINT),
                () -> assertThat(bidderB.getAvailablePoint()).isEqualTo(INIT_POINT)
        );


        final int newBidPrice = previousBidPrice + 5_000;
        final BidCommand command = new BidCommand(
                bidderB.getId(),
                auction.getId(),
                newBidPrice
        );
        given(auctionRepository.findByIdWithRecords(command.auctionId())).willReturn(Optional.of(auction));
        given(artRepository.findById(auction.getArtId())).willReturn(Optional.of(art));
        given(memberRepository.findById(command.memberId())).willReturn(Optional.of(bidderB));
        given(memberRepository.findById(auction.getHighestBidderId())).willReturn(Optional.of(bidderA));

        // when
        sut.invoke(command);

        // then
        assertAll(
                // Auction - Record
                () -> assertThat(auction.getHighestBidderId()).isEqualTo(bidderB.getId()),
                () -> assertThat(auction.getHighestBidPrice()).isEqualTo(newBidPrice),
                () -> assertThat(auction.getAuctionRecords()).hasSize(2),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidderId)
                        .containsExactly(bidderA.getId(), bidderB.getId()),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidPrice)
                        .containsExactly(previousBidPrice, newBidPrice),

                // Bidders
                () -> assertThat(bidderA.getTotalPoint()).isEqualTo(INIT_POINT),
                () -> assertThat(bidderA.getAvailablePoint()).isEqualTo(INIT_POINT),
                () -> assertThat(bidderB.getTotalPoint()).isEqualTo(INIT_POINT),
                () -> assertThat(bidderB.getAvailablePoint()).isEqualTo(INIT_POINT - newBidPrice)
        );
    }
}
