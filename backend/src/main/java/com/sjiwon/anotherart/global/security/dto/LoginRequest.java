package com.sjiwon.anotherart.global.security.dto;

public record LoginRequest(
        String loginId,
        String loginPassword
) {
}
