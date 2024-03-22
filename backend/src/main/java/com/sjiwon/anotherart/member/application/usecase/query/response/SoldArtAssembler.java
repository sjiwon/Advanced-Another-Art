package com.sjiwon.anotherart.member.application.usecase.query.response;

import com.sjiwon.anotherart.member.domain.repository.query.response.SoldArt;

import java.util.List;

public record SoldArtAssembler(
        List<SoldArtResponse> generalArts,
        List<SoldArtResponse> auctionArts
) {
    public record SoldArtResponse(
            Long artId,
            String artName,
            String artDescription,
            String artStorageUrl,
            List<String> artHashtags,
            String ownerNickname,
            String ownerSchool,
            int purchasePrice
    ) {
        public static SoldArtResponse from(final SoldArt result) {
            return new SoldArtResponse(
                    result.getArtId(),
                    result.getArtName(),
                    result.getArtDescription(),
                    result.getArtStorageUrl(),
                    result.getArtHashtags(),
                    result.getBuyerNickname(),
                    result.getBuyerSchool(),
                    result.getSoldPrice()
            );
        }
    }

    public static SoldArtAssembler of(
            final List<SoldArt> generalArts,
            final List<SoldArt> auctionArts
    ) {
        return new SoldArtAssembler(
                generalArts.stream()
                        .map(SoldArtResponse::from)
                        .toList(),
                auctionArts.stream()
                        .map(SoldArtResponse::from)
                        .toList()
        );
    }
}
