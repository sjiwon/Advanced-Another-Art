package com.sjiwon.anotherart.art.application.usecase.command;

public record DeleteArtCommand(
        long memberId,
        long artId
) {
}
