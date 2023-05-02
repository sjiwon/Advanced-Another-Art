package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.dto.SimpleReponseWrapper;
import com.sjiwon.anotherart.member.infra.query.dto.response.UserPointHistory;
import com.sjiwon.anotherart.member.service.MemberProfileService;
import com.sjiwon.anotherart.member.service.MemberProfileWithArtService;
import com.sjiwon.anotherart.member.service.dto.response.UserProfile;
import com.sjiwon.anotherart.member.service.dto.response.UserTradedArt;
import com.sjiwon.anotherart.member.service.dto.response.UserWinningAuction;
import com.sjiwon.anotherart.token.utils.ExtractPayloadId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class MemberProfileApiController {
    private final MemberProfileService memberProfileService;
    private final MemberProfileWithArtService memberProfileWithArtService;

    @PreAuthorize("hasRole('USER') AND @memberDoubleChecker.isTrustworthyMember(#memberId, #payloadId)")
    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long memberId, @ExtractPayloadId Long payloadId) {
        return ResponseEntity.ok(memberProfileService.getUserProfile(memberId));
    }

    @PreAuthorize("hasRole('USER') AND @memberDoubleChecker.isTrustworthyMember(#memberId, #payloadId)")
    @GetMapping("/points")
    public ResponseEntity<SimpleReponseWrapper<List<UserPointHistory>>> getUserPointHistory(@PathVariable Long memberId, @ExtractPayloadId Long payloadId) {
        return ResponseEntity.ok(new SimpleReponseWrapper<>(memberProfileService.getUserPointHistory(memberId)));
    }

    @PreAuthorize("hasRole('USER') AND @memberDoubleChecker.isTrustworthyMember(#memberId, #payloadId)")
    @GetMapping("/winning-auctions")
    public ResponseEntity<SimpleReponseWrapper<List<UserWinningAuction>>> getWinningAuction(@PathVariable Long memberId, @ExtractPayloadId Long payloadId) {
        return ResponseEntity.ok(new SimpleReponseWrapper<>(memberProfileWithArtService.getWinningAuction(memberId)));
    }

    @PreAuthorize("hasRole('USER') AND @memberDoubleChecker.isTrustworthyMember(#memberId, #payloadId)")
    @GetMapping("/auctions/sold")
    public ResponseEntity<SimpleReponseWrapper<List<UserTradedArt>>> getSoldAuctionArt(@PathVariable Long memberId, @ExtractPayloadId Long payloadId) {
        return ResponseEntity.ok(new SimpleReponseWrapper<>(memberProfileWithArtService.getSoldAuctionArt(memberId)));
    }

    @PreAuthorize("hasRole('USER') AND @memberDoubleChecker.isTrustworthyMember(#memberId, #payloadId)")
    @GetMapping("/generals/sold")
    public ResponseEntity<SimpleReponseWrapper<List<UserTradedArt>>> getSoldGeneralArt(@PathVariable Long memberId, @ExtractPayloadId Long payloadId) {
        return ResponseEntity.ok(new SimpleReponseWrapper<>(memberProfileWithArtService.getSoldGeneralArt(memberId)));
    }

    @PreAuthorize("hasRole('USER') AND @memberDoubleChecker.isTrustworthyMember(#memberId, #payloadId)")
    @GetMapping("/auctions/purchase")
    public ResponseEntity<SimpleReponseWrapper<List<UserTradedArt>>> getPurchaseAuctionArt(@PathVariable Long memberId, @ExtractPayloadId Long payloadId) {
        return ResponseEntity.ok(new SimpleReponseWrapper<>(memberProfileWithArtService.getPurchaseAuctionArt(memberId)));
    }

    @PreAuthorize("hasRole('USER') AND @memberDoubleChecker.isTrustworthyMember(#memberId, #payloadId)")
    @GetMapping("/generals/purchase")
    public ResponseEntity<SimpleReponseWrapper<List<UserTradedArt>>> getPurchaseGeneralArt(@PathVariable Long memberId, @ExtractPayloadId Long payloadId) {
        return ResponseEntity.ok(new SimpleReponseWrapper<>(memberProfileWithArtService.getPurchaseGeneralArt(memberId)));
    }
}
