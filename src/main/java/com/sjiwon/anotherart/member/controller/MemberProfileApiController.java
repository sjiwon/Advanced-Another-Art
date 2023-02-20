package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.global.annotation.ExtractPayloadId;
import com.sjiwon.anotherart.member.service.MemberProfileService;
import com.sjiwon.anotherart.member.service.dto.response.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberId}")
public class MemberProfileApiController {
    private final MemberProfileService memberProfileService;

    @PreAuthorize("hasRole('USER') AND @memberDoubleChecker.isTrustworthyMember(#memberId, #payloadId)")
    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long memberId, @ExtractPayloadId Long payloadId) {
        return ResponseEntity.ok(memberProfileService.getUserProfile(memberId));
    }
}
