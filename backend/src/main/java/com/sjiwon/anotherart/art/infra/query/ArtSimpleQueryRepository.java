package com.sjiwon.anotherart.art.infra.query;

public interface ArtSimpleQueryRepository {
    boolean isAuctionRecordExists(Long artId);
}
