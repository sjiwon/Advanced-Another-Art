package com.sjiwon.anotherart.token.application.usecase.command;

public record ReissueTokenCommand(
        Long memberId,
        String refreshToken
) {
}
