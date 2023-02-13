package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.infra.query.dto.response.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.BasicGeneralArt;
import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.service.dto.response.GeneralArt;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Service Layer] -> ArtSearchService 테스트")
@RequiredArgsConstructor
class ArtSearchServiceTest extends ServiceIntegrateTest {
    private final ArtSearchService artSearchService;
    
    @Test
    @DisplayName("일반 작품 정보를 조회한다 [작품 정보 + 해시태그 리스트 + 좋아요 등록 사용자 ID 리스트]")
    void test1() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner, HASHTAGS);

        Member memberB = createMemberB();
        Member buyer = createMemberC();
        processLikeMarking(art, memberB);
        processLikeMarking(art, buyer);

        // when - then
        GeneralArt findGeneralArt1 = (GeneralArt) artSearchService.getSingleArt(art.getId());
        
        BasicGeneralArt findArt1 = findGeneralArt1.getArt();
        List<String> hashtags1 = findGeneralArt1.getHashtags();
        List<Long> likeMarkingMembers1 = findGeneralArt1.getLikeMarkingMembers();
        assertAll(
                // 작품 정보
                () -> assertThat(findArt1.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(findArt1.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(findArt1.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(findArt1.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(findArt1.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE),
                () -> assertThat(findArt1.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(findArt1.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(findArt1.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(findArt1.getOwnerSchool()).isEqualTo(owner.getSchool()),
                // 구매자 정보
                () -> assertThat(findArt1.getBuyerId()).isNull(),
                () -> assertThat(findArt1.getBuyerNickname()).isNull(),
                () -> assertThat(findArt1.getBuyerSchool()).isNull(),
                // 해시태그 정보
                () -> assertThat(hashtags1).containsAll(HASHTAGS),
                // 좋아요 등록 사용자 정보
                () -> assertThat(likeMarkingMembers1).contains(memberB.getId(), buyer.getId())
        );

        // 구매 진행
        processPurchase(art, buyer);
        GeneralArt findGeneralArt2 = (GeneralArt) artSearchService.getSingleArt(art.getId());
        
        BasicGeneralArt findArt2 = findGeneralArt2.getArt();
        List<String> hashtags2 = findGeneralArt2.getHashtags();
        List<Long> likeMarkingMembers2 = findGeneralArt2.getLikeMarkingMembers();
        assertAll(
                // 작품 정보
                () -> assertThat(findArt2.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(findArt2.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(findArt2.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(findArt2.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(findArt2.getArtStatus()).isEqualTo(ArtStatus.SOLD_OUT),
                () -> assertThat(findArt2.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(findArt2.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(findArt2.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(findArt2.getOwnerSchool()).isEqualTo(owner.getSchool()),
                // 구매자 정보
                () -> assertThat(findArt2.getBuyerId()).isEqualTo(buyer.getId()),
                () -> assertThat(findArt2.getBuyerNickname()).isEqualTo(buyer.getNickname()),
                () -> assertThat(findArt2.getBuyerSchool()).isEqualTo(buyer.getSchool()),
                // 해시태그 정보
                () -> assertThat(hashtags2).containsAll(HASHTAGS),
                // 좋아요 등록 사용자 정보
                () -> assertThat(likeMarkingMembers2).contains(memberB.getId(), buyer.getId())
        );
    }
    
    @Test
    @DisplayName("경매 작품 정보를 조회한다 [작품 정보 + 해시태그 리스트 + 좋아요 등록 사용자 ID 리스트 + 입찰 횟수]")
    void test2() {
        // given
        Member owner = createMemberA();
        Art art = createAuctionArt(owner, HASHTAGS);
        Auction auction = initAuction(art);

        // when - then
        AuctionArt auctionArt1 = (AuctionArt) artSearchService.getSingleArt(art.getId());
        BasicAuctionArt findArt1 = auctionArt1.getArt();
        List<String> hashtags1 = auctionArt1.getHashtags();
        List<Long> likeMarkingMembers1 = auctionArt1.getLikeMarkingMembers();
        int bidCount1 = auctionArt1.getBidCount();
        assertAll(
                // 경매 정보
                () -> assertThat(findArt1.getAuctionId()).isEqualTo(auction.getId()),
                () -> assertThat(findArt1.getHighestBidPrice()).isEqualTo(auction.getBidAmount()),
                // 최고 입찰자 정보
                () -> assertThat(findArt1.getHighestBidderId()).isNull(),
                () -> assertThat(findArt1.getHighestBidderNickname()).isNull(),
                () -> assertThat(findArt1.getHighestBidderSchool()).isNull(),
                // 작품 정보
                () -> assertThat(findArt1.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(findArt1.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(findArt1.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(findArt1.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(findArt1.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(findArt1.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(findArt1.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(findArt1.getOwnerSchool()).isEqualTo(owner.getSchool()),
                // 해시태그 정보
                () -> assertThat(hashtags1).containsAll(HASHTAGS),
                // 좋아요 등록 사용자 정보
                () -> assertThat(likeMarkingMembers1).isEmpty(),
                // 입찰 횟수
                () -> assertThat(bidCount1).isEqualTo(0)
        );

        // 입찰 1회 진행
        Member bidder1 = createMemberB();
        processLikeMarking(art, bidder1);
        final int bidAmount1 = auction.getBidAmount() + 5_000;
        processBid(auction, bidder1, bidAmount1);

        AuctionArt auctionArt2 = (AuctionArt) artSearchService.getSingleArt(art.getId());
        BasicAuctionArt findArt2 = auctionArt2.getArt();
        List<String> hashtags2 = auctionArt2.getHashtags();
        List<Long> likeMarkingMembers2 = auctionArt2.getLikeMarkingMembers();
        int bidCount2 = auctionArt2.getBidCount();
        assertAll(
                // 경매 정보
                () -> assertThat(findArt2.getAuctionId()).isEqualTo(auction.getId()),
                () -> assertThat(findArt2.getHighestBidPrice()).isEqualTo(bidAmount1),
                // 최고 입찰자 정보
                () -> assertThat(findArt2.getHighestBidderId()).isEqualTo(bidder1.getId()),
                () -> assertThat(findArt2.getHighestBidderNickname()).isEqualTo(bidder1.getNickname()),
                () -> assertThat(findArt2.getHighestBidderSchool()).isEqualTo(bidder1.getSchool()),
                // 작품 정보
                () -> assertThat(findArt2.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(findArt2.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(findArt2.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(findArt2.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(findArt2.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(findArt2.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(findArt2.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(findArt2.getOwnerSchool()).isEqualTo(owner.getSchool()),
                // 해시태그 정보
                () -> assertThat(hashtags2).containsAll(HASHTAGS),
                // 좋아요 등록 사용자 정보
                () -> assertThat(likeMarkingMembers2).contains(bidder1.getId()),
                // 입찰 횟수
                () -> assertThat(bidCount2).isEqualTo(1)
        );

        // 입찰 2회 진행
        Member bidder2 = createMemberC();
        processLikeMarking(art, bidder2);
        final int bidAmount2 = auction.getBidAmount() + 5_000;
        processBid(auction, bidder2, bidAmount2);

        AuctionArt auctionArt3 = (AuctionArt) artSearchService.getSingleArt(art.getId());
        BasicAuctionArt findArt3 = auctionArt3.getArt();
        List<String> hashtags3 = auctionArt3.getHashtags();
        List<Long> likeMarkingMembers3 = auctionArt3.getLikeMarkingMembers();
        int bidCount3 = auctionArt3.getBidCount();
        assertAll(
                // 경매 정보
                () -> assertThat(findArt3.getAuctionId()).isEqualTo(auction.getId()),
                () -> assertThat(findArt3.getHighestBidPrice()).isEqualTo(bidAmount2),
                // 최고 입찰자 정보
                () -> assertThat(findArt3.getHighestBidderId()).isEqualTo(bidder2.getId()),
                () -> assertThat(findArt3.getHighestBidderNickname()).isEqualTo(bidder2.getNickname()),
                () -> assertThat(findArt3.getHighestBidderSchool()).isEqualTo(bidder2.getSchool()),
                // 작품 정보
                () -> assertThat(findArt3.getArtId()).isEqualTo(art.getId()),
                () -> assertThat(findArt3.getArtName()).isEqualTo(art.getName()),
                () -> assertThat(findArt3.getArtDescription()).isEqualTo(art.getDescription()),
                () -> assertThat(findArt3.getArtPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(findArt3.getArtStorageName()).isEqualTo(art.getStorageName()),
                // 작품 주인 정보
                () -> assertThat(findArt3.getOwnerId()).isEqualTo(owner.getId()),
                () -> assertThat(findArt3.getOwnerNickname()).isEqualTo(owner.getNickname()),
                () -> assertThat(findArt3.getOwnerSchool()).isEqualTo(owner.getSchool()),
                // 해시태그 정보
                () -> assertThat(hashtags3).containsAll(HASHTAGS),
                // 좋아요 등록 사용자 정보
                () -> assertThat(likeMarkingMembers3).contains(bidder1.getId(), bidder2.getId()),
                // 입찰 횟수
                () -> assertThat(bidCount3).isEqualTo(2)
        );
    }
    
    @Test
    @DisplayName("존재하지 않는 작품에 대해서 조회를 하게되면 예외가 발생한다")
    void test3() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner, HASHTAGS);
        
        // when - then
        assertThatThrownBy(() -> artSearchService.getSingleArt(art.getId() + 10000L))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.ART_NOT_FOUND.getMessage());
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