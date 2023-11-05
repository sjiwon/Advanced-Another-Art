package com.sjiwon.anotherart.global.security;

public record LoginRequest(
        String id,
        String password
) {
}
