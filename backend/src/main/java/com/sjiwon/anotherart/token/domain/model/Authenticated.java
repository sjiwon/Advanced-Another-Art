package com.sjiwon.anotherart.token.domain.model;

import java.util.List;

public record Authenticated(
        Long id,
        String name,
        String nickname,
        List<String> roles
) {
}
