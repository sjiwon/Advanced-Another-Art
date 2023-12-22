package com.sjiwon.anotherart.member.presentation;

import com.sjiwon.anotherart.member.application.usecase.SignUpMemberUseCase;
import com.sjiwon.anotherart.member.application.usecase.ValidateMemberResourceUseCase;
import com.sjiwon.anotherart.member.application.usecase.command.SignUpMemberCommand;
import com.sjiwon.anotherart.member.application.usecase.command.ValidateMemberResourceCommand;
import com.sjiwon.anotherart.member.domain.model.Address;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.MemberDuplicateResource;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Phone;
import com.sjiwon.anotherart.member.presentation.dto.request.MemberDuplicateCheckRequest;
import com.sjiwon.anotherart.member.presentation.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.presentation.dto.response.MemberIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "사용자 리소스 중복체크 & 회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {
    private final ValidateMemberResourceUseCase validateMemberResourceUseCase;
    private final SignUpMemberUseCase signUpMemberUseCase;

    @Operation(summary = "사용자 리소스 중복체크 EndPoint")
    @PostMapping("/duplicate/{resource}")
    public ResponseEntity<Void> checkDuplicateResource(
            @PathVariable final String resource,
            @RequestBody @Valid final MemberDuplicateCheckRequest request
    ) {
        validateMemberResourceUseCase.invoke(new ValidateMemberResourceCommand(MemberDuplicateResource.from(resource), request.value()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원가입 EndPoint")
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
}
