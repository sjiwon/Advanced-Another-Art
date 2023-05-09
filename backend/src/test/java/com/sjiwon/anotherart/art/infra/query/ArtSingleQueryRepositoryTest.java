package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.GeneralArt;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.favorite.domain.FavoriteRepository;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.*;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Repository Layer] -> ArtSingleQueryRepository 테스트")
class ArtSingleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    private Member owner;
    private Member buyer;
    private Member bidder;
    private final Art[] generalArts = new Art[2]; // [0] = 입찰 X, [1] = 입찰 O
    private final Art[] auctionArts = new Art[2]; // [0] = 구매 X, [1] = 구매 O
    private final Auction[] auctions = new Auction[2];

    @BeforeEach
    void setUp() {
        owner = createMember(MEMBER_A);
        buyer = createMember(MEMBER_B);
        bidder = createMember(MEMBER_C);

        initArtsAndAuctions();
    }

    private Member createMember(MemberFixture fixture) {
        Member member = fixture.toMember();
        member.addPointRecords(CHARGE, 100_000_000);
        return memberRepository.save(member);
    }

    private void initArtsAndAuctions() {
        List<ArtFixture> generalArtFixtures = Arrays.stream(ArtFixture.values())
                .filter(art -> art.getType() == GENERAL)
                .toList();
        List<ArtFixture> auctionArtFixtures = Arrays.stream(ArtFixture.values())
                .filter(art -> art.getType() == AUCTION)
                .toList();
        Arrays.setAll(generalArts, i -> artRepository.save(generalArtFixtures.get(i).toArt(owner)));
        Arrays.setAll(auctionArts, i -> artRepository.save(auctionArtFixtures.get(i).toArt(owner)));
        Arrays.setAll(auctions, i -> auctionRepository.save(AUCTION_OPEN_NOW.toAuction(auctionArts[i])));
    }

    @Test
    @DisplayName("단건 경매 작품을 조회한다")
    void getAuctionArt() {
        // auctions[1] -> auctionArts[1] 입찰
        bid();

        AuctionArt result1 = artRepository.getAuctionArt(auctionArts[0].getId());
        AuctionArt result2 = artRepository.getAuctionArt(auctionArts[1].getId());
        assertThatAuctionArtMatch(
                List.of(result1, result2),
                List.of(0, 1)
        );
    }

    @Test
    @DisplayName("단건 일반 작품을 조회한다")
    void getGeneralArt() {
        // generalArt[1] 구매
        purchase();

        GeneralArt result1 = artRepository.getGeneralArt(generalArts[0].getId());
        GeneralArt result2 = artRepository.getGeneralArt(generalArts[1].getId());
        assertThatGeneralArtMatch(List.of(result1, result2));
    }

    private void bid() {
        auctions[1].applyNewBid(bidder, auctions[1].getHighestBidPrice());
        favoriteRepository.save(Favorite.favoriteMarking(auctionArts[1].getId(), bidder.getId()));
    }

    private void purchase() {
        purchaseRepository.save(Purchase.purchaseAuctionArt(generalArts[1], buyer, generalArts[1].getPrice()));
        favoriteRepository.save(Favorite.favoriteMarking(generalArts[1].getId(), buyer.getId()));
    }

    private void assertThatAuctionArtMatch(List<AuctionArt> arts, List<Integer> bidCounts) {
        for (int i = 0; i < arts.size(); i++) {
            AuctionArt auctionArt = arts.get(i);
            int bidCount = bidCounts.get(i);

            Auction auction = auctions[i];
            Art art = auctionArts[i];

            assertAll(
                    () -> assertThat(auctionArt.getAuction().getId()).isEqualTo(auction.getId()),
                    () -> assertThat(auctionArt.getAuction().getHighestBidPrice()).isEqualTo(auction.getHighestBidPrice()),
                    () -> assertThat(auctionArt.getAuction().getBidCount()).isEqualTo(bidCount),

                    () -> assertThat(auctionArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(auctionArt.getArt().getName()).isEqualTo(art.getNameValue()),
                    () -> assertThat(auctionArt.getArt().getDescription()).isEqualTo(art.getDescriptionValue()),
                    () -> assertThat(auctionArt.getArt().getPrice()).isEqualTo(art.getPrice()),
                    () -> assertThat(auctionArt.getArt().getStatus()).isEqualTo(art.getStatus().getDescription()),
                    () -> assertThat(auctionArt.getArt().getStorageName()).isEqualTo(art.getStorageName()),
                    () -> assertThat(auctionArt.getArt().getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),

                    () -> assertThat(auctionArt.getOwner().id()).isEqualTo(owner.getId()),
                    () -> assertThat(auctionArt.getOwner().nickname()).isEqualTo(owner.getNicknameValue()),
                    () -> assertThat(auctionArt.getOwner().school()).isEqualTo(owner.getSchool())
            );

            if (i == 0) { // 입찰 X
                assertAll(
                        () -> assertThat(auctionArt.getArt().getLikeCount()).isEqualTo(0),
                        () -> assertThat(auctionArt.getHighestBidder()).isNull()
                );
            } else { // 입찰 O
                assertAll(
                        () -> assertThat(auctionArt.getArt().getLikeCount()).isEqualTo(1),
                        () -> assertThat(auctionArt.getHighestBidder().id()).isEqualTo(bidder.getId()),
                        () -> assertThat(auctionArt.getHighestBidder().nickname()).isEqualTo(bidder.getNicknameValue()),
                        () -> assertThat(auctionArt.getHighestBidder().school()).isEqualTo(bidder.getSchool())
                );
            }
        }
    }

    private void assertThatGeneralArtMatch(List<GeneralArt> arts) {
        for (int i = 0; i < arts.size(); i++) {
            GeneralArt generalArt = arts.get(i);
            Art art = generalArts[i];

            assertAll(
                    () -> assertThat(generalArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(generalArt.getArt().getName()).isEqualTo(art.getNameValue()),
                    () -> assertThat(generalArt.getArt().getDescription()).isEqualTo(art.getDescriptionValue()),
                    () -> assertThat(generalArt.getArt().getPrice()).isEqualTo(art.getPrice()),
                    () -> assertThat(generalArt.getArt().getStatus()).isEqualTo(art.getStatus().getDescription()),
                    () -> assertThat(generalArt.getArt().getStorageName()).isEqualTo(art.getStorageName()),
                    () -> assertThat(generalArt.getArt().getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),

                    () -> assertThat(generalArt.getOwner().id()).isEqualTo(owner.getId()),
                    () -> assertThat(generalArt.getOwner().nickname()).isEqualTo(owner.getNicknameValue()),
                    () -> assertThat(generalArt.getOwner().school()).isEqualTo(owner.getSchool())
            );

            if (i == 0) { // 구매 X
                assertAll(
                        () -> assertThat(generalArt.getArt().getLikeCount()).isEqualTo(0),
                        () -> assertThat(generalArt.getBuyer()).isNull()
                );
            } else { // 구매 O
                assertAll(
                        () -> assertThat(generalArt.getArt().getLikeCount()).isEqualTo(1),
                        () -> assertThat(generalArt.getBuyer().id()).isEqualTo(buyer.getId()),
                        () -> assertThat(generalArt.getBuyer().nickname()).isEqualTo(buyer.getNicknameValue()),
                        () -> assertThat(generalArt.getBuyer().school()).isEqualTo(buyer.getSchool())
                );
            }
        }
    }
}
