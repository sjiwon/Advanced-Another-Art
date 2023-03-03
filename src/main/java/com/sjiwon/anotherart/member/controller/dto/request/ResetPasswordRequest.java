package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = MemberRequestValidationMessage.ResetPassword.LOGIN_ID)
    private String loginId;

    @NotBlank(message = MemberRequestValidationMessage.ResetPassword.CHANGE_PASSWORD)
    private String changePassword;
}
