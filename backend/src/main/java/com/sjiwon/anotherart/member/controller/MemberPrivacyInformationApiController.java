package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.dto.SimpleReponseWrapper;
import com.sjiwon.anotherart.member.controller.dto.request.FindLoginIdRequest;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
