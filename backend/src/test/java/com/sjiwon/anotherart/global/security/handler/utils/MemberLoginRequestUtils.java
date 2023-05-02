package com.sjiwon.anotherart.global.security.handler.utils;

import com.sjiwon.anotherart.global.security.principal.MemberLoginRequest;

public class MemberLoginRequestUtils {
    public static MemberLoginRequest createRequest(String loginId, String loginPassword) {
        return MemberLoginRequest.builder()
                .loginId(loginId)
                .loginPassword(loginPassword)
                .build();
    }
}
