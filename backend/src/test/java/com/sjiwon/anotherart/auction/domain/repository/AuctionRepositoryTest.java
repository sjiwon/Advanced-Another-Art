package com.sjiwon.anotherart.auction.domain.repository;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_현재_진행;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction -> AuctionRepository 테스트")
class AuctionRepositoryTest extends RepositoryTest {
    @Autowired
    private AuctionRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    private Member bidder;
    private Art artA;
    private Art artB;

    @BeforeEach
    void setUp() {
        bidder = memberRepository.save(MEMBER_A.toDomain());
        bidder.increaseTotalPoint(100_000_000);

        final Member owner = memberRepository.save(MEMBER_B.toDomain());
        artA = artRepository.save(AUCTION_1.toDomain(owner));
        artB = artRepository.save(AUCTION_2.toDomain(owner));
    }

    @Test
    @DisplayName("작품 & 작품 ID로 경매 ID를 조회한다")
    void findxxxByArtId() {
        // given
        final Auction auction1 = sut.save(경매_현재_진행.toDomain(artA));
        final Auction auction2 = sut.save(경매_현재_진행.toDomain(artB));

        // when
        final Long actual1 = sut.findIdByArtId(artA.getId());
        final Optional<Auction> actual2 = sut.findByArtId(artA.getId());
        final Long actual3 = sut.findIdByArtId(artB.getId());
        final Optional<Auction> actual4 = sut.findByArtId(artB.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isEqualTo(auction1.getId()),
                () -> assertThat(actual2.get()).isEqualTo(auction1),
                () -> assertThat(actual3).isEqualTo(auction2.getId()),
                () -> assertThat(actual4.get()).isEqualTo(auction2)
        );
    }

    @Test
    @DisplayName("최고 입찰자 ID를 조회한다")
    void findHighestBidderIdByArtId() {
        final Auction auctionA = sut.save(경매_현재_진행.toDomain(artA));
        final Auction auctionB = sut.save(경매_현재_진행.toDomain(artB));

        /* auctionA 입찰 */
        auctionA.updateHighestBid(bidder, auctionA.getHighestBidPrice() + 50_000);
        assertAll(
                () -> assertThat(sut.findHighestBidderIdByArtId(artA.getId())).isEqualTo(bidder.getId()),
                () -> assertThat(sut.findHighestBidderIdByArtId(artB.getId())).isNull()
        );

        /* auctionB 입찰 */
        auctionB.updateHighestBid(bidder, auctionB.getHighestBidPrice() + 50_000);
        assertAll(
                () -> assertThat(sut.findHighestBidderIdByArtId(artA.getId())).isEqualTo(bidder.getId()),
                () -> assertThat(sut.findHighestBidderIdByArtId(artB.getId())).isEqualTo(bidder.getId())
        );
    }
}
