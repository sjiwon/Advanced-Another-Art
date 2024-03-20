package com.sjiwon.anotherart.global.config.web;

import com.sjiwon.anotherart.global.annotation.AuthArgumentResolver;
import com.sjiwon.anotherart.global.annotation.ExtractTokenArgumentResolver;
import com.sjiwon.anotherart.token.domain.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AdditionalWebConfig implements WebMvcConfigurer {
    private final TokenProvider tokenProvider;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArgumentResolver());
        resolvers.add(new ExtractTokenArgumentResolver(tokenProvider));
    }
}
