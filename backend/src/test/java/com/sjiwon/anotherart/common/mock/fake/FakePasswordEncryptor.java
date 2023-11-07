package com.sjiwon.anotherart.common.mock.fake;

import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;

public class FakePasswordEncryptor implements PasswordEncryptor {
    private static final String SUFFIX = "_entrypt";

    @Override
    public String encode(final String rawValue) {
        return rawValue + SUFFIX;
    }

    @Override
    public boolean matches(final String rawValue, final String encodedValue) {
        final String decodedValue = encodedValue.replaceAll(SUFFIX, "");
        return decodedValue.equals(rawValue);
    }
}
