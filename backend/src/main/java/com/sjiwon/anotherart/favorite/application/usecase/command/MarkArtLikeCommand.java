package com.sjiwon.anotherart.favorite.application.usecase.command;

public record MarkArtLikeCommand(
        Long memberId,
        Long artId
) {
}
