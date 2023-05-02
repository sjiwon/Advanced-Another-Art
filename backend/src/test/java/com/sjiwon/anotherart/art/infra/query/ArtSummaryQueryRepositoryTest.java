package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.AuctionRecordSummary;
import com.sjiwon.anotherart.art.infra.query.dto.FavoriteSummary;
import com.sjiwon.anotherart.art.infra.query.dto.HashtagSummary;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecordRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.favorite.domain.FavoriteRepository;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.domain.point.PointType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Repository Layer] -> ArtSummaryQueryRepository 테스트")
class ArtSummaryQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointDetailRepository pointDetailRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionRecordRepository auctionRecordRepository;

    @Test
    @DisplayName("간략화시킨 해시태그 정보를 조회한다")
    void test1() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner, HASHTAGS);

        // when
        List<HashtagSummary> hashtagSummaries = artRepository.findHashtagSummaryList();

        // then
        assertAll(
                () -> assertThat(hashtagSummaries.size()).isEqualTo(HASHTAGS.size()),
                () -> assertThat(hashtagSummaries.stream().map(HashtagSummary::getArtId).distinct().toList()).containsExactly(art.getId()),
                () -> assertThat(hashtagSummaries.stream().map(HashtagSummary::getName).toList()).containsAll(HASHTAGS)
        );
    }

    @Test
    @DisplayName("간략화시킨 좋아요 등록 정보를 조회한다")
    void test2() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner, HASHTAGS);

        Member memberB = createMemberB();
        Member memberC = createMemberC();
        processLikeMarking(art, memberB);
        processLikeMarking(art, memberC);

        // when
        List<FavoriteSummary> favoriteSummaries = artRepository.findFavoriteSummaryList();

        // then
        assertAll(
                () -> assertThat(favoriteSummaries.size()).isEqualTo(2),
                () -> assertThat(favoriteSummaries.stream().map(FavoriteSummary::getArtId).distinct().toList()).containsExactly(art.getId()),
                () -> assertThat(favoriteSummaries.stream().map(FavoriteSummary::getMemberId).toList()).contains(memberB.getId(), memberC.getId())
        );
    }

    @Test
    @DisplayName("간략화시킨 경매 입찰 정보를 조회한다")
    void test3() {
        // given
        Member owner = createMemberA();
        Art art = createAuctionArt(owner, HASHTAGS);
        Auction auction = initAuction(art);

        Member bidder1 = createMemberB();
        processBid(auction, bidder1, auction.getBidAmount() + 5_000);

        Member bidder2 = createMemberC();
        processBid(auction, bidder2, auction.getBidAmount() + 5_000);

        // when
        List<AuctionRecordSummary> auctionRecordSummaries = artRepository.findAuctionRecordSummaryList();

        // then
        assertAll(
                () -> assertThat(auctionRecordSummaries.size()).isEqualTo(2),
                () -> assertThat(auctionRecordSummaries.stream().map(AuctionRecordSummary::getArtId).distinct().toList()).containsExactly(art.getId()),
                () -> assertThat(auctionRecordSummaries.stream().map(AuctionRecordSummary::getMemberId).toList()).contains(bidder1.getId(), bidder2.getId())
        );
    }

    private void processLikeMarking(Art art, Member member) {
        favoriteRepository.save(Favorite.favoriteMarking(art.getId(), member.getId()));
    }

    private void processBid(Auction auction, Member bidder, int bidAmount) {
        auction.applyNewBid(bidder, bidAmount);
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, bidder, bidAmount));
    }

    private Member createMemberA() {
        Member member = memberRepository.save(MemberFixture.A.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }

    private Member createMemberB() {
        Member member = memberRepository.save(MemberFixture.B.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }

    private Member createMemberC() {
        Member member = memberRepository.save(MemberFixture.C.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }

    private Art createGeneralArt(Member owner, List<String> hashtags) {
        return artRepository.save(ArtFixture.B.toArt(owner, hashtags));
    }

    private Art createAuctionArt(Member owner, List<String> hashtags) {
        return artRepository.save(ArtFixture.A.toArt(owner, hashtags));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayAgo, currentTime1DayLater)));
    }
}