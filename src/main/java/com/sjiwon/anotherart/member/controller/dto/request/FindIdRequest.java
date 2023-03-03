package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FindIdRequest {
    @NotBlank(message = MemberRequestValidationMessage.FindId.NAME)
    private String name;

    @NotBlank(message = MemberRequestValidationMessage.FindId.EMAIL)
    private String email;
}
