package com.sjiwon.anotherart.auction.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.application.usecase.command.BidCommand;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.model.AuctionRecord;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.UseCaseTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_CLOSED_WEEK_1_AGO;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_WEEK_1_LATER;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Auction -> BidUseCase 테스트")
class BidUseCaseTest extends UseCaseTest {
    private final AuctionRepository auctionRepository = mock(AuctionRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final BidUseCase sut = new BidUseCase(auctionRepository, memberRepository);

    private static final int MEMBER_INIT_POINT = 1_000_000;
    private Member owner;
    private Member memberA;
    private Member memberB;
    private Auction closedAuction;
    private Auction auction;
    private Auction willOpenAuction;

    @BeforeEach
    void setUp() {
        owner = MEMBER_A.toMember().apply(1L);
        owner.increaseTotalPoint(MEMBER_INIT_POINT);

        memberA = MEMBER_B.toMember().apply(2L);
        memberA.increaseTotalPoint(MEMBER_INIT_POINT);

        memberB = MEMBER_C.toMember().apply(3L);
        memberB.increaseTotalPoint(MEMBER_INIT_POINT);

        final Art art = AUCTION_1.toArt(owner).apply(1L);
        closedAuction = AUCTION_CLOSED_WEEK_1_AGO.toAuction(art).apply(1L);
        auction = AUCTION_OPEN_NOW.toAuction(art).apply(2L);
        willOpenAuction = AUCTION_OPEN_WEEK_1_LATER.toAuction(art).apply(3L);
    }

    @Test
    @DisplayName("경매가 진행되고 있지 않으면 입찰을 진행할 수 없다 -> 1) 이미 종료된 경매")
    void throwExceptionByAuctionIsNotInProgessCaseA() {
        // given
        final BidCommand command = new BidCommand(memberA.getId(), closedAuction.getId(), closedAuction.getHighestBidPrice() + 50_000);
        given(auctionRepository.getByIdWithFetchBidder(command.auctionId())).willReturn(closedAuction);
        given(memberRepository.getById(command.memberId())).willReturn(memberA);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.AUCTION_IS_NOT_IN_PROGRESS.getMessage());

        assertAll(
                () -> verify(auctionRepository, times(1)).getByIdWithFetchBidder(command.auctionId()),
                () -> verify(memberRepository, times(1)).getById(command.memberId())
        );
    }

    @Test
    @DisplayName("경매가 진행되고 있지 않으면 입찰을 진행할 수 없다 -> 2) 아직 오픈되지 않은 경매")
    void throwExceptionByAuctionIsNotInProgessCaseB() {
        // given
        final BidCommand command = new BidCommand(memberA.getId(), willOpenAuction.getId(), willOpenAuction.getHighestBidPrice() + 50_000);
        given(auctionRepository.getByIdWithFetchBidder(command.auctionId())).willReturn(willOpenAuction);
        given(memberRepository.getById(command.memberId())).willReturn(memberA);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.AUCTION_IS_NOT_IN_PROGRESS.getMessage());

        assertAll(
                () -> verify(auctionRepository, times(1)).getByIdWithFetchBidder(command.auctionId()),
                () -> verify(memberRepository, times(1)).getById(command.memberId())
        );
    }

    @Test
    @DisplayName("작품 소유자는 입찰을 진행할 수 없다")
    void throwExceptionByArtOwnerCannotBid() {
        // given
        final BidCommand command = new BidCommand(owner.getId(), auction.getId(), auction.getHighestBidPrice() + 50_000);
        given(auctionRepository.getByIdWithFetchBidder(command.auctionId())).willReturn(auction);
        given(memberRepository.getById(command.memberId())).willReturn(owner);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.ART_OWNER_CANNOT_BID.getMessage());

        assertAll(
                () -> verify(auctionRepository, times(1)).getByIdWithFetchBidder(command.auctionId()),
                () -> verify(memberRepository, times(1)).getById(command.memberId())
        );
    }

    @Test
    @DisplayName("최고 입찰자는 연속으로 입찰을 진행할 수 없다")
    void throwExceptionByHighestBidderCannotBidAgain() {
        // given
        final int newBidPrice = auction.getHighestBidPrice() + 50_000;
        auction.applyNewBid(memberA, newBidPrice);

        final BidCommand command = new BidCommand(memberA.getId(), auction.getId(), newBidPrice + 50_000);
        given(auctionRepository.getByIdWithFetchBidder(command.auctionId())).willReturn(auction);
        given(memberRepository.getById(command.memberId())).willReturn(memberA);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN.getMessage());
    }

    @Test
    @DisplayName("입찰 금액이 부족하다면 입찰을 진행할 수 없다 -> 1) 최고 입찰자가 존재하는 경우")
    void throwExceptionByBidPriceIsNotEnoughCaseA() {
        // given
        final int newBidPrice = auction.getHighestBidPrice() + 50_000;
        auction.applyNewBid(memberA, newBidPrice);

        // when - then
        /* 더 적은 금액 */
        final BidCommand commandA = new BidCommand(memberB.getId(), auction.getId(), newBidPrice - 10_000);
        given(auctionRepository.getByIdWithFetchBidder(commandA.auctionId())).willReturn(auction);
        given(memberRepository.getById(commandA.memberId())).willReturn(memberB);

        assertThatThrownBy(() -> sut.invoke(commandA))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());

        /* 동일한 금액 */
        final BidCommand commandB = new BidCommand(memberB.getId(), auction.getId(), newBidPrice);
        given(auctionRepository.getByIdWithFetchBidder(commandB.auctionId())).willReturn(auction);
        given(memberRepository.getById(commandB.memberId())).willReturn(memberB);

        assertThatThrownBy(() -> sut.invoke(commandB)) // 동일한 금액
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
    }

    @Test
    @DisplayName("입찰 금액이 부족하다면 입찰을 진행할 수 없다 -> 2) 최고 입찰자가 존재하지 않는 경우")
    void throwExceptionByBidPriceIsNotEnoughCaseB() {
        // given
        final int newBidPrice = auction.getHighestBidPrice() - 10_000;
        final BidCommand command = new BidCommand(memberB.getId(), auction.getId(), newBidPrice);
        given(auctionRepository.getByIdWithFetchBidder(command.auctionId())).willReturn(auction);
        given(memberRepository.getById(command.memberId())).willReturn(memberB);

        // when -then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH.getMessage());
    }

    @Test
    @DisplayName("입찰을 성공한다 [최고 입찰자 존재 X]")
    void successA() {
        // given
        final int newBidPrice = auction.getHighestBidPrice() + 50_000;
        final BidCommand command = new BidCommand(memberA.getId(), auction.getId(), newBidPrice);
        given(auctionRepository.getByIdWithFetchBidder(command.auctionId())).willReturn(auction);
        given(memberRepository.getById(command.memberId())).willReturn(memberA);

        // when
        sut.invoke(command);

        // then
        assertAll(
                // HighestBid Info
                () -> assertThat(auction.getAuctionRecords()).hasSize(1),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidder)
                        .containsExactly(memberA),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidPrice)
                        .containsExactly(newBidPrice),
                () -> assertThat(auction.getHighestBidder()).isEqualTo(memberA),
                () -> assertThat(auction.getHighestBidPrice()).isEqualTo(newBidPrice),

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
        auction.applyNewBid(memberA, previousBidPrice);

        final int newBidPrice = previousBidPrice + 50_000;
        final BidCommand command = new BidCommand(memberB.getId(), auction.getId(), newBidPrice);
        given(auctionRepository.getByIdWithFetchBidder(command.auctionId())).willReturn(auction);
        given(memberRepository.getById(command.memberId())).willReturn(memberB);

        // when
        sut.invoke(command);

        // then
        assertAll(
                // Bid Info
                () -> assertThat(auction.getAuctionRecords()).hasSize(2),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidder)
                        .containsExactly(memberA, memberB),
                () -> assertThat(auction.getAuctionRecords())
                        .map(AuctionRecord::getBidPrice)
                        .containsExactly(previousBidPrice, newBidPrice),
                () -> assertThat(auction.getHighestBidder()).isEqualTo(memberB),
                () -> assertThat(auction.getHighestBidPrice()).isEqualTo(newBidPrice),

                // Bidders Info
                () -> assertThat(memberA.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                () -> assertThat(memberA.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - previousBidPrice + previousBidPrice),
                () -> assertThat(memberB.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                () -> assertThat(memberB.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - newBidPrice)
        );
    }
}
