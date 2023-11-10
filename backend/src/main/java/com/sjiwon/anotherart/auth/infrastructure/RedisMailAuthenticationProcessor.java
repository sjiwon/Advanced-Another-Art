package com.sjiwon.anotherart.auth.infrastructure;

import com.sjiwon.anotherart.auth.application.adapter.MailAuthenticationProcessor;
import com.sjiwon.anotherart.auth.domain.AuthCodeGenerator;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisMailAuthenticationProcessor implements MailAuthenticationProcessor {
    private final AuthCodeGenerator authCodeGenerator;
    private final StringRedisTemplate stringRedisTemplate;
    private final long authTtl;

    public RedisMailAuthenticationProcessor(
            final AuthCodeGenerator authCodeGenerator,
            final StringRedisTemplate stringRedisTemplate,
            @Value("${mail.auth.ttl}") final long authTtl
    ) {
        this.authCodeGenerator = authCodeGenerator;
        this.stringRedisTemplate = stringRedisTemplate;
        this.authTtl = authTtl;
    }

    @Override
    public String storeAuthCode(final String key) {
        final String authCode = authCodeGenerator.get();
        stringRedisTemplate.opsForValue().set(key, authCode, authTtl, TimeUnit.MILLISECONDS);
        return authCode;
    }

    @Override
    public void verifyAuthCode(final String key, final String value) {
        final String realValue = stringRedisTemplate.opsForValue().get(key);

        if (!value.equals(realValue)) {
            throw AnotherArtException.type(AuthErrorCode.INVALID_AUTH_CODE);
        }
    }
}
