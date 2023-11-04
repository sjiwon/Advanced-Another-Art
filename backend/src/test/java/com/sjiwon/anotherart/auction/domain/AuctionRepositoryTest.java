package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction [Repository Layer] -> AuctionRepository 테스트")
class AuctionRepositoryTest extends RepositoryTest {
    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    private Member owner;
    private Art art;
    private Auction auction;

    @BeforeEach
    void setUp() {
        owner = memberRepository.save(MEMBER_A.toMember());
        art = artRepository.save(AUCTION_1.toArt(owner));
        auction = auctionRepository.save(AUCTION_OPEN_NOW.toAuction(art));
    }

    @Test
    @DisplayName("ID(PK)로 경매 정보를 조회한다")
    void findByIdWithPessimisticLock() {
        // when
        final Optional<Auction> emptyAuction = auctionRepository.findByIdWithPessimisticLock(auction.getId() + 10000L);
        final Optional<Auction> existsAuction = auctionRepository.findByIdWithPessimisticLock(auction.getId());

        // then
        assertAll(
                () -> assertThat(emptyAuction).isEmpty(),
                () -> assertThat(existsAuction).isPresent()
        );
        assertThat(existsAuction.get()).isEqualTo(auction);
    }

    @Test
    @DisplayName("작품 ID로 경매 정보를 조회한다")
    void findByArtId() {
        // when
        final Optional<Auction> emptyAuction = auctionRepository.findByArtId(art.getId() + 10000L);
        final Optional<Auction> existsAuction = auctionRepository.findByArtId(art.getId());

        // then
        assertAll(
                () -> assertThat(emptyAuction).isEmpty(),
                () -> assertThat(existsAuction).isPresent()
        );
        assertThat(existsAuction.get()).isEqualTo(auction);
    }
}
