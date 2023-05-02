package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;

import java.util.List;

public interface WinningAuctionArtQueryRepository {
    List<BasicAuctionArt> findWinningAuctionArtListByMemberId(Long memberId);
}
