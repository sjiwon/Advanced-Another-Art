package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.dto.SimpleReponseWrapper;
import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ChangePasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.FindLoginIdRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ResetPasswordRequest;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.service.MemberService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberPrivacyInformationApiController {
    private final MemberService memberService;

    @GetMapping("/login-id")
    public ResponseEntity<SimpleReponseWrapper<String>> findLoginId(@ModelAttribute @Valid FindLoginIdRequest request) {
        String loginId = memberService.findLoginId(request.name(), Email.from(request.email()));
        return ResponseEntity.ok(new SimpleReponseWrapper<>(loginId));
    }

    @PostMapping("/reset-password/auth")
    public ResponseEntity<Void> authForResetPassword(@RequestBody @Valid AuthForResetPasswordRequest request) {
        memberService.authForResetPassword(request.name(), Email.from(request.email()), request.loginId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        memberService.resetPassword(request.loginId(), request.changePassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@ExtractPayload Long memberId,
                                               @RequestBody @Valid ChangePasswordRequest request) {
        memberService.changePassword(memberId, request.changePassword());
        return ResponseEntity.noContent().build();
    }
}
