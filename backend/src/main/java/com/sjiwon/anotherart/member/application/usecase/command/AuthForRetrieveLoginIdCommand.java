package com.sjiwon.anotherart.member.application.usecase.command;

public record AuthForRetrieveLoginIdCommand(
        String name,
        String email
) {
}
