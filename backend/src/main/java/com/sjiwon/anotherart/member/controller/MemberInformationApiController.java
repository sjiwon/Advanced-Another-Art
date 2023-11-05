package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.member.service.MemberInformationService;
import com.sjiwon.anotherart.member.service.dto.response.MemberInformation;
import com.sjiwon.anotherart.member.service.dto.response.PointRecordAssembler;
import com.sjiwon.anotherart.member.service.dto.response.TradedArtAssembler;
import com.sjiwon.anotherart.member.service.dto.response.WinningAuctionArtAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class MemberInformationApiController {
    private final MemberInformationService memberInformationService;

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @GetMapping
    public ResponseEntity<MemberInformation> getInformation(@ExtractPayload final Long payloadId, @PathVariable final Long memberId) {
        final MemberInformation response = memberInformationService.getInformation(memberId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @GetMapping("/points")
    public ResponseEntity<PointRecordAssembler> getPointRecords(@ExtractPayload final Long payloadId, @PathVariable final Long memberId) {
        final PointRecordAssembler response = memberInformationService.getPointRecords(memberId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @GetMapping("/winning-auctions")
    public ResponseEntity<WinningAuctionArtAssembler> getWinningAuctionArts(@ExtractPayload final Long payloadId, @PathVariable final Long memberId) {
        final WinningAuctionArtAssembler response = memberInformationService.getWinningAuctionArts(memberId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @GetMapping("/arts/sold")
    public ResponseEntity<TradedArtAssembler> getSoldArts(@ExtractPayload final Long payloadId, @PathVariable final Long memberId) {
        final TradedArtAssembler response = memberInformationService.getSoldArts(memberId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @GetMapping("/arts/purchase")
    public ResponseEntity<TradedArtAssembler> getPurchaseArts(@ExtractPayload final Long payloadId, @PathVariable final Long memberId) {
        final TradedArtAssembler response = memberInformationService.getPurchaseArts(memberId);
        return ResponseEntity.ok(response);
    }
}
