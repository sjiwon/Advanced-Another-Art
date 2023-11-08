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

import java.util.List;
import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_NOW;
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
        bidder = memberRepository.save(MEMBER_A.toMember());
        bidder.increaseTotalPoint(100_000_000);

        final Member owner = memberRepository.save(MEMBER_B.toMember());
        artA = artRepository.save(AUCTION_1.toArt(owner));
        artB = artRepository.save(AUCTION_2.toArt(owner));
    }

    @Test
    @DisplayName("작품 ID로 경매 정보를 조회한다")
    void findByArtId() {
        // given
        sut.save(AUCTION_OPEN_NOW.toAuction(artA));

        // when
        final Optional<Auction> auction1 = sut.findByArtId(artA.getId());
        final Optional<Auction> auction2 = sut.findByArtId(artB.getId());

        // then
        assertAll(
                () -> assertThat(auction1).isPresent(),
                () -> assertThat(auction2).isEmpty()
        );
    }

    @Test
    @DisplayName("작품 ID를 통해서 최근 입찰자 ID를 조회하고 입찰 레코드가 존재하는지 확인한다")
    void getBidderIdByArtId_and_isBidRecordExists() {
        final Auction auctionA = sut.save(AUCTION_OPEN_NOW.toAuction(artA));
        final Auction auctionB = sut.save(AUCTION_OPEN_NOW.toAuction(artB));

        /* auctionA 입찰 */
        auctionA.applyNewBid(bidder, auctionA.getHighestBidPrice() + 50_000);

        final Long bidderIdA1 = sut.getBidderIdByArtId(artA.getId());
        final boolean existsA1 = sut.isBidRecordExists(artA.getId());
        final Long bidderIdB1 = sut.getBidderIdByArtId(artB.getId());
        final boolean existsB1 = sut.isBidRecordExists(artB.getId());

        assertAll(
                () -> assertThat(bidderIdA1).isEqualTo(bidder.getId()),
                () -> assertThat(existsA1).isTrue(),
                () -> assertThat(bidderIdB1).isNull(),
                () -> assertThat(existsB1).isFalse()
        );

        /* auctionB 입찰 */
        auctionB.applyNewBid(bidder, auctionB.getHighestBidPrice() + 50_000);

        final Long bidderIdA2 = sut.getBidderIdByArtId(artA.getId());
        final boolean existsA2 = sut.isBidRecordExists(artA.getId());
        final Long bidderIdB2 = sut.getBidderIdByArtId(artB.getId());
        final boolean existsB2 = sut.isBidRecordExists(artB.getId());

        assertAll(
                () -> assertThat(bidderIdA2).isEqualTo(bidder.getId()),
                () -> assertThat(existsA2).isTrue(),
                () -> assertThat(bidderIdB2).isEqualTo(bidder.getId()),
                () -> assertThat(existsB2).isTrue()
        );
    }

    @Test
    @DisplayName("작품 ID를 통해서 경매 정보를 삭제한다")
    void deleteByArtId() {
        sut.saveAll(List.of(
                AUCTION_OPEN_NOW.toAuction(artA),
                AUCTION_OPEN_NOW.toAuction(artB)
        ));
        assertThat(sut.findAll()).hasSize(2);

        /* artA에 대한 Auction 제거 */
        sut.deleteByArtId(artA.getId());
        assertThat(sut.findAll()).hasSize(1);

        /* artB에 대한 Auction 제거 */
        sut.deleteByArtId(artB.getId());
        assertThat(sut.findAll()).hasSize(0);
    }
}
