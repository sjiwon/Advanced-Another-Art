package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FindIdRequest {
    @NotBlank(message = MemberRequestValidationMessage.FindId.NAME)
    @ApiModelProperty(value = "사용자 이름", example = "빈센트 반 고흐", required = true)
    private String name;

    @NotBlank(message = MemberRequestValidationMessage.FindId.EMAIL)
    @ApiModelProperty(value = "사용자 이메일", example = "anotherart1@gmail.com", required = true)
    private String email;
}
