package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.member.controller.dto.request.ChangeAddressRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ChangeNicknameRequest;
import com.sjiwon.anotherart.member.service.MemberService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class MemberModifyApiController {
    private final MemberService memberService;

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @PatchMapping("/nickname")
    public ResponseEntity<Void> changeNickname(@ExtractPayload Long payloadId,
                                               @PathVariable Long memberId,
                                               @RequestBody @Valid ChangeNicknameRequest request) {
        memberService.changeNickname(memberId, request.value());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @PatchMapping("/address")
    public ResponseEntity<Void> changeAddress(@ExtractPayload Long payloadId,
                                              @PathVariable Long memberId,
                                              @RequestBody @Valid ChangeAddressRequest request) {
        memberService.changeAddress(memberId, request.postcode(), request.defaultAddress(), request.detailAddress());
        return ResponseEntity.noContent().build();
    }
}
