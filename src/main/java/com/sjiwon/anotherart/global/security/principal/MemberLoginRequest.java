package com.sjiwon.anotherart.global.security.principal;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberLoginRequest {
    private String loginId;
    private String loginPassword;

    @Builder
    public MemberLoginRequest(String loginId, String loginPassword) {
        this.loginId = loginId;
        this.loginPassword = loginPassword;
    }
}
