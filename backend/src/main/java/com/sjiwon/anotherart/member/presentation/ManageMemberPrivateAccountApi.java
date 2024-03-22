package com.sjiwon.anotherart.member.presentation;

import com.sjiwon.anotherart.global.ResponseWrapper;
import com.sjiwon.anotherart.member.application.usecase.ResetPasswordUseCase;
import com.sjiwon.anotherart.member.application.usecase.RetrieveLoginIdUseCase;
import com.sjiwon.anotherart.member.presentation.request.ConfirmAuthCodeForLoginIdRequest;
import com.sjiwon.anotherart.member.presentation.request.ConfirmAuthCodeForResetPasswordRequest;
import com.sjiwon.anotherart.member.presentation.request.ProvideAuthCodeForLoginIdRequest;
import com.sjiwon.anotherart.member.presentation.request.ProvideAuthCodeForResetPasswordRequest;
import com.sjiwon.anotherart.member.presentation.request.ResetPasswordRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ## 아이디 찾기 & 비밀번호 재설정 프로세스 <br><br>
 * <p>
 * [아이디 찾기] <br>
 * 1. 이름 & 이메일로 인증번호 받기 API 요청 <br>
 * 2. 이름 & 이메일에 일치하는 Record 확인 후 인증번호 전송 (메일 전송 & 유효 시간 10분 Redis) <br>
 * 3. 인증번호 일치하면 아이디 Get <br>
 *
 * <br>
 * <p>
 * [비밀번호 재설정] <br>
 * 1. 이름 & 이메일 & 로그인 아이디로 인증번호 받기 API 요청 <br>
 * 2. 이름 & 이메일 & 로그인 아이디에 일치하는 Record 확인 후 인증번호 전송 (메일 전송 & 유효 시간 10분 Redis) <br>
 * 3. 인증번호 일치하면 비밀번호 재설정 페이지로 넘어가서 재설정 API 호출 <br>
 */

@Tag(name = "사용자 계정 관련 정보 조회 & 수정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class ManageMemberPrivateAccountApi {
    private final RetrieveLoginIdUseCase retrieveLoginIdUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    @PostMapping("/retrieve-login-id")
    public ResponseEntity<Void> provideAuthCodeForLoginId(
            @RequestBody @Valid final ProvideAuthCodeForLoginIdRequest request
    ) {
        retrieveLoginIdUseCase.provideAuthCode(request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/retrieve-login-id/confirm")
    public ResponseEntity<ResponseWrapper<String>> confirmAuthCodeForLoginId(
            @RequestBody @Valid final ConfirmAuthCodeForLoginIdRequest request
    ) {
        final String loginId = retrieveLoginIdUseCase.getLoginId(request.toCommand());
        return ResponseEntity.ok(ResponseWrapper.from(loginId));
    }

    @PostMapping("/reset-password/auth")
    public ResponseEntity<Void> provideAuthCodeForResetPassword(
            @RequestBody @Valid final ProvideAuthCodeForResetPasswordRequest request
    ) {
        resetPasswordUseCase.provideAuthCode(request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password/auth/confirm")
    public ResponseEntity<Void> confirmAuthCodeForResetPassword(
            @RequestBody @Valid final ConfirmAuthCodeForResetPasswordRequest request
    ) {
        resetPasswordUseCase.confirmAuthCode(request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid final ResetPasswordRequest request
    ) {
        resetPasswordUseCase.resetPassword(request.toCommand());
        return ResponseEntity.noContent().build();
    }
}
