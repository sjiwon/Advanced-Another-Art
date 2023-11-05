package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.dto.SimpleReponseWrapper;
import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ChangePasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.FindLoginIdRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ResetPasswordRequest;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberPrivacyInformationApiController {
    private final MemberService memberService;

    @GetMapping("/login-id")
    public ResponseEntity<SimpleReponseWrapper<String>> findLoginId(@ModelAttribute @Valid final FindLoginIdRequest request) {
        final String loginId = memberService.findLoginId(request.name(), Email.from(request.email()));
        return ResponseEntity.ok(new SimpleReponseWrapper<>(loginId));
    }

    @PostMapping("/reset-password/auth")
    public ResponseEntity<Void> authForResetPassword(@RequestBody @Valid final AuthForResetPasswordRequest request) {
        memberService.authForResetPassword(request.name(), Email.from(request.email()), request.loginId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid final ResetPasswordRequest request) {
        memberService.resetPassword(request.loginId(), request.changePassword());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@ExtractPayload final Long memberId,
                                               @RequestBody @Valid final ChangePasswordRequest request) {
        memberService.changePassword(memberId, request.changePassword());
        return ResponseEntity.noContent().build();
    }
}
