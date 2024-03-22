package com.sjiwon.anotherart.member.presentation.request;

import com.sjiwon.anotherart.member.application.usecase.command.AuthForRetrieveLoginIdCommand;
import jakarta.validation.constraints.NotBlank;

public record ProvideAuthCodeForLoginIdRequest(
        @NotBlank(message = "사용자 이름은 필수입니다.")
        String name,

        @NotBlank(message = "사용자 이메일은 필수입니다.")
        String email
) {
    public AuthForRetrieveLoginIdCommand toCommand() {
        return new AuthForRetrieveLoginIdCommand(name, email);
    }
}
