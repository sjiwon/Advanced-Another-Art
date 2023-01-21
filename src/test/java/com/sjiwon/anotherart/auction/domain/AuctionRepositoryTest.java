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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Auction [Repository Layer] -> AuctionRepository 테스트")
class AuctionRepositoryTest extends RepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArtRepository artRepository;

    @Autowired
    AuctionRepository auctionRepository;

    private static final Period AUCTION_PERIOD = Period.of(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
    private static final int INIT_AVAILABLE_POINT = 1_000_000;

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
            assertThat(auction.getCurrentHighestBidder().isBidderExists()).isFalse();
            assertThat(auction.getCurrentHighestBidder().getBidPrice()).isEqualTo(auctionArt.getPrice());
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
        @DisplayName("입찰 금액이 부족하면 입찰을 진행할 수 없다")
        void test2(){
            // given
            Member memberA = createMemberA();
            Art auctionArt = createAuctionArt(memberA);
            Auction auction = initAuction(auctionArt);

            Member memberB = createMemberB();
            final int previousBidPrice = 150000;
            auction.applyNewBid(memberB, previousBidPrice);
            assertThat(memberB.getAvailablePoint().getValue()).isEqualTo(INIT_AVAILABLE_POINT - previousBidPrice);

            // when - then
            Member memberC = createMemberC();
            assertThatThrownBy(() -> auction.applyNewBid(memberC, previousBidPrice))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.INVALID_BID_PRICE.getMessage());
        }

        @Test
        @DisplayName("경매 최고 입찰자는 연속해서 입찰할 수 없다")
        void test3(){
            // given
            Member memberA = createMemberA();
            Art auctionArt = createAuctionArt(memberA);
            Auction auction = initAuction(auctionArt);

            Member memberB = createMemberB();
            final int previousBidPrice = 150000;
            auction.applyNewBid(memberB, previousBidPrice);
            assertThat(memberB.getAvailablePoint().getValue()).isEqualTo(INIT_AVAILABLE_POINT - previousBidPrice);

            // when - then
            final int currentBidPrice = 200000;
            assertThatThrownBy(() -> auction.applyNewBid(memberB, currentBidPrice))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.INVALID_DUPLICATE_BID.getMessage());
        }

        @Test
        @DisplayName("입찰에 성공한다")
        void test4(){
            // given
            Member memberA = createMemberA();
            Art auctionArt = createAuctionArt(memberA);
            Auction auction = initAuction(auctionArt);

            Member memberB = createMemberB();
            final int previousBidPrice = 150000;
            auction.applyNewBid(memberB, previousBidPrice);
            assertThat(memberB.getAvailablePoint().getValue()).isEqualTo(INIT_AVAILABLE_POINT - previousBidPrice);

            // when - then
            Member memberC = createMemberC();
            final int currentBidPrice = 200000;
            auction.applyNewBid(memberC, currentBidPrice);
            assertThat(memberB.getAvailablePoint().getValue()).isEqualTo(INIT_AVAILABLE_POINT);
            assertThat(memberC.getAvailablePoint().getValue()).isEqualTo(INIT_AVAILABLE_POINT - currentBidPrice);

            // then
            assertThat(auction.getCurrentHighestBidder().getMember().getId()).isEqualTo(memberC.getId());
            assertThat(auction.getCurrentHighestBidder().getMember().getName()).isEqualTo(memberC.getName());
            assertThat(auction.getCurrentHighestBidder().getMember().getNickname()).isEqualTo(memberC.getNickname());
            assertThat(auction.getCurrentHighestBidder().getBidPrice()).isEqualTo(currentBidPrice);
        }
    }

    private PasswordEncoder generatePasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private Member createMemberA() {
        Member memberA = MemberFixture.A.toMember(generatePasswordEncoder());
        memberA.increasePoint(INIT_AVAILABLE_POINT);
        return memberRepository.save(memberA);
    }

    private Member createMemberB() {
        Member memberB = MemberFixture.B.toMember(generatePasswordEncoder());
        memberB.increasePoint(INIT_AVAILABLE_POINT);
        return memberRepository.save(memberB);
    }

    private Member createMemberC() {
        Member memberC = MemberFixture.C.toMember(generatePasswordEncoder());
        memberC.increasePoint(INIT_AVAILABLE_POINT);
        return memberRepository.save(memberC);
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