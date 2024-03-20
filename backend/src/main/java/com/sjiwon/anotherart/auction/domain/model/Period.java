package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.auction.exception.AuctionException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.AUCTION_END_DATE_MUST_BE_AFTER_START_DATE;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.PERIOD_MUST_EXISTS;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
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
            throw new AuctionException(PERIOD_MUST_EXISTS);
        }
    }

    private static void validateStartIsBeforeEnd(final LocalDateTime startDate, final LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new AuctionException(AUCTION_END_DATE_MUST_BE_AFTER_START_DATE);
        }
    }

    public boolean isDateWithInRange(final LocalDateTime time) {
        return time.isAfter(startDate) && time.isBefore(endDate);
    }
}
