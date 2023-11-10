package com.sjiwon.anotherart.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthKey {
    LOGIN_AUTH_KEY("LOGIN:%s"),
    PASSWORD_AUTH_KEY("PASSWORD:%s"),
    ;

    private final String value;

    public String generateAuthKey(final String suffix) {
        return String.format(this.value, suffix);
    }
}
