package com.sjiwon.anotherart.art.application.usecase.query.response;

import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;

public record AuctionArtResponse(
        AuctionSummary auction,
        ArtSummary art,
        UserSummary owner,
        UserSummary highestBidder
) {
    public static AuctionArtResponse from(final AuctionArt result) {
        return new AuctionArtResponse(
                new AuctionSummary(
                        result.getAuctionId(),
                        result.getHighestBidPrice(),
                        result.getAuctionStartDate(),
                        result.getAuctionEndDate(),
                        result.getBidCount()
                ),
                new ArtSummary(
                        result.getArtId(),
                        result.getArtName(),
                        result.getArtDescription(),
                        result.getArtPrice(),
                        result.getArtStatus(),
                        result.getArtStorageUrl(),
                        result.getArtRegistrationDate(),
                        result.getHashtags(),
                        result.getLikeMembers()
                ),
                new UserSummary(
                        result.getOwnerId(),
                        result.getOwnerNickname(),
                        result.getOwnerSchool()
                ),
                new UserSummary(
                        result.getHighestBidderId(),
                        result.getHighestBidderNickname(),
                        result.getHighestBidderSchool()
                )
        );
    }
}
