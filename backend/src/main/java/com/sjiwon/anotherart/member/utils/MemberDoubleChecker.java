package com.sjiwon.anotherart.member.utils;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MemberDoubleChecker {
    public boolean isTrustworthyMember(Long memberId, Long payloadId) {
        return Objects.equals(memberId, payloadId);
    }
}
