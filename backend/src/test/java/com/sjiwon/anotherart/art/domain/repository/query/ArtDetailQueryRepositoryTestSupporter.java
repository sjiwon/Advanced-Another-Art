package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import com.sjiwon.anotherart.art.utils.search.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.utils.search.ArtDetailsSearchCondition;
import com.sjiwon.anotherart.art.utils.search.PagingConstants;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.common.fixture.ArtFixture;
import com.sjiwon.anotherart.common.fixture.AuctionFixture;
import com.sjiwon.anotherart.common.fixture.MemberFixture;
import com.sjiwon.anotherart.favorite.domain.model.Favorite;
import com.sjiwon.anotherart.favorite.domain.repository.FavoriteRepository;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.purchase.domain.model.Purchase;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.sjiwon.anotherart.art.utils.search.SearchSortType.BID_COUNT_ASC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.BID_COUNT_DESC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.DATE_ASC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.DATE_DESC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.LIKE_ASC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.LIKE_DESC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.PRICE_ASC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.PRICE_DESC;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_10;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_11;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_12;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_3;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_4;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_5;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_6;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_7;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_8;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_9;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_10;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_11;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_12;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_2;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_3;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_4;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_5;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_6;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_7;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_8;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_9;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_10;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_2;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_3;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_4;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_5;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_6;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_7;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_8;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.DUMMY_9;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(ArtDetailQueryRepositoryImpl.class)
public abstract class ArtDetailQueryRepositoryTestSupporter extends RepositoryTest {
    @Autowired
    protected ArtDetailQueryRepositoryImpl sut;

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

    private static final int MEMBER_INIT_POINT = 2_000_000_000;
    protected Member owner;
    protected final Member[] members = new Member[10];
    protected final Art[] auctionArts = new Art[12];
    protected final Art[] generalArts = new Art[12];
    protected final Auction[] auctions = new Auction[12];

    protected final List<ActiveAuctionArtsSearchCondition> activeAuctionArtsSearchConditions = List.of(
            new ActiveAuctionArtsSearchCondition(DATE_ASC),
            new ActiveAuctionArtsSearchCondition(DATE_DESC),
            new ActiveAuctionArtsSearchCondition(PRICE_ASC),
            new ActiveAuctionArtsSearchCondition(PRICE_DESC),
            new ActiveAuctionArtsSearchCondition(BID_COUNT_ASC),
            new ActiveAuctionArtsSearchCondition(BID_COUNT_DESC),
            new ActiveAuctionArtsSearchCondition(LIKE_ASC),
            new ActiveAuctionArtsSearchCondition(LIKE_DESC)
    );
    protected final List<ArtDetailsSearchCondition> auctionArtSearchConditions = List.of(
            new ArtDetailsSearchCondition(DATE_ASC, "Hello"),
            new ArtDetailsSearchCondition(DATE_DESC, "Hello"),
            new ArtDetailsSearchCondition(PRICE_ASC, "Hello"),
            new ArtDetailsSearchCondition(PRICE_DESC, "Hello"),
            new ArtDetailsSearchCondition(BID_COUNT_ASC, "Hello"),
            new ArtDetailsSearchCondition(BID_COUNT_DESC, "Hello"),
            new ArtDetailsSearchCondition(LIKE_ASC, "Hello"),
            new ArtDetailsSearchCondition(LIKE_DESC, "Hello")
    );
    protected final List<ArtDetailsSearchCondition> generalArtSearchConditions = List.of(
            new ArtDetailsSearchCondition(DATE_ASC, "Hello"),
            new ArtDetailsSearchCondition(DATE_DESC, "Hello"),
            new ArtDetailsSearchCondition(PRICE_ASC, "Hello"),
            new ArtDetailsSearchCondition(PRICE_DESC, "Hello"),
            new ArtDetailsSearchCondition(LIKE_ASC, "Hello"),
            new ArtDetailsSearchCondition(LIKE_DESC, "Hello")
    );
    protected final Pageable pageable1 = PagingConstants.getPageable(1);
    protected final Pageable pageable2 = PagingConstants.getPageable(2);

    @BeforeEach
    void setUp() {
        initData();
    }

    private void initData() {
        owner = createMember(MEMBER_A);
        members[0] = createMember(DUMMY_1);
        members[1] = createMember(DUMMY_2);
        members[2] = createMember(DUMMY_3);
        members[3] = createMember(DUMMY_4);
        members[4] = createMember(DUMMY_5);
        members[5] = createMember(DUMMY_6);
        members[6] = createMember(DUMMY_7);
        members[7] = createMember(DUMMY_8);
        members[8] = createMember(DUMMY_9);
        members[9] = createMember(DUMMY_10);

        auctionArts[0] = createArt(AUCTION_1, "Hello-AUCTION_1", members[0], members[1], members[4], members[6], members[8]);
        auctionArts[1] = createArt(AUCTION_2, "Hello-AUCTION_2", members[2], members[5], members[6]);
        auctionArts[2] = createArt(AUCTION_3, "World-AUCTION_3", members[0], members[1], members[6], members[7], members[8], members[9]); // search non target
        auctionArts[3] = createArt(AUCTION_4, "Hello-AUCTION_4", members[1], members[5]);
        auctionArts[4] = createArt(AUCTION_5, "Hello-AUCTION_5", members[5]);
        auctionArts[5] = createArt(AUCTION_6, "Hello-AUCTION_6", members[0], members[1], members[2], members[7]);
        auctionArts[6] = createArt(AUCTION_7, "Hello-AUCTION_7", members[0], members[2], members[7], members[9]);
        auctionArts[7] = createArt(AUCTION_8, "World-AUCTION_8", members[0], members[1], members[4]); // search non target
        auctionArts[8] = createArt(AUCTION_9, "Hello-AUCTION_9", members[0], members[2], members[5], members[7], members[8]);
        auctionArts[9] = createArt(AUCTION_10, "Hello-AUCTION_10", members[0], members[2], members[5], members[6], members[7], members[8], members[9]);
        auctionArts[10] = createArt(AUCTION_11, "Hello-AUCTION_11");
        auctionArts[11] = createArt(AUCTION_12, "World-AUCTION_12", members[0], members[2], members[4], members[6]); // search non target

        generalArts[0] = createArt(GENERAL_1, "Hello-GENERAL_1", members[0], members[1]);
        generalArts[1] = createArt(GENERAL_2, "Hello-GENERAL_2", members[0], members[2], members[5], members[8]);
        generalArts[2] = createArt(GENERAL_3, "World-GENERAL_3", members[1], members[3], members[4]); // search non target
        generalArts[3] = createArt(GENERAL_4, "Hello-GENERAL_4");
        generalArts[4] = createArt(GENERAL_5, "Hello-GENERAL_5", members[0], members[3], members[4], members[7], members[9]);
        generalArts[5] = createArt(GENERAL_6, "Hello-GENERAL_6", members[1], members[2], members[4]);
        generalArts[6] = createArt(GENERAL_7, "Hello-GENERAL_7", members[2], members[3], members[5], members[6], members[7], members[8], members[9]);
        generalArts[7] = createArt(GENERAL_8, "World-GENERAL_8", members[1], members[3], members[5]); // search non target
        generalArts[8] = createArt(GENERAL_9, "Hello-GENERAL_9", members[5]);
        generalArts[9] = createArt(GENERAL_10, "Hello-GENERAL_10", members[0], members[2]);
        generalArts[10] = createArt(GENERAL_11, "Hello-GENERAL_11", members[0], members[2], members[3], members[6]);
        generalArts[11] = createArt(GENERAL_12, "World-GENERAL_12", members[1], members[2], members[4], members[6], members[8]); // search non target

        auctions[0] = createAuction(auctionArts[0], AUCTION_OPEN_NOW);
        auctions[1] = createAuction(auctionArts[1], AUCTION_OPEN_NOW); // closed
        auctions[2] = createAuction(auctionArts[2], AUCTION_OPEN_NOW);
        auctions[3] = createAuction(auctionArts[3], AUCTION_OPEN_NOW);
        auctions[4] = createAuction(auctionArts[4], AUCTION_OPEN_NOW);
        auctions[5] = createAuction(auctionArts[5], AUCTION_OPEN_NOW); // closed
        auctions[6] = createAuction(auctionArts[6], AUCTION_OPEN_NOW);
        auctions[7] = createAuction(auctionArts[7], AUCTION_OPEN_NOW);
        auctions[8] = createAuction(auctionArts[8], AUCTION_OPEN_NOW); // closed
        auctions[9] = createAuction(auctionArts[9], AUCTION_OPEN_NOW);
        auctions[10] = createAuction(auctionArts[10], AUCTION_OPEN_NOW);
        auctions[11] = createAuction(auctionArts[11], AUCTION_OPEN_NOW); // closed

        bid(auctions[0], List.of(0, 2, 6), List.of(
                auctionArts[0].getPrice() + 10_000,
                auctionArts[0].getPrice() + 20_000,
                auctionArts[0].getPrice() + 30_000
        ));
        bid(auctions[1], List.of(0, 2, 6, 3, 4, 7, 9), List.of(
                auctionArts[1].getPrice() + 10_000,
                auctionArts[1].getPrice() + 20_000,
                auctionArts[1].getPrice() + 30_000,
                auctionArts[1].getPrice() + 40_000,
                auctionArts[1].getPrice() + 50_000,
                auctionArts[1].getPrice() + 60_000,
                auctionArts[1].getPrice() + 70_000
        ));
        bid(auctions[5], List.of(0, 4), List.of(
                auctionArts[5].getPrice() + 10_000,
                auctionArts[5].getPrice() + 20_000
        ));
        bid(auctions[7], List.of(0, 2, 6, 3, 4, 5, 7, 8, 9, 1), List.of(
                auctionArts[7].getPrice() + 10_000,
                auctionArts[7].getPrice() + 20_000,
                auctionArts[7].getPrice() + 30_000,
                auctionArts[7].getPrice() + 40_000,
                auctionArts[7].getPrice() + 50_000,
                auctionArts[7].getPrice() + 60_000,
                auctionArts[7].getPrice() + 70_000,
                auctionArts[7].getPrice() + 80_000,
                auctionArts[7].getPrice() + 90_000,
                auctionArts[7].getPrice() + 100_000
        ));
        bid(auctions[8], List.of(7, 2, 6, 3, 4), List.of(
                auctionArts[8].getPrice() + 10_000,
                auctionArts[8].getPrice() + 20_000,
                auctionArts[8].getPrice() + 30_000,
                auctionArts[8].getPrice() + 40_000,
                auctionArts[8].getPrice() + 50_000
        ));
        bid(auctions[9], List.of(9, 3, 2, 1), List.of(
                auctionArts[9].getPrice() + 10_000,
                auctionArts[9].getPrice() + 20_000,
                auctionArts[9].getPrice() + 30_000,
                auctionArts[9].getPrice() + 40_000
        ));
        bid(auctions[11], List.of(0, 2, 6, 3, 1, 5, 4, 8, 7), List.of(
                auctionArts[11].getPrice() + 10_000,
                auctionArts[11].getPrice() + 20_000,
                auctionArts[11].getPrice() + 30_000,
                auctionArts[11].getPrice() + 40_000,
                auctionArts[11].getPrice() + 50_000,
                auctionArts[11].getPrice() + 60_000,
                auctionArts[11].getPrice() + 70_000,
                auctionArts[11].getPrice() + 80_000,
                auctionArts[11].getPrice() + 90_000
        ));

        purchase(generalArts[0], members[3]);
        purchase(generalArts[1], members[4]);
        purchase(generalArts[3], members[3]);
        purchase(generalArts[4], members[1]);
        purchase(generalArts[6], members[9]);
        purchase(generalArts[7], members[8]);
        purchase(generalArts[9], members[7]);
        purchase(generalArts[10], members[3]);

        closeAuction(auctions[1]);
        closeAuction(auctions[5]);
        closeAuction(auctions[8]);
        closeAuction(auctions[11]);
    }

    private Member createMember(final MemberFixture fixture) {
        final Member member = memberRepository.save(fixture.toMember());
        member.increaseTotalPoint(MEMBER_INIT_POINT);
        return member;
    }

    private Art createArt(final ArtFixture fixture, final String searchValue, final Member... likeMembers) {
        final Art art = artRepository.save(fixture.toArt(owner, searchValue, Set.of(searchValue)));
        for (final Member likeMember : likeMembers) {
            favoriteRepository.save(Favorite.favoriteMarking(art, likeMember));
        }
        return art;
    }

    private Auction createAuction(final Art art, final AuctionFixture auctionFixture) {
        return auctionRepository.save(auctionFixture.toAuction(art));
    }

    private void bid(final Auction auction, final List<Integer> bidderIndicies, final List<Integer> bidPrices) {
        for (int i = 0; i < bidderIndicies.size(); i++) {
            auction.applyNewBid(members[bidderIndicies.get(i)], bidPrices.get(i));
        }
    }

    private void purchase(final Art art, final Member buyer) {
        purchaseRepository.save(Purchase.purchaseGeneralArt(art, buyer));
    }

    private void closeAuction(final Auction auction) {
        em.createQuery("UPDATE Auction ac" +
                        " SET ac.period.startDate = :startDate, ac.period.endDate = :endDate" +
                        " WHERE ac.id = :auctionId")
                .setParameter("startDate", LocalDateTime.now().minusDays(5))
                .setParameter("endDate", LocalDateTime.now().minusDays(1))
                .setParameter("auctionId", auction.getId())
                .executeUpdate();
    }

    protected void assertThatAuctionArtsMatch(
            final List<AuctionArt> result,
            final List<Integer> artIndicies,
            final List<Integer> highestBidPrices,
            final List<Integer> bidCounts,
            final List<List<Member>> likeMembers,
            final List<Member> highestBidders
    ) {
        assertThat(result).hasSize(artIndicies.size());

        for (int i = 0; i < result.size(); i++) {
            final AuctionArt auctionArt = result.get(i);
            final Auction auction = auctions[artIndicies.get(i)];
            final Art art = auctionArts[artIndicies.get(i)];
            final int highestBidPrice = highestBidPrices.get(i);
            final int bidCount = bidCounts.get(i);
            final List<Long> likeMemberIds = likeMembers.get(i)
                    .stream()
                    .map(Member::getId)
                    .toList();
            final Member highestBidder = highestBidders.get(i);

            assertAll(
                    () -> assertThat(auctionArt.getAuctionId()).isEqualTo(auction.getId()),
                    () -> assertThat(auctionArt.getHighestBidPrice()).isEqualTo(highestBidPrice),
                    () -> assertThat(auctionArt.getBidCount()).isEqualTo(bidCount),
                    () -> assertThat(auctionArt.getArtId()).isEqualTo(art.getId()),
                    () -> assertThat(auctionArt.getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                    () -> assertThat(auctionArt.getLikeMembers()).containsExactlyInAnyOrderElementsOf(likeMemberIds),
                    () -> assertThat(auctionArt.getOwnerId()).isEqualTo(owner.getId()),
                    () -> {
                        if (highestBidder == null) {
                            assertThat(auctionArt.getHighestBidderId()).isNull();
                        } else {
                            assertThat(auctionArt.getHighestBidderId()).isEqualTo(highestBidder.getId());
                        }
                    }
            );
        }
    }

    protected void assertThatGeneralArtsMatch(
            final List<GeneralArt> result,
            final List<Integer> artIndicies,
            final List<List<Member>> likeMembers,
            final List<Member> buyers
    ) {
        assertThat(result).hasSize(artIndicies.size());

        for (int i = 0; i < result.size(); i++) {
            final GeneralArt generalArt = result.get(i);
            final Art art = generalArts[artIndicies.get(i)];
            final List<Long> likeMemberIds = likeMembers.get(i)
                    .stream()
                    .map(Member::getId)
                    .toList();
            final Member buyer = buyers.get(i);

            assertAll(
                    () -> assertThat(generalArt.getArtId()).isEqualTo(art.getId()),
                    () -> assertThat(generalArt.getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                    () -> assertThat(generalArt.getLikeMembers()).containsExactlyInAnyOrderElementsOf(likeMemberIds),
                    () -> assertThat(generalArt.getOwnerId()).isEqualTo(owner.getId()),
                    () -> {
                        if (buyer == null) {
                            assertThat(generalArt.getBuyerId()).isNull();
                        } else {
                            assertThat(generalArt.getBuyerId()).isEqualTo(buyer.getId());
                        }
                    }
            );
        }
    }
}
