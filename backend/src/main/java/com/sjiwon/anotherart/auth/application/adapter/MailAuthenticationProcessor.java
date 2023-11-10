package com.sjiwon.anotherart.auth.application.adapter;

public interface MailAuthenticationProcessor {
    void storeAuthCode(final String key, final String value);

    void verifyAuthCode(final String key, final String value);
}
