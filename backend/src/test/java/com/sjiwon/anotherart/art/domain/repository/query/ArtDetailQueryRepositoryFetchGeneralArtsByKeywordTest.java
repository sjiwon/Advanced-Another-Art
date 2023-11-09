package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> ArtDetailQueryRepository [fetchGeneralArtsByKeyword] 테스트")
public class ArtDetailQueryRepositoryFetchGeneralArtsByKeywordTest extends ArtDetailQueryRepositoryTestSupporter {
    @Nested
    @DisplayName("키워드 기반 일반 작품 리스트 조회 Query")
    class FetchGeneralArtsByKeyword {
        @Test
        @DisplayName("키워드 기반 일반 작품 리스트를 조회한다 -> 1) 등록 날짜 오름차순")
        void dateAsc() {
            final Page<GeneralArt> result1 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(0), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatGeneralArtsMatch(
                    result1.getContent(),
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

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(0), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 일반 작품 리스트를 조회한다 -> 2) 등록 날짜 내림차순")
        void dateDesc() {
            final Page<GeneralArt> result1 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(1), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatGeneralArtsMatch(
                    result1.getContent(),
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

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(1), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 일반 작품 리스트를 조회한다 -> 3) 가격 오름차순")
        void priceAsc() {
            final Page<GeneralArt> result1 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(2), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatGeneralArtsMatch(
                    result1.getContent(),
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

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(2), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 일반 작품 리스트를 조회한다 -> 4) 가격 내림차순")
        void priceDesc() {
            final Page<GeneralArt> result1 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(3), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatGeneralArtsMatch(
                    result1.getContent(),
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

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(3), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 일반 작품 리스트를 조회한다 -> 5) 좋아요 횟수 오름차순")
        void likeAsc() {
            final Page<GeneralArt> result1 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(4), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatGeneralArtsMatch(
                    result1.getContent(),
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

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(4), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );
        }

        @Test
        @DisplayName("키워드 기반 일반 작품 리스트를 조회한다 -> 6) 좋아요 횟수 내림차순")
        void likeDesc() {
            final Page<GeneralArt> result1 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(5), pageable1);
            assertAll(
                    () -> assertThat(result1.hasPrevious()).isFalse(),
                    () -> assertThat(result1.hasNext()).isTrue()
            );
            assertThatGeneralArtsMatch(
                    result1.getContent(),
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

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(5), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(),
                    List.of(
                            List.of(),
                            List.of()
                    ),
                    Arrays.asList()
            );
        }
    }
}
