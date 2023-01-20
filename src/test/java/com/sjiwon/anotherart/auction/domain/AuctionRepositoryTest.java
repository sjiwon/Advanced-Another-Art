package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuctionRepositoryTest extends RepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArtRepository artRepository;

    @Autowired
    AuctionRepository auctionRepository;

    private static final Period AUCTION_PERIOD = Period.of(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

    @Nested
    @DisplayName("경매 등록")
    class auctionRegistration {
        @Test
        @DisplayName("일반 작품은 경매에 등록할 수 없다")
        void test1(){
            // given
            Member member = createMemberA();
            Art generalArt = createGeneralArt(member);

            // when - then
            assertThatThrownBy(() -> Auction.initAuction(generalArt, AUCTION_PERIOD))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.INVALID_ART_TYPE.getMessage());
        }
        
        @Test
        @DisplayName("경매 작품은 경매 등록이 가능하다")
        void test2(){
            // given
            Member member = createMemberA();
            Art auctionArt = createAuctionArt(member);
            final Period period = Period.of(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

            // when
            Auction auction = Auction.initAuction(auctionArt, period);

            // then
            assertThat(auction.getArt().getId()).isEqualTo(auctionArt.getId());
            assertThat(auction.getArt().getName()).isEqualTo(auctionArt.getName());
            assertThat(auction.getArt().getArtType()).isEqualTo(ArtType.AUCTION);
            assertThat(auction.getCurrentBidder().getMember().getId()).isEqualTo(member.getId());
            assertThat(auction.getCurrentBidder().getMember().getName()).isEqualTo(member.getName());
            assertThat(auction.getCurrentBidder().getMember().getNickname()).isEqualTo(member.getNickname());
            assertThat(auction.getCurrentBidder().getBidPrice()).isEqualTo(auctionArt.getPrice());
        }
    }

    @Nested
    @DisplayName("경매 입찰 (초기 가격: 100000)")
    class auctionBid {
        @Test
        @DisplayName("작품 소유자는 본인의 경매 작품에 입찰할 수 없다")
        void test1(){
            // given
            Member member = createMemberA();
            Art auctionArt = createAuctionArt(member);
            Auction auction = initAuction(auctionArt);

            // when - then
            final int bidPrice = 150000;
            assertThatThrownBy(() -> auction.applyNewBid(member, bidPrice))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.INVALID_OWNER_BID.getMessage());
        }

        @Test
        @DisplayName("경매 최고 입찰자는 연속해서 입찰할 수 없다")
        void test2(){
            // given
            Member memberA = createMemberA();
            Art auctionArt = createAuctionArt(memberA);
            Auction oldAuction = initAuction(auctionArt);

            Member memberB = createMemberB();
            final int previousBidPrice = 150000;
            Auction previousAuction = oldAuction.applyNewBid(memberB, previousBidPrice);

            // when - then
            final int bidPrice = 200000;
            assertThatThrownBy(() -> previousAuction.applyNewBid(memberB, bidPrice))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.INVALID_DUPLICATE_BID.getMessage());
        }

        @Test
        @DisplayName("입찰 금액이 부족하면 입찰을 진행할 수 없다")
        void test3(){
            // given
            Member memberA = createMemberA();
            Art auctionArt = createAuctionArt(memberA);
            Auction oldAuction = initAuction(auctionArt);

            Member memberB = createMemberB();
            final int previousBidPrice = 150000;
            Auction previousAuction = oldAuction.applyNewBid(memberB, previousBidPrice);

            // when - then
            Member memberC = createMemberC();
            assertThatThrownBy(() -> previousAuction.applyNewBid(memberC, previousBidPrice))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.INVALID_BID_PRICE.getMessage());
        }

        @Test
        @DisplayName("입찰에 성공한다")
        void test4(){
            // given
            Member memberA = createMemberA();
            Art auctionArt = createAuctionArt(memberA);
            Auction oldAuction = initAuction(auctionArt);

            Member memberB = createMemberB();
            final int previousBidPrice = 150000;
            Auction previousAuction = oldAuction.applyNewBid(memberB, previousBidPrice);

            // when - then
            Member memberC = createMemberC();
            final int currentBidPrice = 200000;
            Auction currentAuction = previousAuction.applyNewBid(memberC, currentBidPrice);

            // then
            assertThat(currentAuction.getCurrentBidder().getMember().getId()).isEqualTo(memberC.getId());
            assertThat(currentAuction.getCurrentBidder().getMember().getName()).isEqualTo(memberC.getName());
            assertThat(currentAuction.getCurrentBidder().getMember().getNickname()).isEqualTo(memberC.getNickname());
            assertThat(currentAuction.getCurrentBidder().getBidPrice()).isEqualTo(currentBidPrice);
        }
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember(PasswordEncoderFactories.createDelegatingPasswordEncoder()));
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember(PasswordEncoderFactories.createDelegatingPasswordEncoder()));
    }

    private Member createMemberC() {
        return memberRepository.save(MemberFixture.C.toMember(PasswordEncoderFactories.createDelegatingPasswordEncoder()));
    }

    private Art createAuctionArt(Member member) {
        return artRepository.save(ArtFixture.A.toArt(member));
    }

    private Art createGeneralArt(Member member) {
        return artRepository.save(ArtFixture.B.toArt(member));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, AUCTION_PERIOD));
    }
}