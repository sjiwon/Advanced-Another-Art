package com.sjiwon.anotherart.member.controller;


import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import com.sjiwon.anotherart.member.service.MemberPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/point")
public class MemberPointApiController {
    private final MemberPointService memberPointService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/charge")
    public ResponseEntity<Void> chargePoint(@ExtractPayload Long memberId, @RequestParam int chargeAmount) {
        memberPointService.chargePoint(memberId, chargeAmount);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/refund")
    public ResponseEntity<Void> refundPoint(@ExtractPayload Long memberId, @RequestParam int refundAmount) {
        memberPointService.refundPoint(memberId, refundAmount);
        return ResponseEntity.noContent().build();
    }
}
