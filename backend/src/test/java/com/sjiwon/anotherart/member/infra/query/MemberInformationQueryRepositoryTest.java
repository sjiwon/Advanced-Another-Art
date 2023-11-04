package com.sjiwon.anotherart.member.infra.query;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.favorite.domain.FavoriteRepository;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.infra.query.dto.response.TradedArt;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_C;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static com.sjiwon.anotherart.member.domain.point.PointType.PURCHASE;
import static com.sjiwon.anotherart.member.domain.point.PointType.REFUND;
import static com.sjiwon.anotherart.member.domain.point.PointType.SOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Repository Layer] -> MemberInformationQueryRepository 테스트")
class MemberInformationQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @PersistenceContext
    private EntityManager em;

    private Member member;
    private Member owner;
    private Member buyer;
    private final Member[] bidders = new Member[10];
    private final Art[] generalArts = new Art[10];
    private final Art[] auctionArts = new Art[10];
    private final Auction[] auctions = new Auction[10];

    @BeforeEach
    void setUp() {
        member = createMember(MEMBER_A);
        owner = createMember(MEMBER_B);
        buyer = createMember(MEMBER_C);

        initDummyBidders();
        initArtsAndAuctions();
    }

    private Member createMember(final MemberFixture fixture) {
        final Member member = fixture.toMember();
        member.addPointRecords(CHARGE, 100_000_000);
        return memberRepository.save(member);
    }

    private void initDummyBidders() {
        final List<MemberFixture> dummyFixtures = Arrays.stream(MemberFixture.values())
                .filter(member -> member.getLoginId().startsWith("dummy"))
                .toList();
        Arrays.setAll(bidders, i -> createMember(dummyFixtures.get(i)));
    }

    private void initArtsAndAuctions() {
        final List<ArtFixture> generalArtFixtures = Arrays.stream(ArtFixture.values())
                .filter(art -> art.getType() == GENERAL)
                .toList();
        final List<ArtFixture> auctionArtFixtures = Arrays.stream(ArtFixture.values())
                .filter(art -> art.getType() == AUCTION)
                .toList();
        Arrays.setAll(generalArts, i -> artRepository.save(generalArtFixtures.get(i).toArt(owner)));
        Arrays.setAll(auctionArts, i -> artRepository.save(auctionArtFixtures.get(i).toArt(owner)));
        Arrays.setAll(auctions, i -> auctionRepository.save(AUCTION_OPEN_NOW.toAuction(auctionArts[i])));
    }

    @Test
    @DisplayName("사용자의 포인트 활용 내역을 조회한다")
    void findPointRecordByMemberId() {
        // given
        member.addPointRecords(CHARGE, 100_000);
        member.addPointRecords(CHARGE, 200_000);
        member.addPointRecords(PURCHASE, 30_000);
        member.addPointRecords(PURCHASE, 50_000);
        member.addPointRecords(REFUND, 50_000);
        member.addPointRecords(SOLD, 100_000);
        member.addPointRecords(CHARGE, 500_000);

        // when
        final List<MemberPointRecord> result = memberRepository.findPointRecordByMemberId(member.getId());

        // then
        assertAll(
                () -> assertThat(result).hasSize(8),
                () -> assertThat(result)
                        .map(MemberPointRecord::getPointType)
                        .containsExactly(
                                CHARGE.getDescription(),
                                SOLD.getDescription(),
                                REFUND.getDescription(),
                                PURCHASE.getDescription(),
                                PURCHASE.getDescription(),
                                CHARGE.getDescription(),
                                CHARGE.getDescription(),
                                CHARGE.getDescription()
                        ),
                () -> assertThat(result)
                        .map(MemberPointRecord::getAmount)
                        .containsExactly(
                                500_000,
                                100_000,
                                50_000,
                                50_000,
                                30_000,
                                200_000,
                                100_000,
                                100_000_000
                        )
        );
    }

    @Test
    @DisplayName("낙찰된 경매 작품을 조회한다")
    void findWinningAuctionArtByMemberId() {
        /* 7건 입찰 & 3건 낙찰 */
        bid(
                List.of(10, 4, 7, 6, 10, 10, 3),
                List.of(15, 20, 13, 3, 4, 8, 9),
                List.of(auctions[0], auctions[2], auctions[3], auctions[5], auctions[6], auctions[8], auctions[9])
        );
        makeAuctionEnd(auctions[0], auctions[2], auctions[3], auctions[5], auctions[6], auctions[8], auctions[9]);

        final List<AuctionArt> result1 = memberRepository.findWinningAuctionArtByMemberId(bidders[0].getId());
        assertThatWinningAuctionMatch(
                result1,
                List.of(8, 6, 0),
                List.of(8, 4, 15)
        );

        /* 추가 3건 입찰 & 2건 낙찰 */
        bid(
                List.of(10, 3, 10),
                List.of(10, 8, 23),
                List.of(auctions[1], auctions[4], auctions[7])
        );
        makeAuctionEnd(auctions[1], auctions[4], auctions[7]);

        final List<AuctionArt> result2 = memberRepository.findWinningAuctionArtByMemberId(bidders[0].getId());
        assertThatWinningAuctionMatch(
                result2,
                List.of(8, 7, 6, 1, 0),
                List.of(8, 23, 4, 10, 15)
        );
    }

    @Test
    @DisplayName("판매한 작품을 조회한다")
    void findSoldArtByMemberIdAndType() {
        /* 경매 작품 3건, 일반 작품 5건 */
        purchaseAuctionArt(auctionArts[0], auctionArts[5], auctionArts[8]);
        purchaseGeneralArt(generalArts[0], generalArts[1], generalArts[3], generalArts[6], generalArts[9]);

        final List<TradedArt> auctionResult1 = memberRepository.findSoldArtByMemberIdAndType(owner.getId(), AUCTION);
        final List<TradedArt> generalResult1 = memberRepository.findSoldArtByMemberIdAndType(owner.getId(), GENERAL);
        assertThatTradedArtMatch(
                auctionResult1,
                List.of(8, 5, 0),
                generalResult1,
                List.of(9, 6, 3, 1, 0)
        );

        /* 경매 작품 추가 4건, 일반 작품 추가 1건 */
        purchaseAuctionArt(auctionArts[1], auctionArts[2], auctionArts[4], auctionArts[9]);
        purchaseGeneralArt(generalArts[5]);

        final List<TradedArt> auctionResult2 = memberRepository.findSoldArtByMemberIdAndType(owner.getId(), AUCTION);
        final List<TradedArt> generalResult2 = memberRepository.findSoldArtByMemberIdAndType(owner.getId(), GENERAL);
        assertThatTradedArtMatch(
                auctionResult2,
                List.of(9, 8, 5, 4, 2, 1, 0),
                generalResult2,
                List.of(9, 6, 5, 3, 1, 0)
        );
    }

    @Test
    @DisplayName("구매한 작품을 조회한다")
    void findPurchaseArtByMemberIdAndType() {
        /* 경매 작품 3건, 일반 작품 5건 */
        purchaseAuctionArt(auctionArts[0], auctionArts[5], auctionArts[8]);
        purchaseGeneralArt(generalArts[0], generalArts[1], generalArts[3], generalArts[6], generalArts[9]);

        final List<TradedArt> auctionResult1 = memberRepository.findPurchaseArtByMemberIdAndType(buyer.getId(), AUCTION);
        final List<TradedArt> generalResult1 = memberRepository.findPurchaseArtByMemberIdAndType(buyer.getId(), GENERAL);
        assertThatTradedArtMatch(
                auctionResult1,
                List.of(8, 5, 0),
                generalResult1,
                List.of(9, 6, 3, 1, 0)
        );

        /* 경매 작품 추가 4건, 일반 작품 추가 1건 */
        purchaseAuctionArt(auctionArts[1], auctionArts[2], auctionArts[4], auctionArts[9]);
        purchaseGeneralArt(generalArts[5]);

        final List<TradedArt> auctionResult2 = memberRepository.findPurchaseArtByMemberIdAndType(buyer.getId(), AUCTION);
        final List<TradedArt> generalResult2 = memberRepository.findPurchaseArtByMemberIdAndType(buyer.getId(), GENERAL);
        assertThatTradedArtMatch(
                auctionResult2,
                List.of(9, 8, 5, 4, 2, 1, 0),
                generalResult2,
                List.of(9, 6, 5, 3, 1, 0)
        );
    }

    private void bid(final List<Integer> bidCounts, final List<Integer> likeCounts, final List<Auction> auctions) {
        for (int i = 0; i < bidCounts.size(); i++) {
            final int bidCount = bidCounts.get(i);
            final int likeCount = likeCounts.get(i);
            final Auction auction = auctions.get(i);

            for (int index = bidders.length - 1; index >= bidders.length - bidCount; index--) {
                final Member bidder = bidders[index];
                auction.applyNewBid(bidder, auction.getHighestBidPrice() + 50_000);
            }

            for (long index = 1; index <= likeCount; index++) {
                favoriteRepository.save(Favorite.favoriteMarking(auction.getArt().getId(), index));
            }
        }
    }

    private void makeAuctionEnd(final Auction... auctions) {
        final List<Long> auctionIds = Arrays.stream(auctions)
                .map(Auction::getId)
                .toList();

        em.createQuery("UPDATE Auction a" +
                        " SET a.period.endDate = :endDate" +
                        " WHERE a.id IN :auctionIds")
                .setParameter("endDate", LocalDateTime.now().minusDays(1))
                .setParameter("auctionIds", auctionIds)
                .executeUpdate();
    }

    private void purchaseAuctionArt(final Art... arts) {
        for (final Art art : arts) {
            purchaseRepository.save(Purchase.purchaseAuctionArt(art, buyer, art.getPrice()));
            favoriteRepository.save(Favorite.favoriteMarking(art.getId(), 1L));
        }
    }

    private void purchaseGeneralArt(final Art... arts) {
        for (final Art art : arts) {
            purchaseRepository.save(Purchase.purchaseGeneralArt(art, buyer));
            favoriteRepository.save(Favorite.favoriteMarking(art.getId(), 1L));
        }
    }

    private void assertThatWinningAuctionMatch(final List<AuctionArt> result, final List<Integer> indices, final List<Integer> likeCounts) {
        final int totalSize = indices.size();
        assertThat(result).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            final AuctionArt auctionArt = result.get(i);
            final int index = indices.get(i);
            final int likeCount = likeCounts.get(i);

            final Auction auction = auctions[index];
            final Art art = auctionArts[index];

            assertAll(
                    () -> assertThat(auctionArt.getAuction().getId()).isEqualTo(auction.getId()),
                    () -> assertThat(auctionArt.getAuction().getHighestBidPrice()).isEqualTo(auction.getHighestBidPrice()),
                    () -> assertThat(auctionArt.getAuction().getBidCount()).isEqualTo(10),

                    () -> assertThat(auctionArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(auctionArt.getArt().getName()).isEqualTo(art.getNameValue()),
                    () -> assertThat(auctionArt.getArt().getDescription()).isEqualTo(art.getDescriptionValue()),
                    () -> assertThat(auctionArt.getArt().getPrice()).isEqualTo(art.getPrice()),
                    () -> assertThat(auctionArt.getArt().getStatus()).isEqualTo(art.getStatus().getDescription()),
                    () -> assertThat(auctionArt.getArt().getStorageName()).isEqualTo(art.getStorageName()),
                    () -> assertThat(auctionArt.getArt().getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                    () -> assertThat(auctionArt.getArt().getLikeMembers()).hasSize(likeCount),

                    () -> assertThat(auctionArt.getOwner().id()).isEqualTo(owner.getId()),
                    () -> assertThat(auctionArt.getOwner().nickname()).isEqualTo(owner.getNicknameValue()),
                    () -> assertThat(auctionArt.getOwner().school()).isEqualTo(owner.getSchool()),

                    () -> assertThat(auctionArt.getHighestBidder().id()).isEqualTo(bidders[0].getId()),
                    () -> assertThat(auctionArt.getHighestBidder().nickname()).isEqualTo(bidders[0].getNicknameValue()),
                    () -> assertThat(auctionArt.getHighestBidder().school()).isEqualTo(bidders[0].getSchool())
            );
        }
    }

    private void assertThatTradedArtMatch(final List<TradedArt> auctionResult, final List<Integer> auctionIndices,
                                          final List<TradedArt> generalResult, final List<Integer> generalIndices) {
        final int auctionTotalSize = auctionIndices.size();
        final int generalTotalSize = generalIndices.size();
        assertThat(auctionResult).hasSize(auctionTotalSize);
        assertThat(generalResult).hasSize(generalTotalSize);

        for (int i = 0; i < auctionTotalSize; i++) {
            final TradedArt tradedArt = auctionResult.get(i);
            final int index = auctionIndices.get(i);

            final Art art = auctionArts[index];

            assertAll(
                    () -> assertThat(tradedArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(tradedArt.getArt().getName()).isEqualTo(art.getNameValue()),
                    () -> assertThat(tradedArt.getArt().getDescription()).isEqualTo(art.getDescriptionValue()),
                    () -> assertThat(tradedArt.getArt().getPrice()).isEqualTo(art.getPrice()),
                    () -> assertThat(tradedArt.getArt().getStatus()).isEqualTo(art.getStatus().getDescription()),
                    () -> assertThat(tradedArt.getArt().getStorageName()).isEqualTo(art.getStorageName()),
                    () -> assertThat(tradedArt.getArt().getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                    () -> assertThat(tradedArt.getArt().getLikeMembers()).hasSize(1),

                    () -> assertThat(tradedArt.getOwner().id()).isEqualTo(owner.getId()),
                    () -> assertThat(tradedArt.getOwner().nickname()).isEqualTo(owner.getNicknameValue()),
                    () -> assertThat(tradedArt.getOwner().school()).isEqualTo(owner.getSchool()),

                    () -> assertThat(tradedArt.getBuyer().id()).isEqualTo(buyer.getId()),
                    () -> assertThat(tradedArt.getBuyer().nickname()).isEqualTo(buyer.getNicknameValue()),
                    () -> assertThat(tradedArt.getBuyer().school()).isEqualTo(buyer.getSchool())
            );
        }

        for (int i = 0; i < generalTotalSize; i++) {
            final TradedArt tradedArt = generalResult.get(i);
            final int index = generalIndices.get(i);

            final Art art = generalArts[index];

            assertAll(
                    () -> assertThat(tradedArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(tradedArt.getArt().getName()).isEqualTo(art.getNameValue()),
                    () -> assertThat(tradedArt.getArt().getDescription()).isEqualTo(art.getDescriptionValue()),
                    () -> assertThat(tradedArt.getArt().getPrice()).isEqualTo(art.getPrice()),
                    () -> assertThat(tradedArt.getArt().getStatus()).isEqualTo(art.getStatus().getDescription()),
                    () -> assertThat(tradedArt.getArt().getStorageName()).isEqualTo(art.getStorageName()),
                    () -> assertThat(tradedArt.getArt().getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                    () -> assertThat(tradedArt.getArt().getLikeMembers()).hasSize(1),

                    () -> assertThat(tradedArt.getOwner().id()).isEqualTo(owner.getId()),
                    () -> assertThat(tradedArt.getOwner().nickname()).isEqualTo(owner.getNicknameValue()),
                    () -> assertThat(tradedArt.getOwner().school()).isEqualTo(owner.getSchool()),

                    () -> assertThat(tradedArt.getBuyer().id()).isEqualTo(buyer.getId()),
                    () -> assertThat(tradedArt.getBuyer().nickname()).isEqualTo(buyer.getNicknameValue()),
                    () -> assertThat(tradedArt.getBuyer().school()).isEqualTo(buyer.getSchool())
            );
        }
    }
}
