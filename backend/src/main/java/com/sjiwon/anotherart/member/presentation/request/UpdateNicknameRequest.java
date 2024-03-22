package com.sjiwon.anotherart.member.presentation.request;

import com.sjiwon.anotherart.member.application.usecase.command.UpdateNicknameCommand;
import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameRequest(
        @NotBlank(message = "변경할 닉네임은 필수입니다.")
        String value
) {
    public UpdateNicknameCommand toCommand(final long memberId) {
        return new UpdateNicknameCommand(
                memberId,
                value
        );
    }
}
