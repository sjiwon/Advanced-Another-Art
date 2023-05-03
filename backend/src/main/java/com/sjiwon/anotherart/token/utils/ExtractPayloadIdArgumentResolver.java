package com.sjiwon.anotherart.token.utils;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.token.exception.TokenErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
public class ExtractPayloadIdArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ExtractPayloadId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String token = extractToken(request);
        return jwtTokenProvider.getId(token);
    }

    private String extractToken(HttpServletRequest request) {
        return AuthorizationExtractor.extractToken(request)
                .orElseThrow(() -> AnotherArtException.type(TokenErrorCode.AUTH_INVALID_TOKEN));
    }
}
