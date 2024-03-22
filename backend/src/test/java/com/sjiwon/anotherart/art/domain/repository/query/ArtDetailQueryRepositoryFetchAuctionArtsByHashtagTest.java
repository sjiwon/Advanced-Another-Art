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

@DisplayName("Art -> ArtDetailQueryRepository [fetchAuctionArtsByHashtag] 테스트")
public class ArtDetailQueryRepositoryFetchAuctionArtsByHashtagTest extends ArtDetailQueryRepositoryTestSupporter {
    @Nested
    @DisplayName("해시태그 기반 경매 작품 리스트 조회 Query")
    class FetchAuctionArtsByHashtag {
        @Test
        @DisplayName("해시태그 기반 경매 작품 리스트를 조회한다 -> 1) 등록 날짜 오름차순")
        void dateAsc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(0), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(0, 1, 3, 4, 5, 6, 8, 9),
                    List.of(
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[1].getPrice() + 70_000,
                            auctionArts[3].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[5].getPrice() + 20_000,
                            auctionArts[6].getPrice(),
                            auctionArts[8].getPrice() + 50_000,
                            auctionArts[9].getPrice() + 40_000
                    ),
                    List.of(3, 7, 0, 0, 2, 0, 5, 4),
                    List.of(
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[2], members[5], members[6]),
                            List.of(members[1], members[5]),
                            List.of(members[5]),
                            List.of(members[0], members[1], members[2], members[7]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[0], members[2], members[5], members[7], members[8]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9])
                    ),
                    Arrays.asList(members[6], members[9], null, null, members[4], null, members[4], members[1])
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(0), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(10, 11),
                    List.of(
                            auctionArts[10].getPrice(),
                            auctionArts[11].getPrice() + 90_000
                    ),
                    List.of(0, 9),
                    List.of(
                            List.of(),
                            List.of(members[0], members[2], members[4], members[6])
                    ),
                    Arrays.asList(null, members[7])
            );
        }

        @Test
        @DisplayName("해시태그 기반 경매 작품 리스트를 조회한다 -> 2) 등록 날짜 내림차순")
        void dateDesc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(1), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(11, 10, 9, 8, 6, 5, 4, 3),
                    List.of(
                            auctionArts[11].getPrice() + 90_000,
                            auctionArts[10].getPrice(),
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[8].getPrice() + 50_000,
                            auctionArts[6].getPrice(),
                            auctionArts[5].getPrice() + 20_000,
                            auctionArts[4].getPrice(),
                            auctionArts[3].getPrice()
                    ),
                    List.of(9, 0, 4, 5, 0, 2, 0, 0),
                    List.of(
                            List.of(members[0], members[2], members[4], members[6]),
                            List.of(),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[2], members[5], members[7], members[8]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[0], members[1], members[2], members[7]),
                            List.of(members[5]),
                            List.of(members[1], members[5])
                    ),
                    Arrays.asList(members[7], null, members[1], members[4], null, members[4], null, null)
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(1), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(1, 0),
                    List.of(
                            auctionArts[1].getPrice() + 70_000,
                            auctionArts[0].getPrice() + 30_000
                    ),
                    List.of(7, 3),
                    List.of(
                            List.of(members[2], members[5], members[6]),
                            List.of(members[0], members[1], members[4], members[6], members[8])
                    ),
                    Arrays.asList(members[9], members[6])
            );
        }

        @Test
        @DisplayName("해시태그 기반 경매 작품 리스트를 조회한다 -> 3) 최고 입찰가 오름차순")
        void priceAsc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(2), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(3, 0, 4, 6, 5, 1, 10, 9),
                    List.of(
                            auctionArts[3].getPrice(),
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[4].getPrice(),
                            auctionArts[6].getPrice(),
                            auctionArts[5].getPrice() + 20_000,
                            auctionArts[1].getPrice() + 70_000,
                            auctionArts[10].getPrice(),
                            auctionArts[9].getPrice() + 40_000
                    ),
                    List.of(0, 3, 0, 0, 2, 7, 0, 4),
                    List.of(
                            List.of(members[1], members[5]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[5]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[0], members[1], members[2], members[7]),
                            List.of(members[2], members[5], members[6]),
                            List.of(),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9])
                    ),
                    Arrays.asList(null, members[6], null, null, members[4], members[9], null, members[1])
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(2), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(8, 11),
                    List.of(
                            auctionArts[8].getPrice() + 50_000,
                            auctionArts[11].getPrice() + 90_000
                    ),
                    List.of(5, 9),
                    List.of(
                            List.of(members[0], members[2], members[5], members[7], members[8]),
                            List.of(members[0], members[2], members[4], members[6])
                    ),
                    Arrays.asList(members[4], members[7])
            );
        }

        @Test
        @DisplayName("해시태그 기반 경매 작품 리스트를 조회한다 -> 4) 최고 입찰가 내림차순")
        void priceDesc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(3), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(11, 9, 8, 10, 1, 5, 6, 4),
                    List.of(
                            auctionArts[11].getPrice() + 90_000,
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[8].getPrice() + 50_000,
                            auctionArts[10].getPrice(),
                            auctionArts[1].getPrice() + 70_000,
                            auctionArts[5].getPrice() + 20_000,
                            auctionArts[6].getPrice(),
                            auctionArts[4].getPrice()
                    ),
                    List.of(9, 4, 5, 0, 7, 2, 0, 0),
                    List.of(
                            List.of(members[0], members[2], members[4], members[6]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[2], members[5], members[7], members[8]),
                            List.of(),
                            List.of(members[2], members[5], members[6]),
                            List.of(members[0], members[1], members[2], members[7]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[5])
                    ),
                    Arrays.asList(members[7], members[1], members[4], null, members[9], members[4], null, null)
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(3), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(3, 0),
                    List.of(
                            auctionArts[3].getPrice(),
                            auctionArts[0].getPrice() + 30_000
                    ),
                    List.of(0, 3),
                    List.of(
                            List.of(members[1], members[5]),
                            List.of(members[0], members[1], members[4], members[6], members[8])
                    ),
                    Arrays.asList(null, members[6])
            );
        }

        @Test
        @DisplayName("해시태그 기반 경매 작품 리스트를 조회한다 -> 5) 입찰 횟수 오름차순")
        void bidCountAsc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(4), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(10, 6, 4, 3, 5, 0, 9, 8),
                    List.of(
                            auctionArts[10].getPrice(),
                            auctionArts[6].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[3].getPrice(),
                            auctionArts[5].getPrice() + 20_000,
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[8].getPrice() + 50_000
                    ),
                    List.of(0, 0, 0, 0, 2, 3, 4, 5),
                    List.of(
                            List.of(),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[5]),
                            List.of(members[1], members[5]),
                            List.of(members[0], members[1], members[2], members[7]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[2], members[5], members[7], members[8])
                    ),
                    Arrays.asList(null, null, null, null, members[4], members[6], members[1], members[4])
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(4), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(1, 11),
                    List.of(
                            auctionArts[1].getPrice() + 70_000,
                            auctionArts[11].getPrice() + 90_000
                    ),
                    List.of(7, 9),
                    List.of(
                            List.of(members[2], members[5], members[6]),
                            List.of(members[0], members[2], members[4], members[6])
                    ),
                    Arrays.asList(members[9], members[7])
            );
        }

        @Test
        @DisplayName("해시태그 기반 경매 작품 리스트를 조회한다 -> 6) 입찰 횟수 내림차순")
        void bidCountDesc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(5), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(11, 1, 8, 9, 0, 5, 10, 6),
                    List.of(
                            auctionArts[11].getPrice() + 90_000,
                            auctionArts[1].getPrice() + 70_000,
                            auctionArts[8].getPrice() + 50_000,
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[5].getPrice() + 20_000,
                            auctionArts[10].getPrice(),
                            auctionArts[6].getPrice()
                    ),
                    List.of(9, 7, 5, 4, 3, 2, 0, 0),
                    List.of(
                            List.of(members[0], members[2], members[4], members[6]),
                            List.of(members[2], members[5], members[6]),
                            List.of(members[0], members[2], members[5], members[7], members[8]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[0], members[1], members[2], members[7]),
                            List.of(),
                            List.of(members[0], members[2], members[7], members[9])
                    ),
                    Arrays.asList(members[7], members[9], members[4], members[1], members[6], members[4], null, null)
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(5), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(4, 3),
                    List.of(
                            auctionArts[4].getPrice(),
                            auctionArts[3].getPrice()
                    ),
                    List.of(0, 0),
                    List.of(
                            List.of(members[5]),
                            List.of(members[1], members[5])
                    ),
                    Arrays.asList(null, null)
            );
        }

        @Test
        @DisplayName("해시태그 기반 경매 작품 리스트를 조회한다 -> 7) 좋아요 횟수 오름차순")
        void likeAsc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(6), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(10, 4, 3, 1, 11, 6, 5, 8),
                    List.of(
                            auctionArts[10].getPrice(),
                            auctionArts[4].getPrice(),
                            auctionArts[3].getPrice(),
                            auctionArts[1].getPrice() + 70_000,
                            auctionArts[11].getPrice() + 90_000,
                            auctionArts[6].getPrice(),
                            auctionArts[5].getPrice() + 20_000,
                            auctionArts[8].getPrice() + 50_000
                    ),
                    List.of(0, 0, 0, 7, 9, 0, 2, 5),
                    List.of(
                            List.of(),
                            List.of(members[5]),
                            List.of(members[1], members[5]),
                            List.of(members[2], members[5], members[6]),
                            List.of(members[0], members[2], members[4], members[6]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[0], members[1], members[2], members[7]),
                            List.of(members[0], members[2], members[5], members[7], members[8])
                    ),
                    Arrays.asList(null, null, null, members[9], members[7], null, members[4], members[4])
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(6), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(0, 9),
                    List.of(
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[9].getPrice() + 40_000
                    ),
                    List.of(3, 4),
                    List.of(
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9])
                    ),
                    Arrays.asList(members[6], members[1])
            );
        }

        @Test
        @DisplayName("해시태그 기반 경매 작품 리스트를 조회한다 -> 8) 좋아요 횟수 내림차순")
        void likeDesc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(7), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(9, 8, 0, 11, 6, 5, 1, 3),
                    List.of(
                            auctionArts[9].getPrice() + 40_000,
                            auctionArts[8].getPrice() + 50_000,
                            auctionArts[0].getPrice() + 30_000,
                            auctionArts[11].getPrice() + 90_000,
                            auctionArts[6].getPrice(),
                            auctionArts[5].getPrice() + 20_000,
                            auctionArts[1].getPrice() + 70_000,
                            auctionArts[3].getPrice()
                    ),
                    List.of(4, 5, 3, 9, 0, 2, 7, 0),
                    List.of(
                            List.of(members[0], members[2], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[0], members[2], members[5], members[7], members[8]),
                            List.of(members[0], members[1], members[4], members[6], members[8]),
                            List.of(members[0], members[2], members[4], members[6]),
                            List.of(members[0], members[2], members[7], members[9]),
                            List.of(members[0], members[1], members[2], members[7]),
                            List.of(members[2], members[5], members[6]),
                            List.of(members[1], members[5])
                    ),
                    Arrays.asList(members[1], members[4], members[6], members[7], null, members[4], members[9], null)
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByHashtag(auctionArtSearchConditions.get(7), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(4, 10),
                    List.of(
                            auctionArts[4].getPrice(),
                            auctionArts[10].getPrice()
                    ),
                    List.of(0, 0),
                    List.of(
                            List.of(members[5]),
                            List.of()
                    ),
                    Arrays.asList(null, null)
            );
        }
    }
}
