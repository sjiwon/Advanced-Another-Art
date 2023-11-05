package com.sjiwon.anotherart.common.fixture;

import com.sjiwon.anotherart.auction.domain.Period;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum PeriodFixture {
    CLOSED_WEEK_2_AGO(LocalDateTime.now().minusDays(14), LocalDateTime.now().minusDays(8)),
    CLOSED_WEEK_1_AGO(LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(3)),
    OPEN_NOW(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(5)),
    OPEN_WEEK_1_LATER(LocalDateTime.now().plusHours(7), LocalDateTime.now().plusHours(13)),
    OPEN_WEEK_2_LATER(LocalDateTime.now().plusHours(14), LocalDateTime.now().plusHours(20)),
    ;

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public Period toPeriod() {
        return Period.of(startDate, endDate);
    }
}
