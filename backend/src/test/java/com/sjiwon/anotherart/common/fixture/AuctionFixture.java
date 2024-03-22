package com.sjiwon.anotherart.common.fixture;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuctionFixture {
    경매_2주전_종료(PeriodFixture.CLOSED_WEEK_2_AGO),
    경매_1주전_종료(PeriodFixture.CLOSED_WEEK_1_AGO),
    경매_현재_진행(PeriodFixture.OPEN_NOW),
    경매_1주뒤_오픈(PeriodFixture.OPEN_WEEK_1_LATER),
    경매_2주뒤_오픈(PeriodFixture.OPEN_WEEK_2_LATER),
    ;

    private final PeriodFixture period;

    public Auction toDomain(final Art art) {
        return Auction.createAuction(art, period.toDomain());
    }
}
