package com.sjiwon.anotherart.member.presentation;

import com.sjiwon.anotherart.global.annotation.Auth;
import com.sjiwon.anotherart.member.application.usecase.UpdateMemberResourceUseCase;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateAddressCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateNicknameCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdatePasswordCommand;
import com.sjiwon.anotherart.member.presentation.request.UpdateAddressRequest;
import com.sjiwon.anotherart.member.presentation.request.UpdateNicknameRequest;
import com.sjiwon.anotherart.member.presentation.request.UpdatePasswordRequest;
import com.sjiwon.anotherart.token.domain.model.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자 리소스 수정 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/me")
public class MemberModifyApiController {
    private final UpdateMemberResourceUseCase updateMemberResourceUseCase;

    @Operation(summary = "닉네임 수정 EndPoint")
    @PatchMapping("/nickname")
    public ResponseEntity<Void> updateNickname(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final UpdateNicknameRequest request
    ) {
        updateMemberResourceUseCase.updateNickname(new UpdateNicknameCommand(authenticated.id(), request.value()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "주소 수정 EndPoint")
    @PatchMapping("/address")
    public ResponseEntity<Void> updateAddress(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final UpdateAddressRequest request
    ) {
        updateMemberResourceUseCase.updateAddress(new UpdateAddressCommand(
                authenticated.id(),
                request.postcode(),
                request.defaultAddress(),
                request.detailAddress()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "비밀번호 수정 EndPoint")
    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @Auth final Authenticated authenticated,
            @RequestBody @Valid final UpdatePasswordRequest request
    ) {
        updateMemberResourceUseCase.updatePassword(new UpdatePasswordCommand(authenticated.id(), request.value()));
        return ResponseEntity.noContent().build();
    }
}
