package com.sjiwon.anotherart.auction.infra.query;

public interface AuctionRecordQueryRepository {
    boolean existsAuctionRecordByArtId(Long artId);
}
