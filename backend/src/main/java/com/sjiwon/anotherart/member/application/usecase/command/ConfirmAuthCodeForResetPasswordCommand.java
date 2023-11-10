package com.sjiwon.anotherart.member.application.usecase.command;

public record ConfirmAuthCodeForResetPasswordCommand(
        String name,
        String email,
        String loginId,
        String authCode
) {
}
