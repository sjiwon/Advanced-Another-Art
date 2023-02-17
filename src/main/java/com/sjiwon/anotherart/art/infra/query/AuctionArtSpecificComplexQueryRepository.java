package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.utils.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuctionArtSpecificComplexQueryRepository {
    Page<BasicAuctionArt> findCurrentActiveAuctionArtList(SearchCondition condition, Pageable pageRequest);
    Page<BasicAuctionArt> findAuctionArtListByKeyword(SearchCondition condition, Pageable pageRequest);
}
