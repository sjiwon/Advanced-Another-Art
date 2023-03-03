package com.sjiwon.anotherart.fixture;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class AuctionFixture {
    public static Auction toAuction(Art art, LocalDateTime startDate, LocalDateTime endDate) {
        return Auction.initAuction(art, Period.of(startDate, endDate));
    }
}
