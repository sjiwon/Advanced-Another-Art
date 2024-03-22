package com.sjiwon.anotherart.like.application.usecase.command;

public record CancelArtLikeCommand(
        long memberId,
        long artId
) {
}
