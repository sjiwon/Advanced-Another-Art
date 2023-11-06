package com.sjiwon.anotherart.global.encrypt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultPasswordEncryptor implements PasswordEncryptor {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(final String rawValue) {
        return passwordEncoder.encode(rawValue);
    }

    @Override
    public boolean matches(final String rawValue, final String encodedValue) {
        return passwordEncoder.matches(rawValue, encodedValue);
    }
}
