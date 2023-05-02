package com.sjiwon.anotherart.purchase.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GeneralArtDealEvent {
    private final Long ownerId;
    private final Long buyerId;
    private final int dealAmount;
    private final boolean isAuctionDeal = false;
}
