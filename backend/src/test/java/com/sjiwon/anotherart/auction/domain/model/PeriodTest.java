package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.auction.exception.AuctionException;
import com.sjiwon.anotherart.auction.exception.AuctionExceptionCode;
import com.sjiwon.anotherart.common.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction -> 도메인 [Period VO] 테스트")
class PeriodTest extends UnitTest {
    @Test
    @DisplayName("날짜가 입력되지 않으면 Period 생성에 실패한다")
    void throwExceptionByPeriodMustExists() {
        assertThatThrownBy(() -> Period.of(null, null))
                .isInstanceOf(AuctionException.class)
                .hasMessage(AuctionExceptionCode.PERIOD_MUST_EXISTS.getMessage());
    }

    @Test
    @DisplayName("경매 종료날짜가 시작날짜 이전이면 Period 생성에 실패한다")
    void throwExceptionByPeriodEndDateMustBeAfterStartDate() {
        final LocalDateTime startDate = LocalDateTime.now().plusDays(7);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        assertThatThrownBy(() -> Period.of(startDate, endDate))
                .isInstanceOf(AuctionException.class)
                .hasMessage(AuctionExceptionCode.AUCTION_END_DATE_MUST_BE_AFTER_START_DATE.getMessage());
    }

    @Test
    @DisplayName("Period을 생성한다")
    void construct() {
        final LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        final Period period = Period.of(startDate, endDate);

        assertAll(
                () -> assertThat(period.getStartDate()).isEqualTo(startDate),
                () -> assertThat(period.getEndDate()).isEqualTo(endDate)
        );
    }

    @Test
    @DisplayName("주어진 날짜가 Period의 StartDate ~ EndDate 사이에 포함되는지 확인한다")
    void isDateWithInRange() {
        // given
        final LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(7);
        final Period period = Period.of(startDate, endDate);

        // when
        final boolean actual1 = period.isDateWithInRange(LocalDateTime.now().plusDays(4));
        final boolean actual2 = period.isDateWithInRange(LocalDateTime.now().plusDays(8));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
