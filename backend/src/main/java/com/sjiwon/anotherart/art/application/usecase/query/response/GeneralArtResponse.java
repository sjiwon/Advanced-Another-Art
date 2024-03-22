package com.sjiwon.anotherart.art.application.usecase.query.response;

import com.sjiwon.anotherart.art.domain.repository.query.response.GeneralArt;

public record GeneralArtResponse(
        ArtSummary art,
        UserSummary owner,
        UserSummary buyer
) {
    public static GeneralArtResponse from(final GeneralArt result) {
        return new GeneralArtResponse(
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
                        result.getBuyerId(),
                        result.getBuyerNickname(),
                        result.getBuyerSchool()
                )
        );
    }
}
