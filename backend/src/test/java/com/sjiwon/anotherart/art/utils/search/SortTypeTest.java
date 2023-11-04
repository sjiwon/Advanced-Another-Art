package com.sjiwon.anotherart.art.utils.search;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.art.utils.search.SortType.BID_COUNT_ASC;
import static com.sjiwon.anotherart.art.utils.search.SortType.BID_COUNT_DESC;
import static com.sjiwon.anotherart.art.utils.search.SortType.DATE_ASC;
import static com.sjiwon.anotherart.art.utils.search.SortType.DATE_DESC;
import static com.sjiwon.anotherart.art.utils.search.SortType.LIKE_ASC;
import static com.sjiwon.anotherart.art.utils.search.SortType.LIKE_DESC;
import static com.sjiwon.anotherart.art.utils.search.SortType.PRICE_ASC;
import static com.sjiwon.anotherart.art.utils.search.SortType.PRICE_DESC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Pagination] -> SortType 테스트")
class SortTypeTest {
    @Test
    @DisplayName("제공하지 않는 SortType value면 예외가 발생한다")
    void throwExceptionBySortTypeNotProvided() {
        assertThatThrownBy(() -> SortType.from("anonymous"))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.SORT_TYPE_NOT_PROVIED.getMessage());
    }

    @Test
    @DisplayName("value에 해당하는 SortType을 조회한다")
    void getSortType() {
        assertAll(
                () -> assertThat(SortType.from("date")).isEqualTo(DATE_ASC),
                () -> assertThat(SortType.from("rdate")).isEqualTo(DATE_DESC),
                () -> assertThat(SortType.from("price")).isEqualTo(PRICE_ASC),
                () -> assertThat(SortType.from("rprice")).isEqualTo(PRICE_DESC),
                () -> assertThat(SortType.from("like")).isEqualTo(LIKE_ASC),
                () -> assertThat(SortType.from("rlike")).isEqualTo(LIKE_DESC),
                () -> assertThat(SortType.from("count")).isEqualTo(BID_COUNT_ASC),
                () -> assertThat(SortType.from("rcount")).isEqualTo(BID_COUNT_DESC)
        );
    }

    @Test
    @DisplayName("제공하는 SortType인지 확인한다")
    void isNotSupportedSortType() {
        // when
        final boolean actual1 = SortType.isNotSupportedSortType("anonymous");
        final boolean actual2 = SortType.isNotSupportedSortType("date");

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
