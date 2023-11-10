package com.sjiwon.anotherart.member.application.usecase.command;

public record AuthForResetPasswordCommand(
        String name,
        String email,
        String loginId
) {
}
