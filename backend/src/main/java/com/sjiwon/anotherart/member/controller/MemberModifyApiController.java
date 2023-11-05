package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.member.controller.dto.request.ChangeAddressRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ChangeNicknameRequest;
import com.sjiwon.anotherart.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class MemberModifyApiController {
    private final MemberService memberService;

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @PatchMapping("/nickname")
    public ResponseEntity<Void> changeNickname(@ExtractPayload final Long payloadId,
                                               @PathVariable final Long memberId,
                                               @RequestBody @Valid final ChangeNicknameRequest request) {
        memberService.changeNickname(memberId, request.value());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER') AND @tokenPayloadChecker.isTrustworthyMember(#payloadId, #memberId)")
    @PatchMapping("/address")
    public ResponseEntity<Void> changeAddress(@ExtractPayload final Long payloadId,
                                              @PathVariable final Long memberId,
                                              @RequestBody @Valid final ChangeAddressRequest request) {
        memberService.changeAddress(memberId, request.postcode(), request.defaultAddress(), request.detailAddress());
        return ResponseEntity.noContent().build();
    }
}
