package com.sjiwon.anotherart.fixture;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.sjiwon.anotherart.fixture.PeriodFixture.*;

@Getter
@RequiredArgsConstructor
public enum AuctionFixture {
    AUCTION_A_1(CLOSED_WEEK_2_AGO),
    AUCTION_A_2(CLOSED_WEEK_1_AGO),
    AUCTION_A_3(OPEN_NOW),
    AUCTION_A_4(OPEN_WEEK_1_LATER),
    AUCTION_A_5(OPEN_WEEK_2_LATER),

    AUCTION_B_1(CLOSED_WEEK_2_AGO),
    AUCTION_B_2(CLOSED_WEEK_1_AGO),
    AUCTION_B_3(OPEN_NOW),
    AUCTION_B_4(OPEN_WEEK_1_LATER),
    AUCTION_B_5(OPEN_WEEK_2_LATER),
    ;

    private final PeriodFixture period;

    public Auction toAuction(Art art) {
        return Auction.createAuction(art, period.toPeriod());
    }
}
