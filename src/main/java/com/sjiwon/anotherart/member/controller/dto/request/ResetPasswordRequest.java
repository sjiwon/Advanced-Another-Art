package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = MemberRequestValidationMessage.ResetPassword.LOGIN_ID)
    @ApiModelProperty(value = "사용자 로그인 아이디", example = "user1", required = true)
    private String loginId;

    @NotBlank(message = MemberRequestValidationMessage.ResetPassword.CHANGE_PASSWORD)
    @ApiModelProperty(value = "사용자 로그인 아이디", example = "user1", required = true)
    private String changePassword;
}
