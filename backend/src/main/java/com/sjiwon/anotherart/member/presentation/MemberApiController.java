package com.sjiwon.anotherart.member.presentation;

import com.sjiwon.anotherart.member.application.MemberService;
import com.sjiwon.anotherart.member.application.usecase.SignUpMemberUseCase;
import com.sjiwon.anotherart.member.application.usecase.command.SignUpMemberCommand;
import com.sjiwon.anotherart.member.domain.model.Address;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Phone;
import com.sjiwon.anotherart.member.presentation.dto.request.MemberDuplicateCheckRequest;
import com.sjiwon.anotherart.member.presentation.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.presentation.dto.response.MemberIdResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {
    private final SignUpMemberUseCase signUpMemberUseCase;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberIdResponse> signUp(@RequestBody @Valid final SignUpRequest request) {
        final Long savedMemberId = signUpMemberUseCase.invoke(new SignUpMemberCommand(
                request.name(),
                Nickname.from(request.nickname()),
                request.loginId(),
                request.password(),
                request.school(),
                Phone.from(request.phone()),
                Email.from(request.email()),
                Address.of(request.postcode(), request.defaultAddress(), request.detailAddress())
        ));

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/members/{id}").build(savedMemberId))
                .body(new MemberIdResponse(savedMemberId));
    }

    @GetMapping("/check-duplicates")
    public ResponseEntity<Void> duplicateCheck(@ModelAttribute @Valid final MemberDuplicateCheckRequest request) {
        memberService.duplicateCheck(request.resource(), request.value());
        return ResponseEntity.noContent().build();
    }
}
