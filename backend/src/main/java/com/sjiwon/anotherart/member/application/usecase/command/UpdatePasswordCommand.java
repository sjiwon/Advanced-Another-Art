package com.sjiwon.anotherart.member.application.usecase.command;

public record UpdatePasswordCommand(
        Long memberId,
        String password
) {
}
