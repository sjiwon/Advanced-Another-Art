package com.sjiwon.anotherart.member.controller.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChangeNicknameRequest {
    @NotBlank(message = "변경할 닉네임은 필수입니다.")
    private String changeNickname;
}
