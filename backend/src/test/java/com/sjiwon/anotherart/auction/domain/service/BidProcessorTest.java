package com.sjiwon.anotherart.auction.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.model.AuctionRecord;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_현재_진행;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("Auction -> BidProcessor 테스트")
class BidProcessorTest extends UnitTest {
    private final BidProcessor sut = new BidProcessor(
            new MemberReader(memberRepository),
            new AuctionWriter(auctionRepository, auctionRecordRepository)
    );

    private static final int INIT_POINT = 1_000_000_000;

    private Member bidderA;
    private Member bidderB;
    private Art art;
    private Auction auction;

    @BeforeEach
    void setUp() {
        final Member owner = MEMBER_A.toDomain(INIT_POINT).apply(1L);
        bidderA = MEMBER_B.toDomain(INIT_POINT).apply(2L);
        bidderB = MEMBER_C.toDomain(INIT_POINT).apply(3L);
        art = AUCTION_1.toDomain(owner).apply(1L);
        auction = 경매_현재_진행.toDomain(art).apply(1L);
    }

    @Test
    @DisplayName("입찰 프로세스를 진행한다 [최고 입찰자 X]")
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

        final int newBidPrice = auction.getHighestBidPrice() + 5_000;

        // when
        sut.execute(auction, bidderA, newBidPrice);

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
    @DisplayName("입찰 프로세스를 진행한다 [최고 입찰자 O]")
    void executeB() {
        // given
        final int previousBidPrice = auction.getHighestBidPrice() + 5_000;
        sut.execute(auction, bidderA, previousBidPrice);
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
        given(memberRepository.findById(auction.getHighestBidderId())).willReturn(Optional.of(bidderA));

        // when
        sut.execute(auction, bidderB, newBidPrice);

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
