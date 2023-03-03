package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/duplicate")
    public ResponseEntity<Void> duplicateCheck(@RequestParam String resource, @RequestParam String value) {
        memberService.duplicateCheck(resource, value);
        return ResponseEntity.noContent().build();
    }
}
