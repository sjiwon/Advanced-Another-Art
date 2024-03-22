package com.sjiwon.anotherart.like.application.usecase.command;

public record MarkArtLikeCommand(
        long memberId,
        long artId
) {
}
