package com.sjiwon.anotherart.art.presentation.request;

import com.sjiwon.anotherart.art.application.usecase.command.UpdateArtCommand;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.Description;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UpdateArtRequest(
        @NotBlank(message = "작품명은 필수입니다.")
        String name,

        @NotBlank(message = "작품 설명은 필수입니다.")
        String description,

        Set<String> hashtags
) {
    public UpdateArtCommand toCommand(
            final long memberId,
            final long artId
    ) {
        return new UpdateArtCommand(
                memberId,
                artId,
                ArtName.from(name),
                Description.from(description),
                hashtags
        );
    }
}
