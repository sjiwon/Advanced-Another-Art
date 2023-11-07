package com.sjiwon.anotherart.auction.presentation;

import com.sjiwon.anotherart.auction.application.facade.BidFacade;
import com.sjiwon.anotherart.auction.presentation.dto.request.BidRequest;
import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions/{auctionId}/bid")
public class BidApiController {
    private final BidFacade bidFacade;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> bid(@ExtractPayload final Long memberId,
                                    @PathVariable final Long auctionId,
                                    @RequestBody @Valid final BidRequest request) {
        bidFacade.bid(auctionId, memberId, request.bidPrice());
        return ResponseEntity.noContent().build();
    }
}
