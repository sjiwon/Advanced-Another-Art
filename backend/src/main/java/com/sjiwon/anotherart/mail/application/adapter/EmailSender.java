package com.sjiwon.anotherart.mail.application.adapter;

public interface EmailSender {
    void sendAuthCodeForLoginId(final String targetEmail, final String authCode);

    void sendAuthCodeForPassword(final String targetEmail, final String authCode);
}
