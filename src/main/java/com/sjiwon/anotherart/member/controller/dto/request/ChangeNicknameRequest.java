package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChangeNicknameRequest {
    @NotBlank(message = MemberRequestValidationMessage.ChangeNickname.CHANGE_NAME)
    @ApiModelProperty(value = "변경할 닉네임", example = "빈센트 반 고흐2", required = true)
    private String changeNickname;
}
