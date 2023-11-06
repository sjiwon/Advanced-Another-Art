package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.OPEN_NOW;
import static com.sjiwon.anotherart.member.domain.model.PointType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Art [Repository Layer] -> ArtSimpleQueryRepository 테스트")
class ArtSimpleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    private Member bidder;
    private Art art;
    private Auction auction;

    @BeforeEach
    void setUp() {
        bidder = memberRepository.save(MEMBER_A.toMember());
        bidder.addPointRecords(CHARGE, 100_000_000);

        final Member owner = memberRepository.save(MEMBER_B.toMember());
        art = artRepository.save(AUCTION_1.toArt(owner));
        auction = auctionRepository.save(Auction.createAuction(art, OPEN_NOW.toPeriod()));
    }

    @Test
    @DisplayName("경매 기록이 존재하는지 확인한다")
    void isAuctionRecordExists() {
        /* 입찰 기록 X */
        assertThat(artRepository.isAuctionRecordExists(art.getId())).isFalse();

        /* 입찰 기록 O */
        auction.applyNewBid(bidder, auction.getHighestBidPrice());
        assertThat(artRepository.isAuctionRecordExists(art.getId())).isTrue();
    }
}
