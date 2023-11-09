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
                    List.of(0, 1, 3, 4, 5, 6, 8, 9),
                    List.of(
                            List.of(members[0], members[1]),
                            List.of(members[0], members[2], members[5], members[8]),
                            List.of(),
                            List.of(members[0], members[3], members[4], members[7], members[9]),
                            List.of(members[1], members[2], members[4]),
                            List.of(members[2], members[3], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[5]),
                            List.of(members[0], members[2])
                    ),
                    Arrays.asList(members[3], members[4], members[3], members[1], null, members[9], null, members[7])
            );

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(0), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(10, 11),
                    List.of(
                            List.of(members[0], members[2], members[3], members[6]),
                            List.of(members[1], members[2], members[4], members[6], members[8])
                    ),
                    Arrays.asList(members[3], null)
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
                    List.of(11, 10, 9, 8, 6, 5, 4, 3),
                    List.of(
                            List.of(members[1], members[2], members[4], members[6], members[8]),
                            List.of(members[0], members[2], members[3], members[6]),
                            List.of(members[0], members[2]),
                            List.of(members[5]),
                            List.of(members[2], members[3], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[1], members[2], members[4]),
                            List.of(members[0], members[3], members[4], members[7], members[9]),
                            List.of()
                    ),
                    Arrays.asList(null, members[3], members[7], null, members[9], null, members[1], members[3])
            );

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(1), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(1, 0),
                    List.of(
                            List.of(members[0], members[2], members[5], members[8]),
                            List.of(members[0], members[1])
                    ),
                    Arrays.asList(members[4], members[3])
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
                    List.of(0, 1, 3, 4, 5, 6, 8, 11),
                    List.of(
                            List.of(members[0], members[1]),
                            List.of(members[0], members[2], members[5], members[8]),
                            List.of(),
                            List.of(members[0], members[3], members[4], members[7], members[9]),
                            List.of(members[1], members[2], members[4]),
                            List.of(members[2], members[3], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[5]),
                            List.of(members[1], members[2], members[4], members[6], members[8])
                    ),
                    Arrays.asList(members[3], members[4], members[3], members[1], null, members[9], null, null)
            );

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(2), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(9, 10),
                    List.of(
                            List.of(members[0], members[2]),
                            List.of(members[0], members[2], members[3], members[6])
                    ),
                    Arrays.asList(members[7], members[3])
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
                    List.of(10, 11, 9, 8, 6, 5, 4, 3),
                    List.of(
                            List.of(members[0], members[2], members[3], members[6]),
                            List.of(members[1], members[2], members[4], members[6], members[8]),
                            List.of(members[0], members[2]),
                            List.of(members[5]),
                            List.of(members[2], members[3], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[1], members[2], members[4]),
                            List.of(members[0], members[3], members[4], members[7], members[9]),
                            List.of()
                    ),
                    Arrays.asList(members[3], null, members[7], null, members[9], null, members[1], members[3])
            );

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(3), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(1, 0),
                    List.of(
                            List.of(members[0], members[2], members[5], members[8]),
                            List.of(members[0], members[1])
                    ),
                    Arrays.asList(members[4], members[3])
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
                    List.of(3, 8, 9, 0, 5, 10, 1, 11),
                    List.of(
                            List.of(),
                            List.of(members[5]),
                            List.of(members[0], members[2]),
                            List.of(members[0], members[1]),
                            List.of(members[1], members[2], members[4]),
                            List.of(members[0], members[2], members[3], members[6]),
                            List.of(members[0], members[2], members[5], members[8]),
                            List.of(members[1], members[2], members[4], members[6], members[8])
                    ),
                    Arrays.asList(members[3], null, members[7], members[3], null, members[3], members[4], null)
            );

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(4), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(4, 6),
                    List.of(
                            List.of(members[0], members[3], members[4], members[7], members[9]),
                            List.of(members[2], members[3], members[5], members[6], members[7], members[8], members[9])
                    ),
                    Arrays.asList(members[1], members[9])
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
                    List.of(6, 11, 4, 10, 1, 5, 9, 0),
                    List.of(
                            List.of(members[2], members[3], members[5], members[6], members[7], members[8], members[9]),
                            List.of(members[1], members[2], members[4], members[6], members[8]),
                            List.of(members[0], members[3], members[4], members[7], members[9]),
                            List.of(members[0], members[2], members[3], members[6]),
                            List.of(members[0], members[2], members[5], members[8]),
                            List.of(members[1], members[2], members[4]),
                            List.of(members[0], members[2]),
                            List.of(members[0], members[1])
                    ),
                    Arrays.asList(members[9], null, members[1], members[3], members[4], null, members[7], members[3])
            );

            final Page<GeneralArt> result2 = sut.fetchGeneralArtsByKeyword(generalArtSearchConditions.get(5), pageable2);
            assertAll(
                    () -> assertThat(result2.hasPrevious()).isTrue(),
                    () -> assertThat(result2.hasNext()).isFalse()
            );
            assertThatGeneralArtsMatch(
                    result2.getContent(),
                    List.of(8, 3),
                    List.of(
                            List.of(members[5]),
                            List.of()
                    ),
                    Arrays.asList(null, members[3])
            );
        }
    }
}
