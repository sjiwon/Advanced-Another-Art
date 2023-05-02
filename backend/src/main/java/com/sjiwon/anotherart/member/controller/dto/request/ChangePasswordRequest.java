package com.sjiwon.anotherart.member.controller.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "변경할 비밀번호는 필수입니다.")
    private String changePassword;
}
