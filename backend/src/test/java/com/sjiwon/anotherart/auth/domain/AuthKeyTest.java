package com.sjiwon.anotherart.auth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Auth -> AuthKey 테스트")
public class AuthKeyTest {
    @Test
    @DisplayName("인증을 위한 AuthKey를 완성한다 (with suffix)")
    void generateAuthKey() {
        final String suffix = "sjiwon4491@gmail.com";

        assertAll(
                () -> assertThat(AuthKey.LOGIN_AUTH_KEY.generateAuthKey(suffix)).isEqualTo("LOGIN:sjiwon4491@gmail.com"),
                () -> assertThat(AuthKey.PASSWORD_AUTH_KEY.generateAuthKey(suffix)).isEqualTo("PASSWORD:sjiwon4491@gmail.com")
        );
    }
}
