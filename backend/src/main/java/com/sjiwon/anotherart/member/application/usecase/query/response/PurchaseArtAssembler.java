package com.sjiwon.anotherart.member.application.usecase.query.response;

import com.sjiwon.anotherart.member.domain.repository.query.response.PurchaseArt;

import java.util.List;

public record PurchaseArtAssembler(
        List<PurchaseArtResponse> generalArts,
        List<PurchaseArtResponse> auctionArts
) {
    public record PurchaseArtResponse(
            Long artId,
            String artName,
            String artDescription,
            String artStorageUrl,
            List<String> artHashtags,
            String ownerNickname,
            String ownerSchool,
            int purchasePrice
    ) {
        public static PurchaseArtResponse from(final PurchaseArt result) {
            return new PurchaseArtResponse(
                    result.getArtId(),
                    result.getArtName(),
                    result.getArtDescription(),
                    result.getArtStorageUrl(),
                    result.getArtHashtags(),
                    result.getOwnerNickname(),
                    result.getOwnerSchool(),
                    result.getPurchasePrice()
            );
        }
    }

    public static PurchaseArtAssembler of(
            final List<PurchaseArt> generalArts,
            final List<PurchaseArt> auctionArts
    ) {
        return new PurchaseArtAssembler(
                generalArts.stream()
                        .map(PurchaseArtResponse::from)
                        .toList(),
                auctionArts.stream()
                        .map(PurchaseArtResponse::from)
                        .toList()
        );
    }
}
