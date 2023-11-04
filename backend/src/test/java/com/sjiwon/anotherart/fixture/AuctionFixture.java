package com.sjiwon.anotherart.fixture;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.sjiwon.anotherart.fixture.PeriodFixture.CLOSED_WEEK_1_AGO;
import static com.sjiwon.anotherart.fixture.PeriodFixture.CLOSED_WEEK_2_AGO;
import static com.sjiwon.anotherart.fixture.PeriodFixture.OPEN_NOW;
import static com.sjiwon.anotherart.fixture.PeriodFixture.OPEN_WEEK_1_LATER;
import static com.sjiwon.anotherart.fixture.PeriodFixture.OPEN_WEEK_2_LATER;

@Getter
@RequiredArgsConstructor
public enum AuctionFixture {
    AUCTION_CLOSED_WEEK_2_AGO(CLOSED_WEEK_2_AGO),
    AUCTION_CLOSED_WEEK_1_AGO(CLOSED_WEEK_1_AGO),
    AUCTION_OPEN_NOW(OPEN_NOW),
    AUCTION_OPEN_WEEK_1_LATER(OPEN_WEEK_1_LATER),
    AUCTION_OPEN_WEEK_2_LATER(OPEN_WEEK_2_LATER),
    ;

    private final PeriodFixture period;

    public Auction toAuction(final Art art) {
        return Auction.createAuction(art, period.toPeriod());
    }
}
