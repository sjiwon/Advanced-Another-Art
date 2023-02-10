package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DuplicateCheckRequest {
    @NotBlank(message = MemberRequestValidationMessage.DuplicateCheck.RESOURCE)
    private String resource;

    @NotBlank(message = MemberRequestValidationMessage.DuplicateCheck.VALUE)
    private String value;
}
