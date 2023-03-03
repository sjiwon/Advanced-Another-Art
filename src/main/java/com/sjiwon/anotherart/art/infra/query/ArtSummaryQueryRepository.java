package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.AuctionRecordSummary;
import com.sjiwon.anotherart.art.infra.query.dto.FavoriteSummary;
import com.sjiwon.anotherart.art.infra.query.dto.HashtagSummary;

import java.util.List;

public interface ArtSummaryQueryRepository {
    List<HashtagSummary> findHashtagSummaryList();
    List<FavoriteSummary> findFavoriteSummaryList();
    List<AuctionRecordSummary> findAuctionRecordSummaryList();
}
