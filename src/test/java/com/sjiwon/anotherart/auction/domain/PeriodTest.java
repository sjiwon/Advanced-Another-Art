package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.utils.ArtUtils.currentTime1DayLater;
import static com.sjiwon.anotherart.common.utils.ArtUtils.currentTime3DayLater;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Auction 도메인 {Period VO} 테스트")
class PeriodTest {
    @Test
    @DisplayName("경매 종료날짜가 시작날짜보다 먼저이면 예외가 발생한다")
    void test(){
        assertThatThrownBy(() -> Period.of(currentTime3DayLater, currentTime1DayLater))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(AuctionErrorCode.INVALID_AUCTION_DURATION.getMessage());
    }
}
