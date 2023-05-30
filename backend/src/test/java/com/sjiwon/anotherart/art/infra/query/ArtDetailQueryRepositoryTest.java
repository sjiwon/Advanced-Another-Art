package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.GeneralArt;
import com.sjiwon.anotherart.art.utils.search.ArtDetailSearchCondition;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.favorite.domain.FavoriteRepository;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.art.utils.search.PagingConstants.getPageRequest;
import static com.sjiwon.anotherart.art.utils.search.SortType.*;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Repository Layer] -> ArtDetailQueryRepository 테스트")
class ArtDetailQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @PersistenceContext
    private EntityManager em;

    private static final Pageable PAGE_REQUEST_1 = getPageRequest(0);
    private static final Pageable PAGE_REQUEST_2 = getPageRequest(1);
    private static final Pageable PAGE_REQUEST_3 = getPageRequest(2);

    private Member owner;
    private final Member[] bidders = new Member[20];

    @BeforeEach
    void setUp() {
        owner = memberRepository.save(MEMBER_A.toMember());
        initDummyBidders();
    }

    private void initDummyBidders() {
        List<MemberFixture> dummyFixtures = Arrays.stream(MemberFixture.values())
                .filter(member -> member.getLoginId().startsWith("dummy"))
                .toList();
        Arrays.setAll(bidders, i -> createMember(dummyFixtures.get(i)));
    }

    private Member createMember(MemberFixture fixture) {
        Member member = fixture.toMember();
        member.addPointRecords(CHARGE, 100_000_000);
        return memberRepository.save(member);
    }

    @Nested
    @DisplayName("현재 경매가 진행중인 작품 조회")
    class findActiveAuctionArts {
        private final Art[] arts = new Art[20];
        private final Auction[] auctions = new Auction[20];

        @BeforeEach
        void setUp() {
            initArts();
        }

        private void initArts() {
            List<ArtFixture> auctionArtFixtures = Arrays.stream(ArtFixture.values())
                    .filter(art -> art.getType() == AUCTION)
                    .toList();

            int oddPrice = 1000;
            int evenPrice = 2000;

            for (int i = 0; i < 20; i++) {
                if (i % 2 == 0) {
                    arts[i] = artRepository.save(auctionArtFixtures.get(i).toArt(owner, evenPrice));
                    evenPrice += 2000;
                } else {
                    arts[i] = artRepository.save(auctionArtFixtures.get(i).toArt(owner, oddPrice));
                    oddPrice += 2000;
                }
                applyLikeMarking(arts[i], i);

                auctions[i] = AUCTION_OPEN_NOW.toAuction(arts[i]);
                auctionRepository.save(auctions[i]);
                applyBid(auctions[i], i);
            }
        }

        private void applyLikeMarking(Art art, int count) {
            for (int i = 0; i < count; i++) {
                favoriteRepository.save(Favorite.favoriteMarking(art.getId(), bidders[i].getId()));
            }
        }

        private void applyBid(Auction auction, int count) {
            for (int i = 0; i < count; i++) {
                auction.applyNewBid(bidders[i], auction.getHighestBidPrice() + 100);
            }
        }

        @Test
        @DisplayName("등록 날짜 오름차순")
        void dateAsc() {
            /* 20건 active */
            Page<AuctionArt> result1 = artRepository.findActiveAuctionArts(DATE_ASC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[0], auctions[1], auctions[2], auctions[3],
                            auctions[4], auctions[5], auctions[6], auctions[7]
                    ),
                    List.of(
                            0, 1, 2, 3,
                            4, 5, 6, 7
                    )
            );

            Page<AuctionArt> result2 = artRepository.findActiveAuctionArts(DATE_ASC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(
                            auctions[8], auctions[9], auctions[10], auctions[11],
                            auctions[12], auctions[13], auctions[14], auctions[15]
                    ),
                    List.of(
                            8, 9, 10, 11,
                            12, 13, 14, 15
                    )
            );

            Page<AuctionArt> result3 = artRepository.findActiveAuctionArts(DATE_ASC, PAGE_REQUEST_3);
            assertAll(
                    () -> assertThat(result3.hasPrevious()).isTrue(),
                    () -> assertThat(result3.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result3.getContent(),
                    List.of(auctions[16], auctions[17], auctions[18], auctions[19]),
                    List.of(16, 17, 18, 19)
            );

            /* 15건 active */
            makeAuctionEnd(auctions[3], auctions[8], auctions[11], auctions[14], auctions[19]);

            Page<AuctionArt> result4 = artRepository.findActiveAuctionArts(DATE_ASC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result4.hasPrevious()).isFalse(),
                    () -> assertThat(result4.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result4.getContent(),
                    List.of(
                            auctions[0], auctions[1], auctions[2], auctions[4],
                            auctions[5], auctions[6], auctions[7], auctions[9]
                    ),
                    List.of(
                            0, 1, 2, 4,
                            5, 6, 7, 9
                    )
            );

            Page<AuctionArt> result5 = artRepository.findActiveAuctionArts(DATE_ASC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result5.hasPrevious()).isTrue(),
                    () -> assertThat(result5.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result5.getContent(),
                    List.of(
                            auctions[10], auctions[12], auctions[13], auctions[15],
                            auctions[16], auctions[17], auctions[18]
                    ),
                    List.of(
                            10, 12, 13, 15,
                            16, 17, 18
                    )
            );
        }

        @Test
        @DisplayName("등록 날짜 내림차순")
        void dateDesc() {
            /* 20건 active */
            Page<AuctionArt> result1 = artRepository.findActiveAuctionArts(DATE_DESC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[19], auctions[18], auctions[17], auctions[16],
                            auctions[15], auctions[14], auctions[13], auctions[12]
                    ),
                    List.of(
                            19, 18, 17, 16,
                            15, 14, 13, 12
                    )
            );

            Page<AuctionArt> result2 = artRepository.findActiveAuctionArts(DATE_DESC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(
                            auctions[11], auctions[10], auctions[9], auctions[8],
                            auctions[7], auctions[6], auctions[5], auctions[4]
                    ),
                    List.of(
                            11, 10, 9, 8,
                            7, 6, 5, 4
                    )
            );

            Page<AuctionArt> result3 = artRepository.findActiveAuctionArts(DATE_DESC, PAGE_REQUEST_3);
            assertAll(
                    () -> assertThat(result3.hasPrevious()).isTrue(),
                    () -> assertThat(result3.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result3.getContent(),
                    List.of(auctions[3], auctions[2], auctions[1], auctions[0]),
                    List.of(3, 2, 1, 0)
            );

            /* 15건 active */
            makeAuctionEnd(auctions[3], auctions[8], auctions[11], auctions[14], auctions[19]);

            Page<AuctionArt> result4 = artRepository.findActiveAuctionArts(DATE_DESC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result4.hasPrevious()).isFalse(),
                    () -> assertThat(result4.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result4.getContent(),
                    List.of(
                            auctions[18], auctions[17], auctions[16], auctions[15],
                            auctions[13], auctions[12], auctions[10], auctions[9]
                    ),
                    List.of(
                            18, 17, 16, 15,
                            13, 12, 10, 9
                    )
            );

            Page<AuctionArt> result5 = artRepository.findActiveAuctionArts(DATE_DESC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result5.hasPrevious()).isTrue(),
                    () -> assertThat(result5.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result5.getContent(),
                    List.of(
                            auctions[7], auctions[6], auctions[5], auctions[4],
                            auctions[2], auctions[1], auctions[0]
                    ),
                    List.of(
                            7, 6, 5, 4,
                            2, 1, 0
                    )
            );
        }

        @Test
        @DisplayName("입찰가 오름차순")
        void priceAsc() {
            /* 20건 active */
            Page<AuctionArt> result1 = artRepository.findActiveAuctionArts(PRICE_ASC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[1], auctions[0], auctions[3], auctions[2],
                            auctions[5], auctions[4], auctions[7], auctions[6]
                    ),
                    List.of(
                            1, 0, 3, 2,
                            5, 4, 7, 6
                    )
            );

            Page<AuctionArt> result2 = artRepository.findActiveAuctionArts(PRICE_ASC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(
                            auctions[9], auctions[8], auctions[11], auctions[10],
                            auctions[13], auctions[12], auctions[15], auctions[14]
                    ),
                    List.of(
                            9, 8, 11, 10,
                            13, 12, 15, 14
                    )
            );

            Page<AuctionArt> result3 = artRepository.findActiveAuctionArts(PRICE_ASC, PAGE_REQUEST_3);
            assertAll(
                    () -> assertThat(result3.hasPrevious()).isTrue(),
                    () -> assertThat(result3.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result3.getContent(),
                    List.of(auctions[17], auctions[16], auctions[19], auctions[18]),
                    List.of(17, 16, 19, 18)
            );

            /* 15건 active */
            makeAuctionEnd(auctions[3], auctions[8], auctions[11], auctions[14], auctions[19]);

            Page<AuctionArt> result4 = artRepository.findActiveAuctionArts(PRICE_ASC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result4.hasPrevious()).isFalse(),
                    () -> assertThat(result4.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result4.getContent(),
                    List.of(
                            auctions[1], auctions[0], auctions[2], auctions[5],
                            auctions[4], auctions[7], auctions[6], auctions[9]
                    ),
                    List.of(
                            1, 0, 2, 5,
                            4, 7, 6, 9
                    )
            );

            Page<AuctionArt> result5 = artRepository.findActiveAuctionArts(PRICE_ASC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result5.hasPrevious()).isTrue(),
                    () -> assertThat(result5.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result5.getContent(),
                    List.of(
                            auctions[10], auctions[13], auctions[12], auctions[15],
                            auctions[17], auctions[16], auctions[18]
                    ),
                    List.of(
                            10, 13, 12, 15,
                            17, 16, 18
                    )
            );
        }

        @Test
        @DisplayName("입찰가 내림차순")
        void priceDesc() {
            /* 20건 active */
            Page<AuctionArt> result1 = artRepository.findActiveAuctionArts(PRICE_DESC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[18], auctions[19], auctions[16], auctions[17],
                            auctions[14], auctions[15], auctions[12], auctions[13]
                    ),
                    List.of(
                            18, 19, 16, 17,
                            14, 15, 12, 13
                    )
            );

            Page<AuctionArt> result2 = artRepository.findActiveAuctionArts(PRICE_DESC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(
                            auctions[10], auctions[11], auctions[8], auctions[9],
                            auctions[6], auctions[7], auctions[4], auctions[5]
                    ),
                    List.of(
                            10, 11, 8, 9,
                            6, 7, 4, 5
                    )
            );

            Page<AuctionArt> result3 = artRepository.findActiveAuctionArts(PRICE_DESC, PAGE_REQUEST_3);
            assertAll(
                    () -> assertThat(result3.hasPrevious()).isTrue(),
                    () -> assertThat(result3.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result3.getContent(),
                    List.of(auctions[2], auctions[3], auctions[0], auctions[1]),
                    List.of(2, 3, 0, 1)
            );

            /* 15건 active */
            makeAuctionEnd(auctions[3], auctions[8], auctions[11], auctions[14], auctions[19]);

            Page<AuctionArt> result4 = artRepository.findActiveAuctionArts(PRICE_DESC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result4.hasPrevious()).isFalse(),
                    () -> assertThat(result4.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result4.getContent(),
                    List.of(
                            auctions[18], auctions[16], auctions[17], auctions[15],
                            auctions[12], auctions[13], auctions[10], auctions[9]
                    ),
                    List.of(
                            18, 16, 17, 15,
                            12, 13, 10, 9
                    )
            );

            Page<AuctionArt> result5 = artRepository.findActiveAuctionArts(PRICE_DESC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result5.hasPrevious()).isTrue(),
                    () -> assertThat(result5.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result5.getContent(),
                    List.of(
                            auctions[6], auctions[7], auctions[4], auctions[5],
                            auctions[2], auctions[0], auctions[1]
                    ),
                    List.of(
                            6, 7, 4, 5,
                            2, 0, 1
                    )
            );
        }

        @Test
        @DisplayName("찜 횟수 오름차순")
        void likeAsc() {
            /* 20건 active */
            Page<AuctionArt> result1 = artRepository.findActiveAuctionArts(LIKE_ASC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[0], auctions[1], auctions[2], auctions[3],
                            auctions[4], auctions[5], auctions[6], auctions[7]
                    ),
                    List.of(
                            0, 1, 2, 3,
                            4, 5, 6, 7
                    )
            );

            Page<AuctionArt> result2 = artRepository.findActiveAuctionArts(LIKE_ASC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(
                            auctions[8], auctions[9], auctions[10], auctions[11],
                            auctions[12], auctions[13], auctions[14], auctions[15]
                    ),
                    List.of(
                            8, 9, 10, 11,
                            12, 13, 14, 15
                    )
            );

            Page<AuctionArt> result3 = artRepository.findActiveAuctionArts(LIKE_ASC, PAGE_REQUEST_3);
            assertAll(
                    () -> assertThat(result3.hasPrevious()).isTrue(),
                    () -> assertThat(result3.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result3.getContent(),
                    List.of(auctions[16], auctions[17], auctions[18], auctions[19]),
                    List.of(16, 17, 18, 19)
            );

            /* 15건 active */
            makeAuctionEnd(auctions[3], auctions[8], auctions[11], auctions[14], auctions[19]);

            Page<AuctionArt> result4 = artRepository.findActiveAuctionArts(LIKE_ASC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result4.hasPrevious()).isFalse(),
                    () -> assertThat(result4.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result4.getContent(),
                    List.of(
                            auctions[0], auctions[1], auctions[2], auctions[4],
                            auctions[5], auctions[6], auctions[7], auctions[9]
                    ),
                    List.of(
                            0, 1, 2, 4,
                            5, 6, 7, 9
                    )
            );

            Page<AuctionArt> result5 = artRepository.findActiveAuctionArts(LIKE_ASC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result5.hasPrevious()).isTrue(),
                    () -> assertThat(result5.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result5.getContent(),
                    List.of(
                            auctions[10], auctions[12], auctions[13], auctions[15],
                            auctions[16], auctions[17], auctions[18]
                    ),
                    List.of(
                            10, 12, 13, 15,
                            16, 17, 18
                    )
            );
        }

        @Test
        @DisplayName("찜 횟수 내림차순")
        void likeDesc() {
            /* 20건 active */
            Page<AuctionArt> result1 = artRepository.findActiveAuctionArts(LIKE_DESC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[19], auctions[18], auctions[17], auctions[16],
                            auctions[15], auctions[14], auctions[13], auctions[12]
                    ),
                    List.of(
                            19, 18, 17, 16,
                            15, 14, 13, 12
                    )
            );

            Page<AuctionArt> result2 = artRepository.findActiveAuctionArts(LIKE_DESC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(
                            auctions[11], auctions[10], auctions[9], auctions[8],
                            auctions[7], auctions[6], auctions[5], auctions[4]
                    ),
                    List.of(
                            11, 10, 9, 8,
                            7, 6, 5, 4
                    )
            );

            Page<AuctionArt> result3 = artRepository.findActiveAuctionArts(LIKE_DESC, PAGE_REQUEST_3);
            assertAll(
                    () -> assertThat(result3.hasPrevious()).isTrue(),
                    () -> assertThat(result3.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result3.getContent(),
                    List.of(auctions[3], auctions[2], auctions[1], auctions[0]),
                    List.of(3, 2, 1, 0)
            );

            /* 15건 active */
            makeAuctionEnd(auctions[3], auctions[8], auctions[11], auctions[14], auctions[19]);

            Page<AuctionArt> result4 = artRepository.findActiveAuctionArts(LIKE_DESC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result4.hasPrevious()).isFalse(),
                    () -> assertThat(result4.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result4.getContent(),
                    List.of(
                            auctions[18], auctions[17], auctions[16], auctions[15],
                            auctions[13], auctions[12], auctions[10], auctions[9]
                    ),
                    List.of(
                            18, 17, 16, 15,
                            13, 12, 10, 9
                    )
            );

            Page<AuctionArt> result5 = artRepository.findActiveAuctionArts(LIKE_DESC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result5.hasPrevious()).isTrue(),
                    () -> assertThat(result5.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result5.getContent(),
                    List.of(
                            auctions[7], auctions[6], auctions[5], auctions[4],
                            auctions[2], auctions[1], auctions[0]
                    ),
                    List.of(
                            7, 6, 5, 4,
                            2, 1, 0
                    )
            );
        }

        @Test
        @DisplayName("입찰 횟수 오름차순")
        void bidCountAsc() {
            /* 20건 active */
            Page<AuctionArt> result1 = artRepository.findActiveAuctionArts(BID_COUNT_ASC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[0], auctions[1], auctions[2], auctions[3],
                            auctions[4], auctions[5], auctions[6], auctions[7]
                    ),
                    List.of(
                            0, 1, 2, 3,
                            4, 5, 6, 7
                    )
            );

            Page<AuctionArt> result2 = artRepository.findActiveAuctionArts(BID_COUNT_ASC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(
                            auctions[8], auctions[9], auctions[10], auctions[11],
                            auctions[12], auctions[13], auctions[14], auctions[15]
                    ),
                    List.of(
                            8, 9, 10, 11,
                            12, 13, 14, 15
                    )
            );

            Page<AuctionArt> result3 = artRepository.findActiveAuctionArts(BID_COUNT_ASC, PAGE_REQUEST_3);
            assertAll(
                    () -> assertThat(result3.hasPrevious()).isTrue(),
                    () -> assertThat(result3.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result3.getContent(),
                    List.of(auctions[16], auctions[17], auctions[18], auctions[19]),
                    List.of(16, 17, 18, 19)
            );

            /* 15건 active */
            makeAuctionEnd(auctions[3], auctions[8], auctions[11], auctions[14], auctions[19]);

            Page<AuctionArt> result4 = artRepository.findActiveAuctionArts(BID_COUNT_ASC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result4.hasPrevious()).isFalse(),
                    () -> assertThat(result4.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result4.getContent(),
                    List.of(
                            auctions[0], auctions[1], auctions[2], auctions[4],
                            auctions[5], auctions[6], auctions[7], auctions[9]
                    ),
                    List.of(
                            0, 1, 2, 4,
                            5, 6, 7, 9
                    )
            );

            Page<AuctionArt> result5 = artRepository.findActiveAuctionArts(BID_COUNT_ASC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result5.hasPrevious()).isTrue(),
                    () -> assertThat(result5.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result5.getContent(),
                    List.of(
                            auctions[10], auctions[12], auctions[13], auctions[15],
                            auctions[16], auctions[17], auctions[18]
                    ),
                    List.of(
                            10, 12, 13, 15,
                            16, 17, 18
                    )
            );
        }

        @Test
        @DisplayName("입찰 횟수 내림차순")
        void bidCountDesc() {
            /* 20건 active */
            Page<AuctionArt> result1 = artRepository.findActiveAuctionArts(BID_COUNT_DESC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[19], auctions[18], auctions[17], auctions[16],
                            auctions[15], auctions[14], auctions[13], auctions[12]
                    ),
                    List.of(
                            19, 18, 17, 16,
                            15, 14, 13, 12
                    )
            );

            Page<AuctionArt> result2 = artRepository.findActiveAuctionArts(BID_COUNT_DESC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(
                            auctions[11], auctions[10], auctions[9], auctions[8],
                            auctions[7], auctions[6], auctions[5], auctions[4]
                    ),
                    List.of(
                            11, 10, 9, 8,
                            7, 6, 5, 4
                    )
            );

            Page<AuctionArt> result3 = artRepository.findActiveAuctionArts(BID_COUNT_DESC, PAGE_REQUEST_3);
            assertAll(
                    () -> assertThat(result3.hasPrevious()).isTrue(),
                    () -> assertThat(result3.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result3.getContent(),
                    List.of(auctions[3], auctions[2], auctions[1], auctions[0]),
                    List.of(3, 2, 1, 0)
            );

            /* 15건 active */
            makeAuctionEnd(auctions[3], auctions[8], auctions[11], auctions[14], auctions[19]);

            Page<AuctionArt> result4 = artRepository.findActiveAuctionArts(BID_COUNT_DESC, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result4.hasPrevious()).isFalse(),
                    () -> assertThat(result4.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result4.getContent(),
                    List.of(
                            auctions[18], auctions[17], auctions[16], auctions[15],
                            auctions[13], auctions[12], auctions[10], auctions[9]
                    ),
                    List.of(
                            18, 17, 16, 15,
                            13, 12, 10, 9
                    )
            );

            Page<AuctionArt> result5 = artRepository.findActiveAuctionArts(BID_COUNT_DESC, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result5.hasPrevious()).isTrue(),
                    () -> assertThat(result5.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result5.getContent(),
                    List.of(
                            auctions[7], auctions[6], auctions[5], auctions[4],
                            auctions[2], auctions[1], auctions[0]
                    ),
                    List.of(
                            7, 6, 5, 4,
                            2, 1, 0
                    )
            );
        }

        private void makeAuctionEnd(Auction... auctions) {
            List<Long> auctionIds = Arrays.stream(auctions)
                    .map(Auction::getId)
                    .toList();

            em.createQuery("UPDATE Auction a" +
                            " SET a.period.endDate = :endDate" +
                            " WHERE a.id IN :auctionIds")
                    .setParameter("endDate", LocalDateTime.now().minusDays(1))
                    .setParameter("auctionIds", auctionIds)
                    .executeUpdate();
        }
    }

    @Nested
    @DisplayName("키워드 작품 조회 [작품명 or 작품 설명]")
    class findArtsByKeyword {
        private final Art[] generalArts = new Art[20];
        private final Art[] auctionArts = new Art[20];
        private final Auction[] auctions = new Auction[20];

        private static final String KEYWORD_HELLO = "Hello";
        private static final String KEYWORD_WORLD = "World";
        private static final Set<String> HASHTAGS = Set.of("A", "B", "C");

        @BeforeEach
        void setUp() {
            initArts();
        }

        private void initArts() {
            List<ArtFixture> auctionArtFixtures = Arrays.stream(ArtFixture.values())
                    .filter(art -> art.getType() == AUCTION)
                    .toList();
            List<ArtFixture> generalArtFixtures = Arrays.stream(ArtFixture.values())
                    .filter(art -> art.getType() == GENERAL)
                    .toList();

            for (int i = 0; i < auctionArts.length; i++) {
                if (i % 2 == 0) {
                    auctionArts[i] = artRepository.save(auctionArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_HELLO, i, "AUCTION"), HASHTAGS));
                } else {
                    auctionArts[i] = artRepository.save(auctionArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_WORLD, i, "AUCTION"), HASHTAGS));
                }
                applyLikeMarking(auctionArts[i], i);

                auctions[i] = AUCTION_OPEN_NOW.toAuction(auctionArts[i]);
                auctionRepository.save(auctions[i]);
                applyBid(auctions[i], i);
            }

            for (int i = 0; i < generalArts.length; i++) {
                if (i % 2 == 0) {
                    generalArts[i] = artRepository.save(generalArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_HELLO, i, "GENERAL"), HASHTAGS));
                } else {
                    generalArts[i] = artRepository.save(generalArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_WORLD, i, "GENERAL"), HASHTAGS));
                }
                applyLikeMarking(generalArts[i], i);
            }
        }

        private void applyLikeMarking(Art art, int count) {
            for (int i = 0; i < count; i++) {
                favoriteRepository.save(Favorite.favoriteMarking(art.getId(), bidders[i].getId()));
            }
        }

        private void applyBid(Auction auction, int count) {
            for (int i = 0; i < count; i++) {
                auction.applyNewBid(bidders[i], auction.getHighestBidPrice() + 100);
            }
        }

        @Test
        @DisplayName("Hello라는 키워드를 포함한 경매 작품을 조회한다")
        void findAuctionArtsByKeyword() {
            final ArtDetailSearchCondition condition = new ArtDetailSearchCondition(DATE_ASC, AUCTION, KEYWORD_HELLO);

            /* 1. 경매 작품 8건 fetching */
            Page<AuctionArt> result1 = artRepository.findAuctionArtsByKeyword(condition, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[0], auctions[2], auctions[4], auctions[6],
                            auctions[8], auctions[10], auctions[12], auctions[14]
                    ),
                    List.of(
                            0, 2, 4, 6,
                            8, 10, 12, 14
                    )
            );

            /* 2. 경매 작품 2건 fetching */
            Page<AuctionArt> result2 = artRepository.findAuctionArtsByKeyword(condition, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(auctions[16], auctions[18]),
                    List.of(16, 18)
            );
        }

        @Test
        @DisplayName("Hello라는 키워드를 포함한 일반 작품을 조회한다")
        void findGeneralArtsByKeyword() {
            final ArtDetailSearchCondition condition = new ArtDetailSearchCondition(DATE_ASC, GENERAL, KEYWORD_HELLO);

            /* 1. 일반 작품 8건 fetching */
            Page<GeneralArt> result1 = artRepository.findGeneralArtsByKeyword(condition, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingGeneralArtMatch(
                    result1.getContent(),
                    List.of(
                            generalArts[0], generalArts[2], generalArts[4], generalArts[6],
                            generalArts[8], generalArts[10], generalArts[12], generalArts[14]
                    ),
                    List.of(
                            0, 2, 4, 6,
                            8, 10, 12, 14
                    )
            );

            /* 2. 일반o 작품 2건 fetching */
            Page<GeneralArt> result2 = artRepository.findGeneralArtsByKeyword(condition, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatPagingGeneralArtMatch(
                    result2.getContent(),
                    List.of(generalArts[16], generalArts[18]),
                    List.of(16, 18)
            );
        }
    }

    @Nested
    @DisplayName("해시태그 작품 조회")
    class findArtsByHashtag {
        private final Art[] generalArts = new Art[20];
        private final Art[] auctionArts = new Art[20];
        private final Auction[] auctions = new Auction[20];

        private static final String HASHTAG_A = "A";
        private static final String KEYWORD_HELLO = "Hello";
        private static final String KEYWORD_WORLD = "World";
        private static final Set<String> HASHTAG_CONTAINS_A = Set.of("A", "C", "D", "E", "F");
        private static final Set<String> HASHTAG_CONTAINS_B = Set.of("B", "C", "D", "E", "F");

        @BeforeEach
        void setUp() {
            initArts();
        }

        private void initArts() {
            List<ArtFixture> auctionArtFixtures = Arrays.stream(ArtFixture.values())
                    .filter(art -> art.getType() == AUCTION)
                    .toList();
            List<ArtFixture> generalArtFixtures = Arrays.stream(ArtFixture.values())
                    .filter(art -> art.getType() == GENERAL)
                    .toList();

            for (int i = 0; i < auctionArts.length; i++) {
                if (i % 2 == 0) {
                    auctionArts[i] = artRepository.save(auctionArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_HELLO, i, "AUCTION"), HASHTAG_CONTAINS_A));
                } else {
                    auctionArts[i] = artRepository.save(auctionArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_WORLD, i, "AUCTION"), HASHTAG_CONTAINS_B));
                }
                applyLikeMarking(auctionArts[i], i);

                auctions[i] = AUCTION_OPEN_NOW.toAuction(auctionArts[i]);
                auctionRepository.save(auctions[i]);
                applyBid(auctions[i], i);
            }

            for (int i = 0; i < generalArts.length; i++) {
                if (i % 2 == 0) {
                    generalArts[i] = artRepository.save(generalArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_HELLO, i, "GENERAL"), HASHTAG_CONTAINS_A));
                } else {
                    generalArts[i] = artRepository.save(generalArtFixtures.get(i).toArt(owner, String.format("%s %d %s", KEYWORD_WORLD, i, "GENERAL"), HASHTAG_CONTAINS_B));
                }
                applyLikeMarking(generalArts[i], i);
            }
        }

        private void applyLikeMarking(Art art, int count) {
            for (int i = 0; i < count; i++) {
                favoriteRepository.save(Favorite.favoriteMarking(art.getId(), bidders[i].getId()));
            }
        }

        private void applyBid(Auction auction, int count) {
            for (int i = 0; i < count; i++) {
                auction.applyNewBid(bidders[i], auction.getHighestBidPrice() + 100);
            }
        }

        @Test
        @DisplayName("해시태그 A를 포함한 경매 작품을 조회한다")
        void findAuctionArtsByHashtag() {
            final ArtDetailSearchCondition condition = new ArtDetailSearchCondition(DATE_ASC, AUCTION, HASHTAG_A);

            /* 1. 경매 작품 8건 fetching */
            Page<AuctionArt> result1 = artRepository.findAuctionArtsByHashtag(condition, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingAuctionArtMatch(
                    result1.getContent(),
                    List.of(
                            auctions[0], auctions[2], auctions[4], auctions[6],
                            auctions[8], auctions[10], auctions[12], auctions[14]
                    ),
                    List.of(
                            0, 2, 4, 6,
                            8, 10, 12, 14
                    )
            );

            /* 2. 경매 작품 2건 fetching */
            Page<AuctionArt> result2 = artRepository.findAuctionArtsByHashtag(condition, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatPagingAuctionArtMatch(
                    result2.getContent(),
                    List.of(auctions[16], auctions[18]),
                    List.of(16, 18)
            );
        }

        @Test
        @DisplayName("해시태그 A를 포함한 일반 작품을 조회한다")
        void findGeneralArtsByHashtag() {
            final ArtDetailSearchCondition condition = new ArtDetailSearchCondition(DATE_ASC, GENERAL, HASHTAG_A);

            /* 1. 일반 작품 8건 fetching */
            Page<GeneralArt> result1 = artRepository.findGeneralArtsByHashtag(condition, PAGE_REQUEST_1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatPagingGeneralArtMatch(
                    result1.getContent(),
                    List.of(
                            generalArts[0], generalArts[2], generalArts[4], generalArts[6],
                            generalArts[8], generalArts[10], generalArts[12], generalArts[14]
                    ),
                    List.of(
                            0, 2, 4, 6,
                            8, 10, 12, 14
                    )
            );

            /* 2. 일반o 작품 2건 fetching */
            Page<GeneralArt> result2 = artRepository.findGeneralArtsByHashtag(condition, PAGE_REQUEST_2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatPagingGeneralArtMatch(
                    result2.getContent(),
                    List.of(generalArts[16], generalArts[18]),
                    List.of(16, 18)
            );
        }
    }

    private void assertThatPagingAuctionArtMatch(List<AuctionArt> content, List<Auction> auctions, List<Integer> counts) {
        int totalSize = auctions.size();
        assertThat(content).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            AuctionArt auctionArt = content.get(i);
            Auction auction = auctions.get(i);
            Integer count = counts.get(i);

            assertAll(
                    () -> assertThat(auctionArt.getAuction().getId()).isEqualTo(auction.getId()),
                    () -> assertThat(auctionArt.getAuction().getBidCount()).isEqualTo(count),
                    () -> assertThat(auctionArt.getArt().getId()).isEqualTo(auction.getArt().getId()),
                    () -> assertThat(auctionArt.getArt().getLikeMembers()).hasSize(count)
            );
        }
    }

    private void assertThatPagingGeneralArtMatch(List<GeneralArt> content, List<Art> arts, List<Integer> counts) {
        int totalSize = arts.size();
        assertThat(content).hasSize(totalSize);

        for (int i = 0; i < totalSize; i++) {
            GeneralArt auctionArt = content.get(i);
            Art art = arts.get(i);
            Integer count = counts.get(i);

            assertAll(
                    () -> assertThat(auctionArt.getArt().getId()).isEqualTo(art.getId()),
                    () -> assertThat(auctionArt.getArt().getLikeMembers()).hasSize(count)
            );
        }
    }
}