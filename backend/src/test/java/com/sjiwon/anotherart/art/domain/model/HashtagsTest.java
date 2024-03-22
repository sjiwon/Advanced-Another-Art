package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.art.exception.ArtExceptionCode;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Art -> 도메인 [Hashtags] 테스트")
class HashtagsTest extends UnitTest {
    private final Member owner = MEMBER_A.toDomain().apply(1L);
    private final Art art = GENERAL_1.toDomain(owner).apply(1L);

    @Nested
    @DisplayName("Hashtags 생성")
    class Construct {
        @Test
        @DisplayName("해시태그는 적어도 1개 이상 존재해야 한다")
        void throwExceptionByHashtagMustExistsAtLeastOne() {
            assertThatThrownBy(() -> new Hashtags(art, Set.of()))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.HASHTAG_MUST_BE_EXISTS_WITHIN_RESTRICTIONS.getMessage());
        }

        @Test
        @DisplayName("해시태그는 10개를 초과해서 존재할 수 없다")
        void throwExceptionByHashtagMustNotExistsMoreThanTen() {
            assertThatThrownBy(() -> new Hashtags(art, Set.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K")))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.HASHTAG_MUST_BE_EXISTS_WITHIN_RESTRICTIONS.getMessage());
        }

        @ParameterizedTest
        @MethodSource("hashtags")
        @DisplayName("Hashtags를 생성한다")
        void success(final Set<String> hashtags) {
            assertDoesNotThrow(() -> new Hashtags(art, hashtags));
        }

        private static Stream<Arguments> hashtags() {
            return Stream.of(
                    Arguments.of(Set.of("A")),
                    Arguments.of(Set.of("A", "B", "C")),
                    Arguments.of(Set.of("A", "B", "C", "D", "E"))
            );
        }
    }

    @Nested
    @DisplayName("Hashtags 수정")
    class Update {
        @Test
        @DisplayName("해시태그는 적어도 1개 이상 존재해야 한다")
        void throwExceptionByHashtagMustExistsAtLeastOne() {
            // given
            final Hashtags hashtags = new Hashtags(art, Set.of("A", "B", "C"));

            // when - then
            assertThatThrownBy(() -> hashtags.update(art, Set.of()))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.HASHTAG_MUST_BE_EXISTS_WITHIN_RESTRICTIONS.getMessage());
        }

        @Test
        @DisplayName("해시태그는 10개를 초과해서 존재할 수 없다")
        void throwExceptionByHashtagMustNotExistsMoreThanTen() {
            // given
            final Hashtags hashtags = new Hashtags(art, Set.of("A", "B", "C"));

            // when - then
            assertThatThrownBy(() -> hashtags.update(art, Set.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K")))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.HASHTAG_MUST_BE_EXISTS_WITHIN_RESTRICTIONS.getMessage());
        }

        @Test
        @DisplayName("Hashtags를 수정한다")
        void success() {
            // given
            final Hashtags hashtags = new Hashtags(art, Set.of("A", "B", "C"));

            // when
            final Set<String> sets = Set.of("Hello", "World", "Spring", "JPA", "JDBC");
            hashtags.update(art, sets);

            // then
            assertAll(
                    () -> assertThat(hashtags.getHashtags()).hasSize(sets.size()),
                    () -> assertThat(hashtags.getHashtags())
                            .map(Hashtag::getName)
                            .containsExactlyInAnyOrderElementsOf(sets)
            );
        }
    }
}
