package com.sjiwon.anotherart.global.utils.encrypt;

public interface Encryptor {
    String hash(final String value);

    boolean matches(final String rawValue, final String encodedValue);

    String encrypt(final String value);

    String decrypt(final String value);
}
