package com.sjiwon.anotherart.auth.application.adapter;

public interface MailAuthenticationProcessor {
    String storeAuthCode(final String key);

    void verifyAuthCode(final String key, final String value);
}
