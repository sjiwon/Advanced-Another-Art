package com.sjiwon.anotherart.member.presentation.request;

import com.sjiwon.anotherart.member.application.usecase.command.UpdatePasswordCommand;
import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(
        @NotBlank(message = "변경할 비밀번호는 필수입니다.")
        String value
) {
    public UpdatePasswordCommand toCommand(final long memberId) {
        return new UpdatePasswordCommand(
                memberId,
                value
        );
    }
}
