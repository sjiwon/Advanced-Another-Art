package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.member.service.MemberService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Api(tags = "사용자 정보 관련 API")
public class MemberDetailApiController {
    private final MemberService memberService;

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/nickname")
    @ApiOperation(value = "닉네임 수정 API", notes = "Access Token의 Payload를 통해서 memberId에 해당하는 사용자의 닉네임 수정")
    public ResponseEntity<Void> changeNickname(@ExtractPayload Long memberId, @RequestParam String changeNickname) {
        memberService.changeNickname(memberId, changeNickname);
        return ResponseEntity.noContent().build();
    }
}
