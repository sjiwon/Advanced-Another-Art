package com.sjiwon.anotherart.member.controller.dto.request;

import javax.validation.constraints.NotBlank;

public record ChangeNicknameRequest(
        @NotBlank(message = "변경할 닉네임은 필수입니다.")
        String changeNickname
) {
}
