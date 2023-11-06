package com.sjiwon.anotherart.member.presentation;

import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.member.application.usecase.UpdateResourceUseCase;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateAddressCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateNicknameCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdatePasswordCommand;
import com.sjiwon.anotherart.member.presentation.dto.request.UpdateAddressRequest;
import com.sjiwon.anotherart.member.presentation.dto.request.UpdateNicknameRequest;
import com.sjiwon.anotherart.member.presentation.dto.request.UpdatePasswordRequest;
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
    private final UpdateResourceUseCase updateResourceUseCase;

    @Operation(summary = "닉네임 수정 EndPoint")
    @PatchMapping("/nickname")
    public ResponseEntity<Void> updateNickname(
            @ExtractPayload final Long memberId,
            @RequestBody @Valid final UpdateNicknameRequest request
    ) {
        updateResourceUseCase.updateNickname(new UpdateNicknameCommand(memberId, request.value()));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "주소 수정 EndPoint")
    @PatchMapping("/address")
    public ResponseEntity<Void> updateAddress(
            @ExtractPayload final Long memberId,
            @RequestBody @Valid final UpdateAddressRequest request
    ) {
        updateResourceUseCase.updateAddress(new UpdateAddressCommand(
                memberId,
                request.postcode(),
                request.defaultAddress(),
                request.detailAddress()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "비밀번호 수정 EndPoint")
    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @ExtractPayload final Long memberId,
            @RequestBody @Valid final UpdatePasswordRequest request
    ) {
        updateResourceUseCase.updatePassword(new UpdatePasswordCommand(memberId, request.value()));
        return ResponseEntity.noContent().build();
    }
}
