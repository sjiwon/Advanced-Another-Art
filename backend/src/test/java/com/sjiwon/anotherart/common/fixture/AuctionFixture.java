package com.sjiwon.anotherart.common.fixture;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionFixture {
    AUCTION_CLOSED_WEEK_2_AGO(PeriodFixture.CLOSED_WEEK_2_AGO),
    AUCTION_CLOSED_WEEK_1_AGO(PeriodFixture.CLOSED_WEEK_1_AGO),
    AUCTION_OPEN_NOW(PeriodFixture.OPEN_NOW),
    AUCTION_OPEN_WEEK_1_LATER(PeriodFixture.OPEN_WEEK_1_LATER),
    AUCTION_OPEN_WEEK_2_LATER(PeriodFixture.OPEN_WEEK_2_LATER),
    ;

    private final PeriodFixture period;

    public Auction toAuction(final Art art) {
        return Auction.createAuction(art, period.toPeriod());
    }
}
