package com.sjiwon.anotherart.global.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nickname;
    private String accessToken;
    private String refreshToken;

    @Builder
    public TokenResponse(String nickname, String accessToken, String refreshToken) {
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
