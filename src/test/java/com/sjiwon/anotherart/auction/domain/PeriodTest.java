package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PeriodTest {
    @Test
    @DisplayName("경매 종료날짜가 시작날짜보다 먼저이면 예외가 발생한다")
    void test(){
        // given
        final LocalDateTime startDate = LocalDateTime.now();
        final LocalDateTime endDate = LocalDateTime.now().minusDays(1);

        // when - then
        assertThatThrownBy(() -> Period.of(startDate, endDate))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.INVALID_AUCTION_DURATION.getMessage());
    }
}