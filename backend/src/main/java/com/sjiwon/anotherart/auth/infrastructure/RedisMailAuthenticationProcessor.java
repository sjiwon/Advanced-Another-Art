package com.sjiwon.anotherart.auth.infrastructure;

import com.sjiwon.anotherart.auth.application.adapter.MailAuthenticationProcessor;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisMailAuthenticationProcessor implements MailAuthenticationProcessor {
    private final StringRedisTemplate stringRedisTemplate;
    private final long authTtl;

    public RedisMailAuthenticationProcessor(
            final StringRedisTemplate stringRedisTemplate,
            @Value("${mail.auth.ttl}") final long authTtl
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.authTtl = authTtl;
    }

    @Override
    public void storeAuthCode(final String key, final String value) {
        stringRedisTemplate.opsForValue().set(key, value, authTtl, TimeUnit.MILLISECONDS);
    }

    @Override
    public void verifyAuthCode(final String key, final String value) {
        final String realValue = stringRedisTemplate.opsForValue().get(key);

        if (!value.equals(realValue)) {
            throw AnotherArtException.type(AuthErrorCode.INVALID_AUTH_CODE);
        }
    }
}
