package com.sjiwon.anotherart.global.config;

import com.sjiwon.anotherart.global.resolver.ExtractPayloadArgumentResolver;
import com.sjiwon.anotherart.global.resolver.ExtractTokenArgumentResolver;
import com.sjiwon.anotherart.token.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AdditionalWebConfiguration implements WebMvcConfigurer {
    private final TokenProvider tokenProvider;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ExtractTokenArgumentResolver());
        resolvers.add(new ExtractPayloadArgumentResolver(tokenProvider));
    }
}
