package com.sjiwon.anotherart.global.security.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class CorsProperties {
    private final List<String> allowedOriginPatterns;

    public CorsProperties(@Value("${cors.allowed-origin-patterns}") final List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }
}
