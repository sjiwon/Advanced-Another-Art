package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthForResetPasswordRequest {
    @NotBlank(message = MemberRequestValidationMessage.AuthForResetPassword.NAME)
    private String name;

    @NotBlank(message = MemberRequestValidationMessage.AuthForResetPassword.LOGIN_ID)
    private String loginId;

    @NotBlank(message = MemberRequestValidationMessage.AuthForResetPassword.EMAIL)
    private String email;
}
