package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.GeneralArt;
import com.sjiwon.anotherart.art.domain.repository.query.spec.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.domain.repository.query.spec.ArtDetailsSearchCondition;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.service.AuctionWriter;
import com.sjiwon.anotherart.auction.domain.service.BidProcessor;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.common.fixture.ArtFixture;
import com.sjiwon.anotherart.common.fixture.AuctionFixture;
import com.sjiwon.anotherart.global.query.PageCreator;
import com.sjiwon.anotherart.like.domain.model.Like;
import com.sjiwon.anotherart.like.domain.repository.LikeRepository;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
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

import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.BID_COUNT_ASC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.BID_COUNT_DESC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.DATE_ASC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.DATE_DESC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.LIKE_ASC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.LIKE_DESC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.PRICE_ASC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.PRICE_DESC;
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
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_현재_진행;
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

@Import({
        ArtDetailJooqRepository.class,
        BidProcessor.class,
        MemberReader.class,
        AuctionWriter.class
})
public abstract class ArtDetailQueryRepositoryTestSupporter extends RepositoryTest {
    @Autowired
    protected ArtDetailJooqRepository sut;

    @Autowired
    private BidProcessor bidProcessor;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int MEMBER_INIT_POINT = 2_000_000_000;

    protected Member owner;
    protected Member[] members = new Member[10];
    protected Art[] auctionArts = new Art[12];
    protected Art[] generalArts = new Art[12];
    protected Auction[] auctions = new Auction[12];

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
    protected final Pageable pageable1 = PageCreator.create(1);
    protected final Pageable pageable2 = PageCreator.create(2);

    @BeforeEach
    void setUp() {
        initData();
    }

    private void initData() {
        owner = memberRepository.save(MEMBER_A.toDomain(MEMBER_INIT_POINT));
        members = memberRepository.saveAll(List.of(
                DUMMY_1.toDomain(MEMBER_INIT_POINT),
                DUMMY_2.toDomain(MEMBER_INIT_POINT),
                DUMMY_3.toDomain(MEMBER_INIT_POINT),
                DUMMY_4.toDomain(MEMBER_INIT_POINT),
                DUMMY_5.toDomain(MEMBER_INIT_POINT),
                DUMMY_6.toDomain(MEMBER_INIT_POINT),
                DUMMY_7.toDomain(MEMBER_INIT_POINT),
                DUMMY_8.toDomain(MEMBER_INIT_POINT),
                DUMMY_9.toDomain(MEMBER_INIT_POINT),
                DUMMY_10.toDomain(MEMBER_INIT_POINT)
        )).toArray(Member[]::new);

        auctionArts[0] = createArt(AUCTION_1, 1, "Hello", members[0], members[1], members[4], members[6], members[8]);
        auctionArts[1] = createArt(AUCTION_2, 2, "Hello", members[2], members[5], members[6]);
        auctionArts[2] = createArt(AUCTION_3, 3, "World", members[0], members[1], members[6], members[7], members[8], members[9]); // search non target
        auctionArts[3] = createArt(AUCTION_4, 4, "Hello", members[1], members[5]);
        auctionArts[4] = createArt(AUCTION_5, 5, "Hello", members[5]);
        auctionArts[5] = createArt(AUCTION_6, 6, "Hello", members[0], members[1], members[2], members[7]);
        auctionArts[6] = createArt(AUCTION_7, 7, "Hello", members[0], members[2], members[7], members[9]);
        auctionArts[7] = createArt(AUCTION_8, 8, "World", members[0], members[1], members[4]); // search non target
        auctionArts[8] = createArt(AUCTION_9, 9, "Hello", members[0], members[2], members[5], members[7], members[8]);
        auctionArts[9] = createArt(AUCTION_10, 10, "Hello", members[0], members[2], members[5], members[6], members[7], members[8], members[9]);
        auctionArts[10] = createArt(AUCTION_11, 11, "Hello");
        auctionArts[11] = createArt(AUCTION_12, 12, "Hello", members[0], members[2], members[4], members[6]);

        generalArts[0] = createArt(GENERAL_1, 13, "Hello", members[0], members[1]);
        generalArts[1] = createArt(GENERAL_2, 14, "Hello", members[0], members[2], members[5], members[8]);
        generalArts[2] = createArt(GENERAL_3, 15, "World", members[1], members[3], members[4]); // search non target
        generalArts[3] = createArt(GENERAL_4, 16, "Hello");
        generalArts[4] = createArt(GENERAL_5, 17, "Hello", members[0], members[3], members[4], members[7], members[9]);
        generalArts[5] = createArt(GENERAL_6, 18, "Hello", members[1], members[2], members[4]);
        generalArts[6] = createArt(GENERAL_7, 19, "Hello", members[2], members[3], members[5], members[6], members[7], members[8], members[9]);
        generalArts[7] = createArt(GENERAL_8, 20, "World", members[1], members[3], members[5]); // search non target
        generalArts[8] = createArt(GENERAL_9, 21, "Hello", members[5]);
        generalArts[9] = createArt(GENERAL_10, 22, "Hello", members[0], members[2]);
        generalArts[10] = createArt(GENERAL_11, 23, "Hello", members[0], members[2], members[3], members[6]);
        generalArts[11] = createArt(GENERAL_12, 24, "Hello", members[1], members[2], members[4], members[6], members[8]);

        auctions[0] = createAuction(auctionArts[0], 경매_현재_진행);
        auctions[1] = createAuction(auctionArts[1], 경매_현재_진행); // closed
        auctions[2] = createAuction(auctionArts[2], 경매_현재_진행);
        auctions[3] = createAuction(auctionArts[3], 경매_현재_진행);
        auctions[4] = createAuction(auctionArts[4], 경매_현재_진행);
        auctions[5] = createAuction(auctionArts[5], 경매_현재_진행); // closed
        auctions[6] = createAuction(auctionArts[6], 경매_현재_진행);
        auctions[7] = createAuction(auctionArts[7], 경매_현재_진행);
        auctions[8] = createAuction(auctionArts[8], 경매_현재_진행); // closed
        auctions[9] = createAuction(auctionArts[9], 경매_현재_진행);
        auctions[10] = createAuction(auctionArts[10], 경매_현재_진행);
        auctions[11] = createAuction(auctionArts[11], 경매_현재_진행); // closed

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

    private Art createArt(final ArtFixture fixture, final int index, final String searchValue, final Member... likeMembers) {
        final Art art = artRepository.save(fixture.toDomain(owner, searchValue + index, Set.of(searchValue)));
        for (final Member likeMember : likeMembers) {
            likeRepository.save(new Like(art, likeMember));
        }
        return art;
    }

    private Auction createAuction(final Art art, final AuctionFixture auctionFixture) {
        return auctionRepository.save(auctionFixture.toDomain(art));
    }

    private void bid(final Auction auction, final List<Integer> bidderIndicies, final List<Integer> bidPrices) {
        for (int i = 0; i < bidderIndicies.size(); i++) {
            final Member bidder = members[bidderIndicies.get(i)];
            final int bidPrice = bidPrices.get(i);
            bidProcessor.execute(auction, bidder, bidPrice);
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
