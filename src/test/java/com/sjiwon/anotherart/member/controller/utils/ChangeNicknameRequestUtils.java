package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.ChangeNicknameRequest;

public class ChangeNicknameRequestUtils {
    public static ChangeNicknameRequest createRequest(String changeNickname) {
        return ChangeNicknameRequest.builder()
                .changeNickname(changeNickname)
                .build();
    }
}
