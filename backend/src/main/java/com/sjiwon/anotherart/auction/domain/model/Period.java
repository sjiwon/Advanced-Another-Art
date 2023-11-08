package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Period {
    @Column(name = "start_date", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false, updatable = false)
    private LocalDateTime endDate;

    private Period(final LocalDateTime startDate, final LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Period of(final LocalDateTime startDate, final LocalDateTime endDate) {
        validateDateExists(startDate, endDate);
        validateStartIsBeforeEnd(startDate, endDate);
        return new Period(startDate, endDate);
    }

    private static void validateDateExists(final LocalDateTime startDate, final LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw AnotherArtException.type(AuctionErrorCode.PERIOD_MUST_EXISTS);
        }
    }

    private static void validateStartIsBeforeEnd(final LocalDateTime startDate, final LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw AnotherArtException.type(AuctionErrorCode.AUCTION_END_DATE_MUST_BE_AFTER_START_DATE);
        }
    }

    public boolean isDateWithInRange(final LocalDateTime time) {
        return time.isAfter(startDate) && time.isBefore(endDate);
    }
}
