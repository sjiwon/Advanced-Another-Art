package com.sjiwon.anotherart.global.config.encrypt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class EncryptorConfig {
    private final EncryptorProperties encryptorProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BytesEncryptor bytesEncryptor() {
        return new AesBytesEncryptor(
                encryptorProperties.secretKey(),
                encryptorProperties.salt()
        );
    }
}
