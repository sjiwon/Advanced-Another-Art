package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.ChangeNicknameRequest;

public class ChangeNicknameRequestUtils {
    public static ChangeNicknameRequest createRequest(String changeNickname) {
        return new ChangeNicknameRequest(changeNickname);
    }
}
