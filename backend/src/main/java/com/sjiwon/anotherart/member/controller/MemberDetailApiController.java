package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.dto.SimpleReponseWrapper;
import com.sjiwon.anotherart.member.controller.dto.request.*;
import com.sjiwon.anotherart.member.service.MemberService;
import com.sjiwon.anotherart.token.utils.ExtractPayloadId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberDetailApiController {
    private final MemberService memberService;

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/nickname")
    public ResponseEntity<Void> changeNickname(@ExtractPayloadId Long memberId, @Valid @RequestBody ChangeNicknameRequest request) {
        memberService.changeNickname(memberId, request.changeNickname());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@ExtractPayloadId Long memberId, @Valid @RequestBody ChangePasswordRequest request) {
        memberService.changePassword(memberId, request.changePassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/id")
    public ResponseEntity<SimpleReponseWrapper<String>> findLoginId(@Valid @ModelAttribute FindIdRequest request) {
        String loginId = memberService.findLoginId(request.name(), request.email());
        return ResponseEntity.ok(new SimpleReponseWrapper<>(loginId));
    }

    @PostMapping("/reset-password/auth")
    public ResponseEntity<Void> authMemberForPasswordReset(@Valid @RequestBody AuthForResetPasswordRequest request) {
        memberService.authMemberForPasswordReset(request.name(), request.loginId(), request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        memberService.resetPassword(request.loginId(), request.changePassword());
        return ResponseEntity.noContent().build();
    }
}
