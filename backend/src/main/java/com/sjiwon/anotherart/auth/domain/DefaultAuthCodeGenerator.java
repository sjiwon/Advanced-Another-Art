package com.sjiwon.anotherart.auth.domain;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DefaultAuthCodeGenerator implements AuthCodeGenerator {
    @Override
    public String get() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
