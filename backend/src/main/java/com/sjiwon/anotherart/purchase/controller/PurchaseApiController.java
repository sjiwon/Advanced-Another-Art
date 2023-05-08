package com.sjiwon.anotherart.purchase.controller;

import com.sjiwon.anotherart.purchase.service.PurchaseService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts/{artId}/purchase")
public class PurchaseApiController {
    private final PurchaseService purchaseService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> purchaseArt(@ExtractPayload Long memberId, @PathVariable Long artId) {
        purchaseService.purchaseArt(artId, memberId);
        return ResponseEntity.noContent().build();
    }
}
