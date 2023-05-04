package com.sjiwon.anotherart.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderUtils {
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public static PasswordEncoder getEncoder() {
        return ENCODER;
    }
}
