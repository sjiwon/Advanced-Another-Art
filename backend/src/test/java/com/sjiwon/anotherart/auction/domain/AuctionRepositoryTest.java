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

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
            final Period period = Period.of(currentTime1DayAgo, currentTime1DayLater);

            // when
            Auction auction = Auction.initAuction(auctionArt, period);

            // then
            assertAll(
                    () -> assertThat(auction.getArt().getId()).isEqualTo(auctionArt.getId()),
                    () -> assertThat(auction.getArt().getName()).isEqualTo(auctionArt.getName()),
                    () -> assertThat(auction.getArt().getArtType()).isEqualTo(ArtType.AUCTION),
                    () -> assertThat(auction.getBidder()).isNull(),
                    () -> assertThat(auction.getBidAmount()).isEqualTo(auctionArt.getPrice())
            );
        }
    }

    @Test
    @DisplayName("작품 ID에 해당하는 경매 정보를 조회한다")
    void test1() {
        // given
        Member owner = createMemberA();
        Art auctionArt = createAuctionArtA(owner);
        Auction auction = initAuction(auctionArt);

        // when
        Auction findAuction = auctionRepository.findByArtId(auctionArt.getId()).orElseThrow();

        // then
        assertAll(
                () -> assertThat(findAuction.getBidder()).isNull(),
                () -> assertThat(findAuction.getBidAmount()).isEqualTo(auctionArt.getPrice()),
                () -> assertThat(findAuction.getArt().getId()).isEqualTo(auctionArt.getId()),
                () -> assertThat(findAuction.getArt().getName()).isEqualTo(auctionArt.getName()),
                () -> assertThat(findAuction.getArt().getArtStatus()).isEqualTo(ArtStatus.FOR_SALE),
                () -> assertThat(findAuction.getArt().getArtType()).isEqualTo(ArtType.AUCTION),
                () -> assertThat(findAuction.getArt().getOwner().getId()).isEqualTo(owner.getId()),
                () -> assertThat(findAuction.getArt().getOwner().getName()).isEqualTo(owner.getName()),
                () -> assertThat(findAuction.getArt().getOwner().getNickname()).isEqualTo(owner.getNickname())
        );
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

    private Art createGeneralArt(Member member) {
        return artRepository.save(ArtFixture.B.toArt(member, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayAgo, currentTime1DayLater)));
    }
}