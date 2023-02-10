package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChangeNicknameRequest {
    @NotBlank(message = MemberRequestValidationMessage.ChangeNickname.CHANGE_NAME)
    private String changeNickname;
}
