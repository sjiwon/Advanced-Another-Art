package com.sjiwon.anotherart.purchase.controller;

import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import com.sjiwon.anotherart.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/art/{artId}/purchase")
public class PurchaseApiController {
    private final PurchaseService purchaseService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> purchaseArt(@PathVariable Long artId, @ExtractPayload Long memberId) {
        purchaseService.purchaseArt(artId, memberId);
        return ResponseEntity.noContent().build();
    }
}
