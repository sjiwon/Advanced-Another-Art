package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.ArtStatus;
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

import java.time.LocalDateTime;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Auction [Repository Layer] -> AuctionRepository 테스트")
class AuctionRepositoryTest extends RepositoryTest {
    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArtRepository artRepository;

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
            assertThatThrownBy(() -> initAuction(generalArt))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuctionErrorCode.INVALID_ART_TYPE.getMessage());
        }
        
        @Test
        @DisplayName("경매 작품은 경매 등록이 가능하다")
        void test2(){
            // given
            Member member = createMemberA();
            Art auctionArt = createAuctionArtA(member);
            final Period period = Period.of(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

            // when
            Auction auction = Auction.initAuction(auctionArt, period);

            // then
            assertThat(auction.getArt().getId()).isEqualTo(auctionArt.getId());
            assertThat(auction.getArt().getName()).isEqualTo(auctionArt.getName());
            assertThat(auction.getArt().getArtType()).isEqualTo(ArtType.AUCTION);
            assertThat(auction.getBidder()).isNull();
            assertThat(auction.getBidAmount()).isEqualTo(auctionArt.getPrice());
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
            Art auctionArt = createAuctionArtA(member);
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
            Art auctionArt = createAuctionArtA(memberA);
            Auction auction = initAuction(auctionArt);

            Member memberB = createMemberB();
            final int previousBidPrice = 150000;
            auction.applyNewBid(memberB, previousBidPrice);
            assertThat(memberB.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - previousBidPrice);

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
            Art auctionArt = createAuctionArtA(memberA);
            Auction auction = initAuction(auctionArt);

            Member memberB = createMemberB();
            final int previousBidPrice = 150000;
            auction.applyNewBid(memberB, previousBidPrice);
            assertThat(memberB.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - previousBidPrice);

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
            Art auctionArt = createAuctionArtA(memberA);
            Auction auction = initAuction(auctionArt);

            Member memberB = createMemberB();
            final int previousBidPrice = 150000;
            auction.applyNewBid(memberB, previousBidPrice);
            assertThat(memberB.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - previousBidPrice);

            // when
            Member memberC = createMemberC();
            final int currentBidPrice = 200000;
            auction.applyNewBid(memberC, currentBidPrice);
            assertThat(memberB.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT);
            assertThat(memberC.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - currentBidPrice);

            // then
            assertThat(auction.getBidder().getId()).isEqualTo(memberC.getId());
            assertThat(auction.getBidder().getName()).isEqualTo(memberC.getName());
            assertThat(auction.getBidder().getNickname()).isEqualTo(memberC.getNickname());
            assertThat(auction.getBidAmount()).isEqualTo(currentBidPrice);
        }
    }

    @Test
    @DisplayName("작품 ID에 해당하는 경매 정보를 조회한다")
    void test5() {
        // given
        Member owner = createMemberA();
        Art auctionArt = createAuctionArtA(owner);
        Auction auction = initAuction(auctionArt);

        // when
        Auction findAuction = auctionRepository.findByArtId(auctionArt.getId()).orElseThrow();

        // then
        assertThat(findAuction.getBidder()).isNull();
        assertThat(findAuction.getBidAmount()).isEqualTo(auctionArt.getPrice());
        assertThat(findAuction.getArt().getId()).isEqualTo(auctionArt.getId());
        assertThat(findAuction.getArt().getName()).isEqualTo(auctionArt.getName());
        assertThat(findAuction.getArt().getArtStatus()).isEqualTo(ArtStatus.FOR_SALE);
        assertThat(findAuction.getArt().getArtType()).isEqualTo(ArtType.AUCTION);
        assertThat(findAuction.getArt().getOwner().getId()).isEqualTo(owner.getId());
        assertThat(findAuction.getArt().getOwner().getName()).isEqualTo(owner.getName());
        assertThat(findAuction.getArt().getOwner().getNickname()).isEqualTo(owner.getNickname());
    }

    private Member createMemberA() {
        Member memberA = MemberFixture.A.toMember();
        memberA.increasePoint(INIT_AVAILABLE_POINT);
        return memberRepository.save(memberA);
    }

    private Member createMemberB() {
        Member memberB = MemberFixture.B.toMember();
        memberB.increasePoint(INIT_AVAILABLE_POINT);
        return memberRepository.save(memberB);
    }

    private Member createMemberC() {
        Member memberC = MemberFixture.C.toMember();
        memberC.increasePoint(INIT_AVAILABLE_POINT);
        return memberRepository.save(memberC);
    }

    private Art createAuctionArtA(Member member) {
        return artRepository.save(ArtFixture.A.toArt(member, HASHTAGS));
    }

    private Art createAuctionArtC(Member member) {
        return artRepository.save(ArtFixture.C.toArt(member, HASHTAGS));
    }

    private Art createGeneralArt(Member member) {
        return artRepository.save(ArtFixture.B.toArt(member, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayLater, currentTime3DayLater)));
    }
}