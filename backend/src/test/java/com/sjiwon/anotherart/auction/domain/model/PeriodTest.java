package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.sjiwon.anotherart.common.fixture.PeriodFixture.CLOSED_WEEK_1_AGO;
import static com.sjiwon.anotherart.common.fixture.PeriodFixture.OPEN_NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auction 도메인 {Period VO} 테스트")
class PeriodTest {
    @Test
    @DisplayName("날짜가 입력되지 않으면 Period 생성에 실패한다")
    void throwExceptionByPeriodMustExists() {
        assertThatThrownBy(() -> Period.of(null, null))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.PERIOD_MUST_EXISTS.getMessage());
    }

    @Test
    @DisplayName("경매 종료날짜가 시작날짜 이전이면 Period 생성에 실패한다")
    void throwExceptionByPeriodEndDateMustBeAfterStartDate() {
        final LocalDateTime startDate = LocalDateTime.now().plusDays(7);
        final LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        assertThatThrownBy(() -> Period.of(startDate, endDate))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.AUCTION_END_DATE_MUST_BE_AFTER_START_DATE.getMessage());
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

    @Test
    @DisplayName("경매가 종료되었는지 확인한다")
    void isAuctionFinished() {
        // given
        final Period periodA = CLOSED_WEEK_1_AGO.toPeriod();
        final Period periodB = OPEN_NOW.toPeriod();

        // when
        final boolean actual1 = periodA.isAuctionFinished(LocalDateTime.now());
        final boolean actual2 = periodB.isAuctionFinished(LocalDateTime.now());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
