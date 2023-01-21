package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Api(tags = "사용자 API")
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping
    @ApiOperation(value = "회원가입 API", notes = "회원가입 요청 정보를 토대로 회원가입 진행")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        memberService.signUp(request.toMemberEntity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
