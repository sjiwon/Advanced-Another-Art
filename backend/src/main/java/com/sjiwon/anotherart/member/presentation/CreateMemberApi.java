package com.sjiwon.anotherart.member.presentation;

import com.sjiwon.anotherart.global.ResponseWrapper;
import com.sjiwon.anotherart.member.application.usecase.SignUpMemberUseCase;
import com.sjiwon.anotherart.member.application.usecase.ValidateMemberResourceUseCase;
import com.sjiwon.anotherart.member.application.usecase.command.ValidateMemberResourceCommand;
import com.sjiwon.anotherart.member.domain.model.MemberDuplicateResource;
import com.sjiwon.anotherart.member.presentation.request.SignUpRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 리소스 중복체크 & 회원가입 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class CreateMemberApi {
    private final ValidateMemberResourceUseCase validateMemberResourceUseCase;
    private final SignUpMemberUseCase signUpMemberUseCase;

    @Operation(summary = "사용자 리소스 사용 가능 여부 확인 EndPoint")
    @GetMapping("/duplicate/{resource}")
    public ResponseEntity<ResponseWrapper<Boolean>> checkDuplicateResource(
            @PathVariable final String resource,
            @RequestParam @NotBlank(message = "중복 체크 값은 필수입니다.") final String value
    ) {
        final boolean result = validateMemberResourceUseCase.useable(new ValidateMemberResourceCommand(
                MemberDuplicateResource.from(resource),
                value
        ));
        return ResponseEntity.ok(ResponseWrapper.from(result));
    }

    @Operation(summary = "회원가입 EndPoint")
    @PostMapping
    public ResponseEntity<ResponseWrapper<Long>> signUp(
            @RequestBody @Valid final SignUpRequest request
    ) {
        final Long memberId = signUpMemberUseCase.invoke(request.toCommand());
        return ResponseEntity.ok(ResponseWrapper.from(memberId));
    }
}
