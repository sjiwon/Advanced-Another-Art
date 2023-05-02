package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.domain.hashtag.Hashtag;
import com.sjiwon.anotherart.art.domain.hashtag.HashtagRepository;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
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
import com.sjiwon.anotherart.purchase.domain.Purchase;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Repository Layer] -> ArtSpecificSimpleQueryRepository 테스트")
class ArtSpecificSimpleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointDetailRepository pointDetailRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionRecordRepository auctionRecordRepository;

    @Test
    @DisplayName("작품의 해시태그를 조회한다")
    void test1() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner, HASHTAGS);

        // when - then
        List<String> hashtags1 = artRepository.getHashtagsById(art.getId());
        assertThat(hashtags1).containsAll(HASHTAGS);

        updateHashtags(art, UPDATE_HASHTAGS);

        List<String> hashtags2 = artRepository.getHashtagsById(art.getId());
        assertThat(hashtags2).containsAll(UPDATE_HASHTAGS);
    }

    @Test
    @DisplayName("작품에 대해서 좋아요 등록을 한 사용자의 ID(PK)를 조회한다")
    void test2() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner, HASHTAGS);

        // when - then
        Member memberB = createMemberB();
        Member memberC = createMemberC();

        processLikeMarking(art, memberB);
        List<Long> likeMarkingMembers1 = artRepository.getLikeMarkingMembersById(art.getId());
        assertAll(
                () -> assertThat(likeMarkingMembers1.size()).isEqualTo(1),
                () -> assertThat(likeMarkingMembers1).contains(memberB.getId())
        );

        processLikeMarking(art, memberC);
        List<Long> likeMarkingMembers2 = artRepository.getLikeMarkingMembersById(art.getId());
        assertAll(
                () -> assertThat(likeMarkingMembers2.size()).isEqualTo(2),
                () -> assertThat(likeMarkingMembers2).contains(memberB.getId(), memberC.getId())
        );
    }

    @Test
    @DisplayName("Assemble된 일반 작품 정보를 조회한다")
    void test3() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner, HASHTAGS);

        // when - then
        BasicGeneralArt generalArt1 = artRepository.getGeneralArtById(art.getId());
        assertAll(
                // 작품 정보
                () -> assertThat(generalArt1.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(generalArt1.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(generalArt1.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(generalArt1.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(generalArt1.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE),
                () -> assertThat(generalArt1.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(generalArt1.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(generalArt1.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(generalArt1.getOwnerSchool()).isEqualTo(owner.getSchool()),
                // 구매자 정보
                () -> assertThat(generalArt1.getBuyerId()).isNull(),
                () -> assertThat(generalArt1.getBuyerNickname()).isNull(),
                () -> assertThat(generalArt1.getBuyerSchool()).isNull()
        );

        // 구매 진행
        Member buyer = createMemberB();
        processPurchase(art, buyer);

        BasicGeneralArt generalArt2 = artRepository.getGeneralArtById(art.getId());
        assertAll(
                // 작품 정보
                () -> assertThat(generalArt2.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(generalArt2.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(generalArt2.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(generalArt2.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(generalArt2.getArtStatus()).isEqualTo(ArtStatus.SOLD_OUT),
                () -> assertThat(generalArt2.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(generalArt2.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(generalArt2.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(generalArt2.getOwnerSchool()).isEqualTo(owner.getSchool()),
                // 구매자 정보
                () -> assertThat(generalArt2.getBuyerId()).isEqualTo(buyer.getId()),
                () -> assertThat(generalArt2.getBuyerNickname()).isEqualTo(buyer.getNickname()),
                () -> assertThat(generalArt2.getBuyerSchool()).isEqualTo(buyer.getSchool())
        );
    }

    @Test
    @DisplayName("Assemble된 경매 작품 정보를 조회한다")
    void test4() {
        // given
        Member owner = createMemberA();
        Art art = createAuctionArt(owner, HASHTAGS);
        Auction auction = initAuction(art);

        // when - then
        BasicAuctionArt auctionArt1 = artRepository.getAuctionArtById(art.getId());
        assertAll(
                // 경매 정보
                () -> assertThat(auctionArt1.getAuctionId()).isEqualTo(auction.getId()),
                () -> assertThat(auctionArt1.getHighestBidPrice()).isEqualTo(auction.getBidAmount()),
                // 최고 입찰자 정보
                () -> assertThat(auctionArt1.getHighestBidderId()).isNull(),
                () -> assertThat(auctionArt1.getHighestBidderNickname()).isNull(),
                () -> assertThat(auctionArt1.getHighestBidderSchool()).isNull(),
                // 작품 정보
                () -> assertThat(auctionArt1.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(auctionArt1.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(auctionArt1.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(auctionArt1.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(auctionArt1.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(auctionArt1.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(auctionArt1.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(auctionArt1.getOwnerSchool()).isEqualTo(owner.getSchool())
        );
        
        
        // 입찰 1회 진행
        Member bidder1 = createMemberB();
        final int bidAmount1 = auction.getBidAmount() + 5_000;
        processBid(auction, bidder1, bidAmount1);

        BasicAuctionArt auctionArt2 = artRepository.getAuctionArtById(art.getId());
        assertAll(
                // 경매 정보
                () -> assertThat(auctionArt2.getAuctionId()).isEqualTo(auction.getId()),
                () -> assertThat(auctionArt2.getHighestBidPrice()).isEqualTo(bidAmount1),
                // 최고 입찰자 정보
                () -> assertThat(auctionArt2.getHighestBidderId()).isEqualTo(bidder1.getId()),
                () -> assertThat(auctionArt2.getHighestBidderNickname()).isEqualTo(bidder1.getNickname()),
                () -> assertThat(auctionArt2.getHighestBidderSchool()).isEqualTo(bidder1.getSchool()),
                // 작품 정보
                () -> assertThat(auctionArt2.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(auctionArt2.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(auctionArt2.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(auctionArt2.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(auctionArt2.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(auctionArt2.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(auctionArt2.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(auctionArt2.getOwnerSchool()).isEqualTo(owner.getSchool())
        );
        
        // 입찰 2회 진행
        Member bidder2 = createMemberC();
        final int bidAmount2 = auction.getBidAmount() + 5_000;
        processBid(auction, bidder2, bidAmount2);

        BasicAuctionArt auctionArt3 = artRepository.getAuctionArtById(art.getId());
        assertAll(
                // 경매 정보
                () -> assertThat(auctionArt3.getAuctionId()).isEqualTo(auction.getId()),
                () -> assertThat(auctionArt3.getHighestBidPrice()).isEqualTo(bidAmount2),
                // 최고 입찰자 정보
                () -> assertThat(auctionArt3.getHighestBidderId()).isEqualTo(bidder2.getId()),
                () -> assertThat(auctionArt3.getHighestBidderNickname()).isEqualTo(bidder2.getNickname()),
                () -> assertThat(auctionArt3.getHighestBidderSchool()).isEqualTo(bidder2.getSchool()),
                // 작품 정보
                () -> assertThat(auctionArt3.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(auctionArt3.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(auctionArt3.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(auctionArt3.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(auctionArt3.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(auctionArt3.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(auctionArt3.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(auctionArt3.getOwnerSchool()).isEqualTo(owner.getSchool())
        );
    }

    private void updateHashtags(Art art, List<String> updateHashtags) {
        artRepository.deleteHashtagsByArtId(art.getId());
        hashtagRepository.saveAllAndFlush(
                updateHashtags.stream()
                        .map(value -> Hashtag.from(art, value))
                        .collect(Collectors.toList())
        );
    }

    private void processLikeMarking(Art art, Member member) {
        favoriteRepository.save(Favorite.favoriteMarking(art.getId(), member.getId()));
    }

    private void processPurchase(Art art, Member buyer) {
        // 판매자
        art.getOwner().increasePoint(art.getPrice());
        pointDetailRepository.save(PointDetail.insertPointDetail(art.getOwner(), PointType.SOLD, art.getPrice()));
        // 구매자
        buyer.decreasePoint(art.getPrice());
        pointDetailRepository.save(PointDetail.insertPointDetail(buyer, PointType.PURCHASE, art.getPrice()));
        purchaseRepository.save(Purchase.purchaseArt(buyer, art, art.getPrice()));
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