package com.sjiwon.anotherart.global.utils.encrypt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class DefaultEncryptor implements Encryptor {
    private final PasswordEncoder passwordEncoder;
    private final BytesEncryptor bytesEncryptor;

    @Override
    public String hash(final String rawValue) {
        return passwordEncoder.encode(rawValue);
    }

    @Override
    public boolean matches(final String rawValue, final String encodedValue) {
        return passwordEncoder.matches(rawValue, encodedValue);
    }

    @Override
    public String encrypt(final String value) {
        final byte[] encryptedBytes = bytesEncryptor.encrypt(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @Override
    public String decrypt(final String value) {
        final byte[] decryptedBytes = bytesEncryptor.decrypt(Base64.getDecoder().decode(value));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
