package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.ChangePasswordRequest;

public class ChangePasswordRequestUtils {
    public static ChangePasswordRequest createRequest(String changePassword) {
        return ChangePasswordRequest.builder()
                .changePassword(changePassword)
                .build();
    }
}
