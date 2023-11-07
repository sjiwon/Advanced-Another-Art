package com.sjiwon.anotherart.favorite.application.usecase.command;

public record CancelArtLikeCommand(
        Long memberId,
        Long artId
) {
}
