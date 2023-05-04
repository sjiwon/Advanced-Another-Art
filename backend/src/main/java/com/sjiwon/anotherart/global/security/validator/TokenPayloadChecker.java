package com.sjiwon.anotherart.global.security.validator;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TokenPayloadChecker {
    public boolean isTrustworthyMember(Long payloadId, Long memberId) {
        return Objects.equals(payloadId, memberId);
    }
}
