package com.sjiwon.anotherart.member.application.usecase.command;

public record UpdateNicknameCommand(
        Long memberId,
        String nickname
) {
}
