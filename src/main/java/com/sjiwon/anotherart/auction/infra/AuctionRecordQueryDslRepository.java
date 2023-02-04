package com.sjiwon.anotherart.auction.infra;

public interface AuctionRecordQueryDslRepository {
    boolean existsAuctionRecordByArtId(Long artId);
}
