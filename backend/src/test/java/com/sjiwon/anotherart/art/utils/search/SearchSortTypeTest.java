package com.sjiwon.anotherart.art.utils.search;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.art.utils.search.SearchSortType.BID_COUNT_ASC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.BID_COUNT_DESC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.DATE_ASC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.DATE_DESC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.LIKE_ASC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.LIKE_DESC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.PRICE_ASC;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.PRICE_DESC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> SearchSortType 테스트")
class SearchSortTypeTest {
    @Nested
    @DisplayName("value로부터 SearchSortType 얻기")
    class Get {
        @Test
        @DisplayName("제공하지 않는 SortType value면 예외가 발생한다")
        void throwExceptionBySortTypeNotProvided() {
            assertThatThrownBy(() -> SearchSortType.from("anonymous"))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.SORT_TYPE_NOT_PROVIED.getMessage());
        }

        @Test
        @DisplayName("value에 해당하는 SortType을 조회한다")
        void success() {
            assertAll(
                    () -> assertThat(SearchSortType.from("date")).isEqualTo(DATE_ASC),
                    () -> assertThat(SearchSortType.from("rdate")).isEqualTo(DATE_DESC),
                    () -> assertThat(SearchSortType.from("price")).isEqualTo(PRICE_ASC),
                    () -> assertThat(SearchSortType.from("rprice")).isEqualTo(PRICE_DESC),
                    () -> assertThat(SearchSortType.from("like")).isEqualTo(LIKE_ASC),
                    () -> assertThat(SearchSortType.from("rlike")).isEqualTo(LIKE_DESC),
                    () -> assertThat(SearchSortType.from("count")).isEqualTo(BID_COUNT_ASC),
                    () -> assertThat(SearchSortType.from("rcount")).isEqualTo(BID_COUNT_DESC)
            );
        }
    }

    @Test
    @DisplayName("제공하지 않는 SortType인지 확인한다")
    void isNotSupportedSortType() {
        assertAll(
                () -> assertThat(SearchSortType.isNotSupportedSortType("anonymous")).isTrue(),
                () -> assertThat(SearchSortType.isNotSupportedSortType("date")).isFalse(),
                () -> assertThat(SearchSortType.isNotSupportedSortType("rdate")).isFalse(),
                () -> assertThat(SearchSortType.isNotSupportedSortType("price")).isFalse(),
                () -> assertThat(SearchSortType.isNotSupportedSortType("rprice")).isFalse(),
                () -> assertThat(SearchSortType.isNotSupportedSortType("like")).isFalse(),
                () -> assertThat(SearchSortType.isNotSupportedSortType("rlike")).isFalse(),
                () -> assertThat(SearchSortType.isNotSupportedSortType("count")).isFalse(),
                () -> assertThat(SearchSortType.isNotSupportedSortType("rcount")).isFalse()
        );
    }
}
