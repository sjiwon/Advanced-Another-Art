package com.sjiwon.anotherart.token.presentation;

import com.sjiwon.anotherart.global.annotation.ExtractToken;
import com.sjiwon.anotherart.token.application.usecase.ReissueTokenUseCase;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.domain.model.TokenType;
import com.sjiwon.anotherart.token.utils.TokenResponseWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "토큰 재발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class ReissueTokenApi {
    private final ReissueTokenUseCase reissueTokenUseCase;
    private final TokenResponseWriter tokenResponseWriter;

    @Operation(summary = "RefreshToken을 통한 토큰 재발급 EndPoint")
    @PostMapping
    public ResponseEntity<Void> reissueTokens(
            @ExtractToken(tokenType = TokenType.REFRESH) final String refreshToken,
            final HttpServletResponse response
    ) {
        final AuthToken authToken = reissueTokenUseCase.invoke(refreshToken);
        tokenResponseWriter.applyToken(response, authToken);

        return ResponseEntity.noContent().build();
    }
}
