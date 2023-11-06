package com.sjiwon.anotherart.token.presentation;

import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.global.resolver.ExtractToken;
import com.sjiwon.anotherart.token.application.usecase.ReissueTokenUseCase;
import com.sjiwon.anotherart.token.application.usecase.command.ReissueTokenCommand;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "토큰 재발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class TokenReissueApiController {
    private final ReissueTokenUseCase reissueTokenUseCase;

    @Operation(summary = "RefreshToken을 통한 토큰 재발급 EndPoint")
    @PostMapping
    public ResponseEntity<AuthToken> reissueTokens(
            @ExtractPayload final Long memberId,
            @ExtractToken final String refreshToken
    ) {
        final AuthToken result = reissueTokenUseCase.reissueTokens(new ReissueTokenCommand(memberId, refreshToken));
        return ResponseEntity.ok(result);
    }
}
