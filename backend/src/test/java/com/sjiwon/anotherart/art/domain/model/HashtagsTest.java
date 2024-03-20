package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.art.exception.ArtExceptionCode;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
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

@DisplayName("Art -> 도메인 [Hashtags VO] 테스트")
public class HashtagsTest {
    private final Member owner = MEMBER_A.toMember().apply(1L);
    private final Art art = GENERAL_1.toArt(owner).apply(1L);

    @Nested
    @DisplayName("Hashtags 생성")
    class Construct {
        @Test
        @DisplayName("해시태그는 적어도 1개 이상 존재해야 한다")
        void throwExceptionByHashtagMustExistsAtLeastOne() {
            assertThatThrownBy(() -> new Hashtags(art, Set.of()))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.HASHTAG_MUST_EXISTS_AT_LEAST_ONE.getMessage());
        }

        @Test
        @DisplayName("해시태그는 10개를 초과해서 존재할 수 없다")
        void throwExceptionByHashtagMustNotExistsMoreThanTen() {
            assertThatThrownBy(() -> new Hashtags(art, Set.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K")))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.HASHTAG_MUST_NOT_EXISTS_MORE_THAN_TEN.getMessage());
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
        private Hashtags hashtags;

        @BeforeEach
        void setUp() {
            hashtags = new Hashtags(art, Set.of("A", "B", "C"));
        }

        @Test
        @DisplayName("해시태그는 적어도 1개 이상 존재해야 한다")
        void throwExceptionByHashtagMustExistsAtLeastOne() {
            assertThatThrownBy(() -> hashtags.update(art, Set.of()))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.HASHTAG_MUST_EXISTS_AT_LEAST_ONE.getMessage());
        }

        @Test
        @DisplayName("해시태그는 10개를 초과해서 존재할 수 없다")
        void throwExceptionByHashtagMustNotExistsMoreThanTen() {
            assertThatThrownBy(() -> hashtags.update(art, Set.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K")))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.HASHTAG_MUST_NOT_EXISTS_MORE_THAN_TEN.getMessage());
        }

        @Test
        @DisplayName("Hashtags를 업데이트한다")
        void success() {
            // when
            hashtags.update(art, Set.of("Hello", "World", "Spring", "JPA", "JDBC"));

            // then
            assertAll(
                    () -> assertThat(hashtags.getHashtags()).hasSize(5),
                    () -> assertThat(hashtags.getHashtags())
                            .map(Hashtag::getName)
                            .containsExactlyInAnyOrder("Hello", "World", "Spring", "JPA", "JDBC")
            );
        }
    }
}
