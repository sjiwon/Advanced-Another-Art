package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> ArtDetailQueryRepository [fetchActiveAuctionArts] 테스트")
public class ArtDetailQueryRepositoryFetchActiveAuctionArtsTest extends ArtDetailQueryRepositoryTestSupporter {
    @Nested
    @DisplayName("현재 진행중인 경매 작품 리스트 조회 Query")
    class FetchActiveAuctionArts {
        @Test
        @DisplayName("현재 진행중인 경매 작품 리스트를 조회한다 -> 1) 등록 날짜 오름차순")
        void dateAsc() {
            final Page<AuctionArt> result = sut.fetchActiveAuctionArts(activeAuctionArtsSearchConditions.get(0), pageable1);
            assertAll(
                    () -> assertThat(result.hasPrevious()).isFalse(),
                    () -> assertThat(result.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result.getContent(),
                    List.of(0, 2, 3, 4, 6, 7, 9, 10),
                    List.of(
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[2].getPrice(),
                            auctionArts[3].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[6].getPrice(),
                            auctionArts[7].getPrice() + 100_000,
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[10].getPrice()
                    ),
                    List.of(3, 0, 0, 0, 0, 10, 4, 0),
                    List.of(
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[0], members[1], members[6], members[7], members[8], members[9]),
                            List.of(members[1], members[5]),
                            List.of(members[5]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[0], members[1], members[4]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of()
                    ),
                    Arrays.asList(members[6], null, null, null, null, members[1], members[1], null)
            );
        }

        @Test
        @DisplayName("현재 진행중인 경매 작품 리스트를 조회한다 -> 2) 등록 날짜 내림차순")
        void dateDesc() {
            final Page<AuctionArt> result = sut.fetchActiveAuctionArts(activeAuctionArtsSearchConditions.get(1), pageable1);
            assertAll(
                    () -> assertThat(result.hasPrevious()).isFalse(),
                    () -> assertThat(result.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result.getContent(),
                    List.of(10, 9, 7, 6, 4, 3, 2, 0),
                    List.of(
                            auctionArts[10].getPrice(),
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[7].getPrice() + 100_000,
                            auctionArts[6].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[3].getPrice(),
                            auctionArts[2].getPrice(),
                            auctionArts[0].getPrice() + 30_000
                    ),
                    List.of(0, 4, 10, 0, 0, 0, 0, 3),
                    List.of(
                            List.of(),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[1], members[4]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[5]),
                            List.of(members[1], members[5]),
                            List.of(members[0], members[1], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[1], members[4], members[6], members[8])
                    ),
                    Arrays.asList(null, members[1], members[1], null, null, null, null, members[6])
            );
        }

        @Test
        @DisplayName("현재 진행중인 경매 작품 리스트를 조회한다 -> 3) 최고 입찰가 오름차순")
        void priceAsc() {
            final Page<AuctionArt> result = sut.fetchActiveAuctionArts(activeAuctionArtsSearchConditions.get(2), pageable1);
            assertAll(
                    () -> assertThat(result.hasPrevious()).isFalse(),
                    () -> assertThat(result.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result.getContent(),
                    List.of(2, 3, 0, 4, 6, 10, 9, 7),
                    List.of(
                            auctionArts[2].getPrice(),
                            auctionArts[3].getPrice(),
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[4].getPrice(),
                            auctionArts[6].getPrice(),
                            auctionArts[10].getPrice(),
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[7].getPrice() + 100_000
                    ),
                    List.of(0, 0, 3, 0, 0, 0, 4, 10),
                    List.of(
                            List.of(members[0], members[1], members[6], members[7], members[8], members[9]),
                            List.of(members[1], members[5]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[5]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[1], members[4])
                    ),
                    Arrays.asList(null, null, members[6], null, null, null, members[1], members[1])
            );
        }

        @Test
        @DisplayName("현재 진행중인 경매 작품 리스트를 조회한다 -> 4) 최고 입찰가 내림차순")
        void priceDesc() {
            final Page<AuctionArt> result = sut.fetchActiveAuctionArts(activeAuctionArtsSearchConditions.get(3), pageable1);
            assertAll(
                    () -> assertThat(result.hasPrevious()).isFalse(),
                    () -> assertThat(result.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result.getContent(),
                    List.of(7, 9, 10, 6, 4, 3, 0, 2),
                    List.of(
                            auctionArts[7].getPrice() + 100_000,
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[10].getPrice(),
                            auctionArts[6].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[3].getPrice(),
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[2].getPrice()
                    ),
                    List.of(10, 4, 0, 0, 0, 0, 3, 0),
                    List.of(
                            List.of(members[0], members[1], members[4]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[5]),
                            List.of(members[1], members[5]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[0], members[1], members[6], members[7], members[8], members[9])
                    ),
                    Arrays.asList(members[1], members[1], null, null, null, null, members[6], null)
            );
        }

        @Test
        @DisplayName("현재 진행중인 경매 작품 리스트를 조회한다 -> 5) 입찰 횟수 오름차순")
        void bidCountAsc() {
            final Page<AuctionArt> result = sut.fetchActiveAuctionArts(activeAuctionArtsSearchConditions.get(4), pageable1);
            assertAll(
                    () -> assertThat(result.hasPrevious()).isFalse(),
                    () -> assertThat(result.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result.getContent(),
                    List.of(10, 6, 4, 3, 2, 0, 9, 7),
                    List.of(
                            auctionArts[10].getPrice(),
                            auctionArts[6].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[3].getPrice(),
                            auctionArts[2].getPrice(),
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[7].getPrice() + 100_000
                    ),
                    List.of(0, 0, 0, 0, 0, 3, 4, 10),
                    List.of(
                            List.of(),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[5]),
                            List.of(members[1], members[5]),
                            List.of(members[0], members[1], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[1], members[4])
                    ),
                    Arrays.asList(null, null, null, null, null, members[6], members[1], members[1])
            );
        }

        @Test
        @DisplayName("현재 진행중인 경매 작품 리스트를 조회한다 -> 6) 입찰 횟수 내림차순")
        void bidCountDesc() {
            final Page<AuctionArt> result = sut.fetchActiveAuctionArts(activeAuctionArtsSearchConditions.get(5), pageable1);
            assertAll(
                    () -> assertThat(result.hasPrevious()).isFalse(),
                    () -> assertThat(result.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result.getContent(),
                    List.of(7, 9, 0, 10, 6, 4, 3, 2),
                    List.of(
                            auctionArts[7].getPrice() + 100_000,
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[10].getPrice(),
                            auctionArts[6].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[3].getPrice(),
                            auctionArts[2].getPrice()
                    ),
                    List.of(10, 4, 3, 0, 0, 0, 0, 0),
                    List.of(
                            List.of(members[0], members[1], members[4]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[5]),
                            List.of(members[1], members[5]),
                            List.of(members[0], members[1], members[6], members[7], members[8], members[9])
                    ),
                    Arrays.asList(members[1], members[1], members[6], null, null, null, null, null)
            );
        }

        @Test
        @DisplayName("현재 진행중인 경매 작품 리스트를 조회한다 -> 7) 좋아요 횟수 오름차순")
        void likeAsc() {
            final Page<AuctionArt> result = sut.fetchActiveAuctionArts(activeAuctionArtsSearchConditions.get(6), pageable1);
            assertAll(
                    () -> assertThat(result.hasPrevious()).isFalse(),
                    () -> assertThat(result.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result.getContent(),
                    List.of(10, 4, 3, 7, 6, 0, 2, 9),
                    List.of(
                            auctionArts[10].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[3].getPrice(),
                            auctionArts[7].getPrice() + 100_000,
                            auctionArts[6].getPrice(),
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[2].getPrice(),
                            auctionArts[9].getPrice() + 40_000
                    ),
                    List.of(0, 0, 0, 10, 0, 3, 0, 4),
                    List.of(
                            List.of(),
                            List.of(members[5]),
                            List.of(members[1], members[5]),
                            List.of(members[0], members[1], members[4]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[0], members[1], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9])
                    ),
                    Arrays.asList(null, null, null, members[1], null, members[6], null, members[1])
            );
        }

        @Test
        @DisplayName("현재 진행중인 경매 작품 리스트를 조회한다 -> 8) 좋아요 횟수 내림차순")
        void likeDesc() {
            final Page<AuctionArt> result = sut.fetchActiveAuctionArts(activeAuctionArtsSearchConditions.get(7), pageable1);
            assertAll(
                    () -> assertThat(result.hasPrevious()).isFalse(),
                    () -> assertThat(result.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result.getContent(),
                    List.of(9, 2, 0, 6, 7, 3, 4, 10),
                    List.of(
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[2].getPrice(),
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[6].getPrice(),
                            auctionArts[7].getPrice() + 100_000,
                            auctionArts[3].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[10].getPrice()
                    ),
                    List.of(4, 0, 3, 0, 10, 0, 0, 0),
                    List.of(
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[1], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[0], members[1], members[4]),
                            List.of(members[1], members[5]),
                            List.of(members[5]),
                            List.of()
                    ),
                    Arrays.asList(members[1], null, members[6], null, members[1], null, null, null)
            );
        }
    }
}
