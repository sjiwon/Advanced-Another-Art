package com.sjiwon.anotherart.member.application.usecase.command;

public record ConfirmAuthCodeForLoginIdCommand(
        String name,
        String email,
        String authCode
) {
}
