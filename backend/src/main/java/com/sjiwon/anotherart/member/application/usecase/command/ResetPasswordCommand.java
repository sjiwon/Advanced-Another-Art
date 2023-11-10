package com.sjiwon.anotherart.member.application.usecase.command;

public record ResetPasswordCommand(
        String name,
        String email,
        String loginId,
        String password
) {
}
