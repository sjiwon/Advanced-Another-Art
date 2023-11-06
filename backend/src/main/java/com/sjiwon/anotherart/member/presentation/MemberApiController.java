package com.sjiwon.anotherart.member.presentation;

import com.sjiwon.anotherart.member.application.usecase.SignUpMemberUseCase;
import com.sjiwon.anotherart.member.application.usecase.ValidateResourceUseCase;
import com.sjiwon.anotherart.member.application.usecase.command.SignUpMemberCommand;
import com.sjiwon.anotherart.member.application.usecase.command.ValidateResouceCommand;
import com.sjiwon.anotherart.member.domain.model.Address;
import com.sjiwon.anotherart.member.domain.model.Email;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import static com.sjiwon.anotherart.member.domain.model.DuplicateResource.EMAIL;
import static com.sjiwon.anotherart.member.domain.model.DuplicateResource.LOGIN_ID;
import static com.sjiwon.anotherart.member.domain.model.DuplicateResource.NICKNAME;
import static com.sjiwon.anotherart.member.domain.model.DuplicateResource.PHONE;

@Tag(name = "사용자 리소스 중복 체크 & 회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {
    private final ValidateResourceUseCase validateResourceUseCase;
    private final SignUpMemberUseCase signUpMemberUseCase;

    @Operation(summary = "로그인 아이디 중복체크 EndPoint")
    @GetMapping("/duplicate/login-id")
    public ResponseEntity<Void> checkLoginId(@RequestBody @Valid final MemberDuplicateCheckRequest request) {
        validateResourceUseCase.invoke(new ValidateResouceCommand(LOGIN_ID, request.value()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "이메일 중복체크 EndPoint")
    @GetMapping("/duplicate/email")
    public ResponseEntity<Void> checkEmail(@RequestBody @Valid final MemberDuplicateCheckRequest request) {
        validateResourceUseCase.invoke(new ValidateResouceCommand(EMAIL, request.value()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "닉네임 중복체크 EndPoint")
    @GetMapping("/duplicate/nickname")
    public ResponseEntity<Void> checkNickname(@RequestBody @Valid final MemberDuplicateCheckRequest request) {
        validateResourceUseCase.invoke(new ValidateResouceCommand(NICKNAME, request.value()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전화번호 중복체크 EndPoint")
    @GetMapping("/duplicate/phone")
    public ResponseEntity<Void> checkPhone(@RequestBody @Valid final MemberDuplicateCheckRequest request) {
        validateResourceUseCase.invoke(new ValidateResouceCommand(PHONE, request.value()));
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
