package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.infra.query.dto.response.ArtDetails;
import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.GeneralArt;
import com.sjiwon.anotherart.art.service.dto.response.ArtAssembler;
import com.sjiwon.anotherart.art.utils.search.ArtDetailSearchCondition;
import com.sjiwon.anotherart.art.utils.search.Pagination;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.common.fixture.ArtFixture;
import com.sjiwon.anotherart.common.fixture.MemberFixture;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.art.utils.search.PagingConstants.getPageRequest;
import static com.sjiwon.anotherart.art.utils.search.SortType.DATE_ASC;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_C;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.OPEN_NOW;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Service Layer] -> ArtSearchService 테스트")
class ArtSearchServiceTest extends ServiceTest {
    @Autowired
    private ArtSearchService artSearchService;

    private Member owner;
    private Member buyer;
    private Member bidder;
    private final Art[] generalArts = new Art[2]; // [0] = 입찰 X, [1] = 입찰 O
    private final Art[] auctionArts = new Art[2]; // [0] = 구매 X, [1] = 구매 O
    private final Auction[] auctions = new Auction[2];

    private static final Pageable PAGE_REQUEST_1 = getPageRequest(0);
    private static final Pageable PAGE_REQUEST_2 = getPageRequest(1);

    private static final String HASHTAG_A = "A";
    private static final String KEYWORD_HELLO = "Hello";
    private static final String KEYWORD_WORLD = "World";
    private static final Set<String> HASHTAG_CONTAINS_A = Set.of("A", "C", "D", "E", "F");
    private static final Set<String> HASHTAG_CONTAINS_B = Set.of("B", "C", "D", "E", "F");

    @BeforeEach
    void setUp() {
        owner = createMember(MEMBER_A);
        buyer = createMember(MEMBER_B);
        bidder = createMember(MEMBER_C);

        initArtsAndAuctions();
    }

    private Member createMember(final MemberFixture fixture) {
        final Member member = fixture.toMember();
        member.addPointRecords(CHARGE, 100_000_000);
        return memberRepository.save(member);
    }

    private void initArtsAndAuctions() {
        final List<ArtFixture> generalArtFixtures = Arrays.stream(ArtFixture.values())
                .filter(art -> art.getType() == GENERAL)
                .toList();
        final List<ArtFixture> auctionArtFixtures = Arrays.stream(ArtFixture.values())
                .filter(art -> art.getType() == AUCTION)
                .toList();

        for (int i = 0; i < auctionArts.length; i++) {
            if (i % 2 == 0) {
                auctionArts[i] = artRepository.save(auctionArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_HELLO, i, "AUCTION"), HASHTAG_CONTAINS_A));
            } else {
                auctionArts[i] = artRepository.save(auctionArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_WORLD, i, "AUCTION"), HASHTAG_CONTAINS_B));
            }
            auctions[i] = auctionRepository.save(Auction.createAuction(auctionArts[i], OPEN_NOW.toPeriod()));
        }

        for (int i = 0; i < generalArts.length; i++) {
            if (i % 2 == 0) {
                generalArts[i] = artRepository.save(generalArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_HELLO, i, "GENERAL"), HASHTAG_CONTAINS_A));
            } else {
                generalArts[i] = artRepository.save(generalArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_WORLD, i, "GENERAL"), HASHTAG_CONTAINS_B));
            }
        }
    }

    @Nested
    @DisplayName("작품 단건 조회")
    class getArt {
        @Test
        @DisplayName("작품이 존재하지 않으면 예외가 발생한다")
        void throwExceptionByArtNotFound() {
            assertThatThrownBy(() -> artSearchService.getArt(auctionArts[0].getId() + 100L))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.ART_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("단건 경매 작품을 조회한다")
        void getAuctionArt() {
            // auctions[1] -> auctionArts[1] 입찰
            bid();

            final AuctionArt result1 = (AuctionArt) artSearchService.getArt(auctionArts[0].getId());
            final AuctionArt result2 = (AuctionArt) artSearchService.getArt(auctionArts[1].getId());
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

            final GeneralArt result1 = (GeneralArt) artSearchService.getArt(generalArts[0].getId());
            final GeneralArt result2 = (GeneralArt) artSearchService.getArt(generalArts[1].getId());
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

        private void assertThatAuctionArtMatch(final List<AuctionArt> arts, final List<Integer> bidCounts) {
            for (int i = 0; i < arts.size(); i++) {
                final AuctionArt auctionArt = arts.get(i);
                final int bidCount = bidCounts.get(i);

                final Auction auction = auctions[i];
                final Art art = auctionArts[i];

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
                            () -> assertThat(auctionArt.getArt().getLikeMembers()).hasSize(0),
                            () -> assertThat(auctionArt.getHighestBidder()).isNull()
                    );
                } else { // 입찰 O
                    assertAll(
                            () -> assertThat(auctionArt.getArt().getLikeMembers()).hasSize(1),
                            () -> assertThat(auctionArt.getHighestBidder().id()).isEqualTo(bidder.getId()),
                            () -> assertThat(auctionArt.getHighestBidder().nickname()).isEqualTo(bidder.getNicknameValue()),
                            () -> assertThat(auctionArt.getHighestBidder().school()).isEqualTo(bidder.getSchool())
                    );
                }
            }
        }

        private void assertThatGeneralArtMatch(final List<GeneralArt> arts) {
            for (int i = 0; i < arts.size(); i++) {
                final GeneralArt generalArt = arts.get(i);
                final Art art = generalArts[i];

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
                            () -> assertThat(generalArt.getArt().getLikeMembers()).hasSize(0),
                            () -> assertThat(generalArt.getBuyer()).isNull()
                    );
                } else { // 구매 O
                    assertAll(
                            () -> assertThat(generalArt.getArt().getLikeMembers()).hasSize(1),
                            () -> assertThat(generalArt.getBuyer().id()).isEqualTo(buyer.getId()),
                            () -> assertThat(generalArt.getBuyer().nickname()).isEqualTo(buyer.getNicknameValue()),
                            () -> assertThat(generalArt.getBuyer().school()).isEqualTo(buyer.getSchool())
                    );
                }
            }
        }
    }

    @Test
    @DisplayName("현재 경매가 진행중인 작품을 조회한다 [등록날짜 ASC]")
    void getActiveAuctionArts() {
        final ArtAssembler assembler1 = artSearchService.getActiveAuctionArts(DATE_ASC.getValue(), PAGE_REQUEST_1); // 2건
        final ArtAssembler assembler2 = artSearchService.getActiveAuctionArts(DATE_ASC.getValue(), PAGE_REQUEST_2); // 0건

        final Pagination pagination1 = assembler1.pagination();
        final Pagination pagination2 = assembler2.pagination();
        assertAll(
                () -> assertThat(pagination1.getCurrentPage()).isEqualTo(1),
                () -> assertThat(pagination1.getTotalPages()).isEqualTo(1),
                () -> assertThat(pagination1.getTotalElements()).isEqualTo(2),
                () -> assertThat(pagination1.isPrevExists()).isFalse(),
                () -> assertThat(pagination1.isNextExists()).isFalse(),

                () -> assertThat(pagination2.getCurrentPage()).isEqualTo(2),
                () -> assertThat(pagination2.getTotalPages()).isEqualTo(1),
                () -> assertThat(pagination2.getTotalElements()).isEqualTo(2),
                () -> assertThat(pagination2.isPrevExists()).isFalse(),
                () -> assertThat(pagination2.isNextExists()).isFalse()
        );

        asssertThatAuctionArtMatch(assembler1.result(), List.of(auctions[0], auctions[1]));
        asssertThatAuctionArtMatch(assembler2.result(), List.of());
    }

    @Nested
    @DisplayName("키워드 작품 조회")
    class findArtsByKeyword {
        private static final ArtDetailSearchCondition auctionCondition
                = new ArtDetailSearchCondition(DATE_ASC, AUCTION, KEYWORD_HELLO);
        private static final ArtDetailSearchCondition generalCondition
                = new ArtDetailSearchCondition(DATE_ASC, GENERAL, KEYWORD_HELLO);

        @Test
        @DisplayName("Hello 키워드를 포함한 경매 작품을 조회한다 [등록날짜 ASC]")
        void getAuctionArtsByKeyword() {
            final ArtAssembler assembler1 = artSearchService.getArtsByKeyword(auctionCondition, PAGE_REQUEST_1); // 1건
            final ArtAssembler assembler2 = artSearchService.getArtsByKeyword(auctionCondition, PAGE_REQUEST_2); // 0건

            final Pagination pagination1 = assembler1.pagination();
            final Pagination pagination2 = assembler2.pagination();
            assertAll(
                    () -> assertThat(pagination1.getCurrentPage()).isEqualTo(1),
                    () -> assertThat(pagination1.getTotalPages()).isEqualTo(1),
                    () -> assertThat(pagination1.getTotalElements()).isEqualTo(1),
                    () -> assertThat(pagination1.isPrevExists()).isFalse(),
                    () -> assertThat(pagination1.isNextExists()).isFalse(),

                    () -> assertThat(pagination2.getCurrentPage()).isEqualTo(2),
                    () -> assertThat(pagination2.getTotalPages()).isEqualTo(1),
                    () -> assertThat(pagination2.getTotalElements()).isEqualTo(1),
                    () -> assertThat(pagination2.isPrevExists()).isFalse(),
                    () -> assertThat(pagination2.isNextExists()).isFalse()
            );

            asssertThatAuctionArtMatch(assembler1.result(), List.of(auctions[0]));
            asssertThatAuctionArtMatch(assembler2.result(), List.of());
        }

        @Test
        @DisplayName("Hello 키워드를 포함한 일반 작품을 조회한다 [등록날짜 ASC]")
        void getGeneralArtsByKeyword() {
            final ArtAssembler assembler1 = artSearchService.getArtsByKeyword(generalCondition, PAGE_REQUEST_1); // 1건
            final ArtAssembler assembler2 = artSearchService.getArtsByKeyword(generalCondition, PAGE_REQUEST_2); // 0건

            final Pagination pagination1 = assembler1.pagination();
            final Pagination pagination2 = assembler2.pagination();
            assertAll(
                    () -> assertThat(pagination1.getCurrentPage()).isEqualTo(1),
                    () -> assertThat(pagination1.getTotalPages()).isEqualTo(1),
                    () -> assertThat(pagination1.getTotalElements()).isEqualTo(1),
                    () -> assertThat(pagination1.isPrevExists()).isFalse(),
                    () -> assertThat(pagination1.isNextExists()).isFalse(),

                    () -> assertThat(pagination2.getCurrentPage()).isEqualTo(2),
                    () -> assertThat(pagination2.getTotalPages()).isEqualTo(1),
                    () -> assertThat(pagination2.getTotalElements()).isEqualTo(1),
                    () -> assertThat(pagination2.isPrevExists()).isFalse(),
                    () -> assertThat(pagination2.isNextExists()).isFalse()
            );

            asssertThatGeneralArtMatch(assembler1.result(), List.of(generalArts[0]));
            asssertThatGeneralArtMatch(assembler2.result(), List.of());
        }
    }

    @Nested
    @DisplayName("해시태그 작품 조회")
    class findArtsByHashtag {
        private static final ArtDetailSearchCondition auctionCondition
                = new ArtDetailSearchCondition(DATE_ASC, AUCTION, HASHTAG_A);
        private static final ArtDetailSearchCondition generalCondition
                = new ArtDetailSearchCondition(DATE_ASC, GENERAL, HASHTAG_A);

        @Test
        @DisplayName("해시태그 A를 포함한 경매 작품을 조회한다 [등록날짜 ASC]")
        void getAuctionArtsByKeyword() {
            final ArtAssembler assembler1 = artSearchService.getArtsByHashtag(auctionCondition, PAGE_REQUEST_1); // 1건
            final ArtAssembler assembler2 = artSearchService.getArtsByHashtag(auctionCondition, PAGE_REQUEST_2); // 0건

            final Pagination pagination1 = assembler1.pagination();
            final Pagination pagination2 = assembler2.pagination();
            assertAll(
                    () -> assertThat(pagination1.getCurrentPage()).isEqualTo(1),
                    () -> assertThat(pagination1.getTotalPages()).isEqualTo(1),
                    () -> assertThat(pagination1.getTotalElements()).isEqualTo(1),
                    () -> assertThat(pagination1.isPrevExists()).isFalse(),
                    () -> assertThat(pagination1.isNextExists()).isFalse(),

                    () -> assertThat(pagination2.getCurrentPage()).isEqualTo(2),
                    () -> assertThat(pagination2.getTotalPages()).isEqualTo(1),
                    () -> assertThat(pagination2.getTotalElements()).isEqualTo(1),
                    () -> assertThat(pagination2.isPrevExists()).isFalse(),
                    () -> assertThat(pagination2.isNextExists()).isFalse()
            );

            asssertThatAuctionArtMatch(assembler1.result(), List.of(auctions[0]));
            asssertThatAuctionArtMatch(assembler2.result(), List.of());
        }

        @Test
        @DisplayName("해시태그 A를 포함한 일반 작품을 조회한다 [등록날짜 ASC]")
        void getGeneralArtsByKeyword() {
            final ArtAssembler assembler1 = artSearchService.getArtsByHashtag(generalCondition, PAGE_REQUEST_1); // 1건
            final ArtAssembler assembler2 = artSearchService.getArtsByHashtag(generalCondition, PAGE_REQUEST_2); // 0건

            final Pagination pagination1 = assembler1.pagination();
            final Pagination pagination2 = assembler2.pagination();
            assertAll(
                    () -> assertThat(pagination1.getCurrentPage()).isEqualTo(1),
                    () -> assertThat(pagination1.getTotalPages()).isEqualTo(1),
                    () -> assertThat(pagination1.getTotalElements()).isEqualTo(1),
                    () -> assertThat(pagination1.isPrevExists()).isFalse(),
                    () -> assertThat(pagination1.isNextExists()).isFalse(),

                    () -> assertThat(pagination2.getCurrentPage()).isEqualTo(2),
                    () -> assertThat(pagination2.getTotalPages()).isEqualTo(1),
                    () -> assertThat(pagination2.getTotalElements()).isEqualTo(1),
                    () -> assertThat(pagination2.isPrevExists()).isFalse(),
                    () -> assertThat(pagination2.isNextExists()).isFalse()
            );

            asssertThatGeneralArtMatch(assembler1.result(), List.of(generalArts[0]));
            asssertThatGeneralArtMatch(assembler2.result(), List.of());
        }
    }

    private void asssertThatAuctionArtMatch(final List<ArtDetails> result, final List<Auction> auctions) {
        final int totalSize = auctions.size();
        assertThat(result).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            final AuctionArt auctionArt = (AuctionArt) result.get(i);
            final Auction auction = auctions.get(i);
            assertThat(auctionArt.getAuction().getId()).isEqualTo(auction.getId());
        }
    }

    private void asssertThatGeneralArtMatch(final List<ArtDetails> result, final List<Art> arts) {
        final int totalSize = arts.size();
        assertThat(result).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            final GeneralArt generalArt = (GeneralArt) result.get(i);
            final Art art = arts.get(i);
            assertThat(generalArt.getArt().getId()).isEqualTo(art.getId());
        }
    }
}
