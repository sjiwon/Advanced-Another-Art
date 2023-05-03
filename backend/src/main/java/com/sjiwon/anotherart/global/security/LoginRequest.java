package com.sjiwon.anotherart.global.security;

public record LoginRequest(
        String loginId,
        String loginPassword
) {
}
