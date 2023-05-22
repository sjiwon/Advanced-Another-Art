package com.sjiwon.anotherart.auction.controller;

import com.sjiwon.anotherart.auction.controller.dto.request.BidRequest;
import com.sjiwon.anotherart.auction.facade.BidFacade;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions/{auctionId}/bid")
public class BidApiController {
    private final BidFacade bidFacade;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> bid(@ExtractPayload Long memberId,
                                    @PathVariable Long auctionId,
                                    @RequestBody @Valid BidRequest request) {
        bidFacade.bid(auctionId, memberId, request.bidPrice());
        return ResponseEntity.noContent().build();
    }
}
