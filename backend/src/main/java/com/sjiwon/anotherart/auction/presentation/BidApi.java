package com.sjiwon.anotherart.auction.presentation;

import com.sjiwon.anotherart.auction.application.usecase.BidUseCase;
import com.sjiwon.anotherart.auction.presentation.request.BidRequest;
import com.sjiwon.anotherart.global.annotation.Auth;
import com.sjiwon.anotherart.token.domain.model.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "경매 작품 입찰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions/{auctionId}/bid")
public class BidApi {
    private final BidUseCase bidUseCase;

    @Operation(summary = "경매 작품 입찰 Endpoint")
    @PostMapping
    public ResponseEntity<Void> bid(
            @Auth final Authenticated authenticated,
            @PathVariable final Long auctionId,
            @RequestBody @Valid final BidRequest request
    ) {
        bidUseCase.invoke(request.toCommand(authenticated.id(), auctionId));
        return ResponseEntity.noContent().build();
    }
}
