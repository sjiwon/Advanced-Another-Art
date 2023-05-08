package com.sjiwon.anotherart.member.controller;


import com.sjiwon.anotherart.member.controller.dto.request.PointChargeRequest;
import com.sjiwon.anotherart.member.controller.dto.request.PointRefundRequest;
import com.sjiwon.anotherart.member.service.MemberPointService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}/point")
public class MemberPointApiController {
    private final MemberPointService memberPointService;

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @PostMapping("/charge")
    public ResponseEntity<Void> chargePoint(@ExtractPayload Long payloadId,
                                            @PathVariable Long memberId,
                                            @RequestBody @Valid PointChargeRequest request) {
        memberPointService.chargePoint(memberId, request.chargeAmount());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @PostMapping("/refund")
    public ResponseEntity<Void> refundPoint(@ExtractPayload Long payloadId,
                                            @PathVariable Long memberId,
                                            @RequestBody @Valid PointRefundRequest request) {
        memberPointService.refundPoint(memberId, request.refundAmount());
        return ResponseEntity.noContent().build();
    }
}
