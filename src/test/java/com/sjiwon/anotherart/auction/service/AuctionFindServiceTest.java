package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Auction [Service Layer] -> AuctionFindService 테스트")
@RequiredArgsConstructor
class AuctionFindServiceTest extends ServiceIntegrateTest {
    private final AuctionFindService auctionFindService;

    @Test
    @DisplayName("경매 ID(PK)로 경매 정보를 조회한다")
    void test1() {
        // given
        Member owner = createMemberA();
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt);

        // when
        Auction findAuction = auctionFindService.findByIdWithPessimisticLock(auction.getId());

        // then
        assertThat(findAuction.getId()).isEqualTo(auction.getId());
        assertThat(findAuction.getArt().getName()).isEqualTo(auctionArt.getName());
        assertThat(findAuction.getArt().getOwner().getId()).isEqualTo(owner.getId());
        assertThat(findAuction.getBidder()).isNull();
        assertThat(findAuction.getBidAmount()).isEqualTo(auctionArt.getPrice());
    }

    @Test
    @DisplayName("작품 ID(PK)로 경매 정보를 조회한다")
    void test2() {
        // given
        Member owner = createMemberA();
        Art generalArt = createGeneralArt(owner);
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt);

        // when
        assertThatThrownBy(() -> auctionFindService.findByArtId(generalArt.getId()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.AUCTION_NOT_FOUND.getMessage());
        Auction findAuction = auctionFindService.findByArtId(auctionArt.getId());

        // then
        assertThat(findAuction.getId()).isEqualTo(auction.getId());
        assertThat(findAuction.getArt().getName()).isEqualTo(auctionArt.getName());
        assertThat(findAuction.getArt().getOwner().getId()).isEqualTo(owner.getId());
        assertThat(findAuction.getBidder()).isNull();
        assertThat(findAuction.getBidAmount()).isEqualTo(auctionArt.getPrice());
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Art createGeneralArt(Member owner) {
        return artRepository.save(ArtFixture.B.toArt(owner, HASHTAGS));
    }

    private Art createAuctionArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayLater, currentTime3DayLater)));
    }
}