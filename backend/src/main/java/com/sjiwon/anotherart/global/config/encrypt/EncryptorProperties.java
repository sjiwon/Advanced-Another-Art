package com.sjiwon.anotherart.global.config.encrypt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "encrypt")
public record EncryptorProperties(
        String secretKey,
        String salt
) {
}
