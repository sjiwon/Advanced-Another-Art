package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Period {
    @Column(name = "start_date", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false, updatable = false)
    private LocalDateTime endDate;

    private Period(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Period of(LocalDateTime startDate, LocalDateTime endDate) {
        validateDuration(startDate, endDate);
        return new Period(startDate, endDate);
    }

    private static void validateDuration(LocalDateTime startDate, LocalDateTime endDate) {
        if (isEndBeforeStart(startDate, endDate)) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_AUCTION_DURATION);
        }
    }

    private static boolean isEndBeforeStart(LocalDateTime startDate, LocalDateTime endDate) {
        return endDate.isBefore(startDate);
    }

    public boolean isAuctionFinished(LocalDateTime bidTime) {
        return bidTime.isAfter(this.endDate);
    }
}
