package com.sjiwon.anotherart.global.encrypt;

public interface PasswordEncryptor {
    String encode(final String rawValue);

    boolean matches(final String rawValue, final String encodedValue);
}
