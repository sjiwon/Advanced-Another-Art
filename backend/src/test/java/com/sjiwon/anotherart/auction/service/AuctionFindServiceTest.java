package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction [Service Layer] -> AuctionFindService 테스트")
class AuctionFindServiceTest extends ServiceTest {
    @Autowired
    private AuctionFindService auctionFindService;

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
    void findById() {
        // when
        assertThatThrownBy(() -> auctionFindService.findById(auction.getId() + 10000L))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.AUCTION_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> auctionFindService.findByIdWithPessimisticLock(auction.getId() + 10000L))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.AUCTION_NOT_FOUND.getMessage());

        Auction findAuction1 = auctionFindService.findById(auction.getId());
        Auction findAuction2 = auctionFindService.findByIdWithPessimisticLock(auction.getId());

        // then
        assertAll(
                () -> assertThat(findAuction1).isEqualTo(auction),
                () -> assertThat(findAuction2).isEqualTo(auction)
        );
    }

    @Test
    @DisplayName("작품 ID로 경매 정보를 조회한다")
    void findByArtId() {
        // when
        assertThatThrownBy(() -> auctionFindService.findById(art.getId() + 10000L))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.AUCTION_NOT_FOUND.getMessage());

        Auction findAuction = auctionFindService.findByArtId(art.getId());

        // then
        assertAll(
                () -> assertThat(findAuction).isEqualTo(auction)
        );
    }
}
