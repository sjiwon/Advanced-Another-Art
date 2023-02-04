package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import com.sjiwon.anotherart.global.annotation.RequiredToken;
import com.sjiwon.anotherart.global.dto.SimpleWrapper;
import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.FindIdRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ResetPasswordRequest;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Api(tags = "사용자 정보 관련 API")
public class MemberDetailApiController {
    private final MemberService memberService;

    @RequiredToken
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/nickname")
    @ApiOperation(value = "닉네임 수정 API", notes = "Access Token의 Payload를 통해서 memberId에 해당하는 사용자의 닉네임 수정")
    public ResponseEntity<Void> changeNickname(@ExtractPayload Long memberId, @RequestParam String changeNickname) {
        memberService.changeNickname(memberId, changeNickname);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/find/id")
    @ApiOperation(value = "아이디 찾기 API", notes = "이름, 이메일을 통해서 사용자 아이디 찾기")
    public ResponseEntity<SimpleWrapper<String>> findLoginId(@Valid @RequestBody FindIdRequest request) {
        String loginId = memberService.findLoginId(request.getName(), Email.from(request.getEmail()));
        return ResponseEntity.ok(new SimpleWrapper<>(loginId));
    }

    @PostMapping("/reset/password/auth")
    @ApiOperation(value = "비밀번호 재설정 간 사용자 인증 API", notes = "로그인 아이디, 이름, 이메일을 통해서 비밀번호 재설정 프로세스 간 사용자 인증")
    public ResponseEntity<Void> authMemberForResetPassword(@Valid @RequestBody AuthForResetPasswordRequest request) {
        memberService.authMemberForResetPassword(request.getName(), request.getLoginId(), Email.from(request.getEmail()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset/password")
    @ApiOperation(value = "비밀번호 재설정 API", notes = "로그인 아이디에 해당하는 사용자의 비밀번호를 변경")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        memberService.resetPassword(request.getLoginId(), request.getChangePassword());
        return ResponseEntity.noContent().build();
    }
}
