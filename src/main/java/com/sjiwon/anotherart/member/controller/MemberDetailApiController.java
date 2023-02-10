package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import com.sjiwon.anotherart.global.dto.SimpleWrapper;
import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ChangeNicknameRequest;
import com.sjiwon.anotherart.member.controller.dto.request.FindIdRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ResetPasswordRequest;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.service.MemberService;
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
    public ResponseEntity<Void> changeNickname(@ExtractPayload Long memberId, @Valid @RequestBody ChangeNicknameRequest request) {
        memberService.changeNickname(memberId, request.getChangeNickname());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/find/id")
    public ResponseEntity<SimpleWrapper<String>> findLoginId(@Valid @RequestBody FindIdRequest request) {
        String loginId = memberService.findLoginId(request.getName(), Email.from(request.getEmail()));
        return ResponseEntity.ok(new SimpleWrapper<>(loginId));
    }

    @PostMapping("/reset/password/auth")
    public ResponseEntity<Void> authMemberForResetPassword(@Valid @RequestBody AuthForResetPasswordRequest request) {
        memberService.authMemberForResetPassword(request.getName(), request.getLoginId(), Email.from(request.getEmail()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset/password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        memberService.resetPassword(request.getLoginId(), request.getChangePassword());
        return ResponseEntity.noContent().build();
    }
}
