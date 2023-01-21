package com.sjiwon.anotherart.common;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderUtils {
    public static PasswordEncoder getEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
