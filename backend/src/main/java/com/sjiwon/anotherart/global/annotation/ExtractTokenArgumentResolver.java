package com.sjiwon.anotherart.global.annotation;

import com.sjiwon.anotherart.global.security.exception.AuthException;
import com.sjiwon.anotherart.token.domain.model.TokenType;
import com.sjiwon.anotherart.token.domain.service.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.sjiwon.anotherart.global.security.exception.AuthErrorCode.INVALID_PERMISSION;
import static com.sjiwon.anotherart.token.utils.TokenExtractor.extractAccessToken;
import static com.sjiwon.anotherart.token.utils.TokenExtractor.extractRefreshToken;

@RequiredArgsConstructor
public class ExtractTokenArgumentResolver implements HandlerMethodArgumentResolver {
    private final TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        final boolean hasRequiredAnnotation = parameter.getParameterAnnotation(ExtractToken.class) != null;
        final boolean hasRequiredType = parameter.getParameterType().isAssignableFrom(String.class);
        return hasRequiredAnnotation && hasRequiredType;
    }

    @Override
    public String resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final ExtractToken extractToken = parameter.getParameterAnnotation(ExtractToken.class);
        return getToken(request, extractToken.tokenType());
    }

    private String getToken(final HttpServletRequest request, final TokenType type) {
        if (type == TokenType.ACCESS) {
            final String accessToken = extractAccessToken(request)
                    .orElseThrow(() -> new AuthException(INVALID_PERMISSION));
            tokenProvider.validateAccessToken(accessToken);
            return accessToken;
        }
        final String refreshToken = extractRefreshToken(request)
                .orElseThrow(() -> new AuthException(INVALID_PERMISSION));
        tokenProvider.validateRefreshToken(refreshToken);
        return refreshToken;
    }
}
