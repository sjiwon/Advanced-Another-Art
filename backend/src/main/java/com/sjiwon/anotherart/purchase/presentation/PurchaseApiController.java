package com.sjiwon.anotherart.purchase.presentation;

import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.purchase.application.usecase.PurchaseArtUseCase;
import com.sjiwon.anotherart.purchase.application.usecase.command.PurchaseArtCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "작품 구매 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts/{artId}/purchase")
public class PurchaseApiController {
    private final PurchaseArtUseCase purchaseArtUseCase;

    @Operation(summary = "작품 구매 Endpoint")
    @PostMapping
    public ResponseEntity<Void> purchaseArt(
            @ExtractPayload final Long memberId,
            @PathVariable final Long artId
    ) {
        purchaseArtUseCase.invoke(new PurchaseArtCommand(memberId, artId));
        return ResponseEntity.noContent().build();
    }
}
