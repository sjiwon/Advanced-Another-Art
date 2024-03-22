package com.sjiwon.anotherart.art.domain.repository.query.spec;

import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.art.exception.ArtExceptionCode;
import com.sjiwon.anotherart.common.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.BID_COUNT_ASC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.BID_COUNT_DESC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.DATE_ASC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.DATE_DESC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.LIKE_ASC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.LIKE_DESC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.PRICE_ASC;
import static com.sjiwon.anotherart.art.domain.repository.query.spec.SortType.PRICE_DESC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> SearchSortType 테스트")
class SortTypeTest extends UnitTest {
    @Nested
    @DisplayName("value로부터 SearchSortType 얻기")
    class Get {
        @Test
        @DisplayName("제공하지 않는 SortType value면 예외가 발생한다")
        void throwExceptionBySortTypeNotProvided() {
            assertThatThrownBy(() -> SortType.from("anonymous"))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.SORT_TYPE_NOT_PROVIED.getMessage());
        }

        @Test
        @DisplayName("value에 해당하는 SortType을 조회한다")
        void success() {
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
    }
}
