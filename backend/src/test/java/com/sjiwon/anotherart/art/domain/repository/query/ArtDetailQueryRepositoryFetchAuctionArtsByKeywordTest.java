package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> ArtDetailQueryRepository [fetchAuctionArtsByKeyword] 테스트")
public class ArtDetailQueryRepositoryFetchAuctionArtsByKeywordTest extends ArtDetailQueryRepositoryTestSupporter {
    @Nested
    @DisplayName("키워드 기반 경매 작품 리스트 조회 Query")
    class FetchAuctionArtsByKeyword {
        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 1) 등록 날짜 오름차순")
        void dateAsc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(0), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(),
                    List.of(
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice()
                    ),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(0), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(auctionArts[0].getPrice()),
                    List.of(),
                    List.of(List.of()),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 2) 등록 날짜 내림차순")
        void dateDesc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(1), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(),
                    List.of(
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice()
                    ),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(1), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(auctionArts[0].getPrice()),
                    List.of(),
                    List.of(List.of()),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 3) 최고 입찰가 오름차순")
        void priceAsc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(2), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(),
                    List.of(
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice()
                    ),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(2), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(auctionArts[0].getPrice()),
                    List.of(),
                    List.of(List.of()),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 4) 최고 입찰가 내림차순")
        void priceDesc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(3), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(),
                    List.of(
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice()
                    ),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(3), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(auctionArts[0].getPrice()),
                    List.of(),
                    List.of(List.of()),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 5) 입찰 횟수 오름차순")
        void bidCountAsc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(4), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(),
                    List.of(
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice()
                    ),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(4), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(auctionArts[0].getPrice()),
                    List.of(),
                    List.of(List.of()),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 6) 입찰 횟수 내림차순")
        void bidCountDesc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(5), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(),
                    List.of(
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice()
                    ),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(5), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(auctionArts[0].getPrice()),
                    List.of(),
                    List.of(List.of()),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 7) 좋아요 횟수 오름차순")
        void likeAsc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(6), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(),
                    List.of(
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice()
                    ),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(6), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(auctionArts[0].getPrice()),
                    List.of(),
                    List.of(List.of()),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 경매 작품 리스트를 조회한다 -> 8) 좋아요 횟수 내림차순")
        void likeDesc() {
            final Page<AuctionArt> result1 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(7), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatAuctionArtsMatch(
                    result1.getContent(),
                    List.of(),
                    List.of(
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice(),
                            auctionArts[0].getPrice()
                    ),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );

            final Page<AuctionArt> result2 = sut.fetchAuctionArtsByKeyword(auctionArtSearchConditions.get(7), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatAuctionArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(auctionArts[0].getPrice()),
                    List.of(),
                    List.of(List.of()),
                    Arrays.asList()
            );
        }
    }
}
