package com.sjiwon.anotherart.member.controller;


import com.sjiwon.anotherart.member.service.MemberPointService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "사용자 포인트 관련 API")
public class MemberPointApiController {
    private final MemberPointService memberPointService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/charge")
    @ApiOperation(value = "포인트 충전 API", notes = "포인트 충전을 위한 API")
    public ResponseEntity<Void> chargePoint(@ExtractPayload Long memberId, @RequestParam int chargeAmount) {
        memberPointService.chargePoint(memberId, chargeAmount);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/refund")
    @ApiOperation(value = "포인트 환불 API", notes = "포인트 환불을 위한 API")
    public ResponseEntity<Void> refundPoint(@ExtractPayload Long memberId, @RequestParam int refundAmount) {
        memberPointService.refundPoint(memberId, refundAmount);
        return ResponseEntity.noContent().build();
    }
}
