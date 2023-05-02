package com.sjiwon.anotherart.member.controller.utils;

import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;

public class AuthForResetPasswordRequestUtils {
    public static AuthForResetPasswordRequest createRequest(String name, String loginId, String email) {
        return new AuthForResetPasswordRequest(name, loginId, email);
    }
}
