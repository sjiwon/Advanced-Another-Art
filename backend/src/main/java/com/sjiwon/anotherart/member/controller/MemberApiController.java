package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.member.controller.dto.request.MemberDuplicateCheckRequest;
import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberApiController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        Long savedMemberId = memberService.signUp(request.toEntity());

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/members/{id}").build(savedMemberId))
                .build();
    }

    @GetMapping("/check-duplicates")
    public ResponseEntity<Void> duplicateCheck(@ModelAttribute @Valid MemberDuplicateCheckRequest request) {
        memberService.duplicateCheck(request.resource(), request.value());
        return ResponseEntity.noContent().build();
    }
}
