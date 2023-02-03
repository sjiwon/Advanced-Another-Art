package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DuplicateCheckRequest {
    @NotBlank(message = MemberRequestValidationMessage.DuplicateCheck.RESOURCE)
    @ApiModelProperty(value = "중복 체크 타입", example = "nickname", required = true)
    private String resource;

    @NotBlank(message = MemberRequestValidationMessage.DuplicateCheck.VALUE)
    @ApiModelProperty(value = "중복 체크 값", example = "user", required = true)
    private String value;
}
