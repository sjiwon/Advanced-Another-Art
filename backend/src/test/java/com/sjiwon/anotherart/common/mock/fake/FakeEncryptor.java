package com.sjiwon.anotherart.common.mock.fake;

import com.sjiwon.anotherart.global.utils.encrypt.Encryptor;

public class FakeEncryptor implements Encryptor {
    private static final String UNI_DIRECTIONAL_DUMMY = "_hello_art_uni";
    private static final String BI_DIRECTIONAL_DUMMY = "_hello_art_bi";

    @Override
    public String hash(final String rawValue) {
        return rawValue + UNI_DIRECTIONAL_DUMMY;
    }

    @Override
    public boolean matches(final String rawValue, final String encodedValue) {
        final String decodedValue = encodedValue.replaceAll(UNI_DIRECTIONAL_DUMMY, "");
        return decodedValue.equals(rawValue);
    }

    @Override
    public String encrypt(final String value) {
        return value + BI_DIRECTIONAL_DUMMY;
    }

    @Override
    public String decrypt(final String value) {
        return value.replaceAll(BI_DIRECTIONAL_DUMMY, "");
    }
}
