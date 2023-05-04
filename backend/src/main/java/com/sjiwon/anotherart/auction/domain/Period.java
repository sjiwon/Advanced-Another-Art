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
        validateStartIsBeforeEnd(startDate, endDate);
        return new Period(startDate, endDate);
    }

    private static void validateStartIsBeforeEnd(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw AnotherArtException.type(AuctionErrorCode.AUCTION_END_DATE_MUST_BE_AFTER_START_DATE);
        }
    }

    public boolean isDateWithInRange(LocalDateTime time) {
        return time.isAfter(startDate) && time.isBefore(endDate);
    }

    public boolean isAuctionFinished(LocalDateTime time) {
        return endDate.isBefore(time);
    }
}
