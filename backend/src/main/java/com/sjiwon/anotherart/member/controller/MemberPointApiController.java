package com.sjiwon.anotherart.member.controller;


import com.sjiwon.anotherart.member.controller.dto.request.PointChargeRequest;
import com.sjiwon.anotherart.member.controller.dto.request.PointRefundRequest;
import com.sjiwon.anotherart.member.service.MemberPointService;
import com.sjiwon.anotherart.token.utils.ExtractPayloadId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/point")
public class MemberPointApiController {
    private final MemberPointService memberPointService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/charge")
    public ResponseEntity<Void> chargePoint(@ExtractPayloadId Long memberId, @Valid @RequestBody PointChargeRequest request) {
        memberPointService.chargePoint(memberId, request.getChargeAmount());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/refund")
    public ResponseEntity<Void> refundPoint(@ExtractPayloadId Long memberId, @Valid @RequestBody PointRefundRequest request) {
        memberPointService.refundPoint(memberId, request.getRefundAmount());
        return ResponseEntity.noContent().build();
    }
}
