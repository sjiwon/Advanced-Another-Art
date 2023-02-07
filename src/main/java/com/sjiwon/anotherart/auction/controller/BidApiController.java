package com.sjiwon.anotherart.auction.controller;

import com.sjiwon.anotherart.auction.service.BidService;
import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auction/{auctionId}/bid")
@Api(tags = "경매 작품 비드 API")
public class BidApiController {
    private final BidService bidService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "경매 작품 입찰 API", notes = "경매 작품에 대한 입찰을 진행하는 APi")
    public ResponseEntity<Void> bid(@PathVariable Long auctionId, @ExtractPayload Long memberId, @RequestParam int bidAmount) {
        bidService.bid(auctionId, memberId, bidAmount);
        return ResponseEntity.noContent().build();
    }
}
