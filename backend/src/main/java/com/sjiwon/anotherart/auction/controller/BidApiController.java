package com.sjiwon.anotherart.auction.controller;

import com.sjiwon.anotherart.auction.controller.dto.request.BidRequest;
import com.sjiwon.anotherart.auction.service.BidService;
import com.sjiwon.anotherart.token.utils.ExtractPayloadId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auction/{auctionId}/bid")
public class BidApiController {
    private final BidService bidService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> bid(@PathVariable Long auctionId, @ExtractPayloadId Long memberId, @Valid @RequestBody BidRequest request) {
        bidService.bid(auctionId, memberId, request.bidAmount());
        return ResponseEntity.noContent().build();
    }
}
