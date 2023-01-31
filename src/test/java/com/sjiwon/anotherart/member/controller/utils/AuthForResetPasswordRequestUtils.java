package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;

public class AuthForResetPasswordRequestUtils {
    public static AuthForResetPasswordRequest createRequest(String loginId, String name, String email) {
        return AuthForResetPasswordRequest.builder()
                .loginId(loginId)
                .name(name)
                .email(email)
                .build();
    }
}
