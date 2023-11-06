package com.sjiwon.anotherart.member.presentation;


import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.member.application.MemberPointService;
import com.sjiwon.anotherart.member.presentation.dto.request.PointChargeRequest;
import com.sjiwon.anotherart.member.presentation.dto.request.PointRefundRequest;
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
@RequestMapping("/api/members/{memberId}/point")
public class MemberPointApiController {
    private final MemberPointService memberPointService;

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @PostMapping("/charge")
    public ResponseEntity<Void> chargePoint(@ExtractPayload final Long payloadId,
                                            @PathVariable final Long memberId,
                                            @RequestBody @Valid final PointChargeRequest request) {
        memberPointService.chargePoint(memberId, request.chargeAmount());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @PostMapping("/refund")
    public ResponseEntity<Void> refundPoint(@ExtractPayload final Long payloadId,
                                            @PathVariable final Long memberId,
                                            @RequestBody @Valid final PointRefundRequest request) {
        memberPointService.refundPoint(memberId, request.refundAmount());
        return ResponseEntity.noContent().build();
    }
}
