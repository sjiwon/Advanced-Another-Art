package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.member.controller.dto.request.DuplicateCheckRequest;
import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.service.MemberService;
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
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        memberService.signUp(request.toMemberEntity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/duplicate-check")
    public ResponseEntity<Void> duplicateCheck(@Valid @RequestBody DuplicateCheckRequest request) {
        memberService.duplicateCheck(request.getResource(), request.getValue());
        return ResponseEntity.noContent().build();
    }
}
