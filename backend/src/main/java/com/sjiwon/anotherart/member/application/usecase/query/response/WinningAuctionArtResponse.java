package com.sjiwon.anotherart.member.application.usecase.query.response;

import com.sjiwon.anotherart.member.domain.repository.query.response.WinningAuctionArt;

import java.util.List;

public record WinningAuctionArtResponse(
        Long artId,
        String artName,
        String artDescription,
        String artStorageUrl,
        List<String> artHashtags,
        String ownerNickname,
        String ownerSchool,
        int highestBidPrice
) {
    public static WinningAuctionArtResponse from(final WinningAuctionArt result) {
        return new WinningAuctionArtResponse(
                result.getArtId(),
                result.getArtName(),
                result.getArtDescription(),
                result.getArtStorageUrl(),
                result.getArtHashtags(),
                result.getOwnerNickname(),
                result.getOwnerSchool(),
                result.getHighestBidPrice()
        );
    }
}
