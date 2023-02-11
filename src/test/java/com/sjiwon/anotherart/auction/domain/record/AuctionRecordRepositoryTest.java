package com.sjiwon.anotherart.auction.domain.record;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("AuctionRecord [Repository Layer] -> AuctionRecordRepository 테스트")
class AuctionRecordRepositoryTest extends RepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ArtRepository artRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    AuctionRecordRepository auctionRecordRepository;

    @Test
    @DisplayName("경매 작품에 대한 입찰 횟수를 조회한다")
    void test1() {
        // 입찰 0회
        Member owner = createMemberA();
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt);
        assertThat(auctionRecordRepository.countByAuctionId(auction.getId())).isEqualTo(0);

        // when - then
        Member memberB = createMemberB();
        Member memberC = createMemberC();

        // 입찰 1회
        int bidAmount = auctionArt.getPrice() + 5000;
        bidProcess(auction, memberB, bidAmount);
        assertThat(auctionRecordRepository.countByAuctionId(auction.getId())).isEqualTo(1);

        // 입찰 2회
        bidAmount += 5000;
        bidProcess(auction, memberC, bidAmount);
        assertThat(auctionRecordRepository.countByAuctionId(auction.getId())).isEqualTo(2);

        // 입찰 3회
        bidAmount += 5000;
        bidProcess(auction, memberB, bidAmount);
        assertThat(auctionRecordRepository.countByAuctionId(auction.getId())).isEqualTo(3);
    }

    @Test
    @DisplayName("경매 작품에 대한 경매 기록을 조회한다")
    void test2() {
        // 입찰 0회
        Member owner = createMemberA();
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt);

        List<AuctionRecord> records1 = auctionRecordRepository.findByAuctionId(auction.getId());
        assertThat(records1.size()).isEqualTo(0);

        // when - then
        Member memberB = createMemberB();
        Member memberC = createMemberC();

        // 입찰 1회
        int bidAmount1 = auctionArt.getPrice() + 5000;
        bidProcess(auction, memberB, bidAmount1);

        List<AuctionRecord> records2 = auctionRecordRepository.findByAuctionId(auction.getId());
        assertAll(
                () -> assertThat(records2.size()).isEqualTo(1),

                () -> assertThat(records2.get(0).getBidder()).isNotNull(),
                () -> assertThat(records2.get(0).getBidder().getId()).isEqualTo(memberB.getId()),
                () -> assertThat(records2.get(0).getBidAmount()).isEqualTo(bidAmount1)
        );

        // 입찰 2회
        int bidAmount2 = bidAmount1 + 5000;
        bidProcess(auction, memberC, bidAmount2);

        List<AuctionRecord> records3 = auctionRecordRepository.findByAuctionId(auction.getId());
        assertAll(
                () -> assertThat(records3.size()).isEqualTo(2),

                () -> assertThat(records3.get(0).getBidder()).isNotNull(),
                () -> assertThat(records3.get(0).getBidder().getId()).isEqualTo(memberB.getId()),
                () -> assertThat(records3.get(0).getBidAmount()).isEqualTo(bidAmount1),

                () -> assertThat(records3.get(1).getBidder()).isNotNull(),
                () -> assertThat(records3.get(1).getBidder().getId()).isEqualTo(memberC.getId()),
                () -> assertThat(records3.get(1).getBidAmount()).isEqualTo(bidAmount2)
        );

        // 입찰 3회
        int bidAmount3 = bidAmount2 + 5000;
        bidProcess(auction, memberB, bidAmount3);

        List<AuctionRecord> records4 = auctionRecordRepository.findByAuctionId(auction.getId());
        assertAll(
                () -> assertThat(records4.size()).isEqualTo(3),

                () -> assertThat(records4.get(0).getBidder()).isNotNull(),
                () -> assertThat(records4.get(0).getBidder().getId()).isEqualTo(memberB.getId()),
                () -> assertThat(records4.get(0).getBidAmount()).isEqualTo(bidAmount1),

                () -> assertThat(records4.get(1).getBidder()).isNotNull(),
                () -> assertThat(records4.get(1).getBidder().getId()).isEqualTo(memberC.getId()),
                () -> assertThat(records4.get(1).getBidAmount()).isEqualTo(bidAmount2),

                () -> assertThat(records4.get(2).getBidder()).isNotNull(),
                () -> assertThat(records4.get(2).getBidder().getId()).isEqualTo(memberB.getId()),
                () -> assertThat(records4.get(2).getBidAmount()).isEqualTo(bidAmount3)
        );
    }

    private void bidProcess(Auction auction, Member newBidder, int newBidAmount) {
        auction.applyNewBid(newBidder, newBidAmount);
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, newBidder, newBidAmount));
    }

    private Member createMemberA() {
        Member member = MemberFixture.A.toMember();
        member.increasePoint(INIT_AVAILABLE_POINT);
        return memberRepository.save(member);
    }

    private Member createMemberB() {
        Member member = MemberFixture.B.toMember();
        member.increasePoint(INIT_AVAILABLE_POINT);
        return memberRepository.save(member);
    }

    private Member createMemberC() {
        Member member = MemberFixture.C.toMember();
        member.increasePoint(INIT_AVAILABLE_POINT);
        return memberRepository.save(member);
    }

    private Art createAuctionArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayAgo, currentTime1DayLater)));
    }
}