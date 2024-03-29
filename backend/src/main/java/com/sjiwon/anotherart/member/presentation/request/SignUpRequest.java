package com.sjiwon.anotherart.member.presentation.request;

import com.sjiwon.anotherart.member.application.usecase.command.SignUpMemberCommand;
import com.sjiwon.anotherart.member.domain.model.Address;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "아이디는 필수입니다.")
        String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @NotBlank(message = "학교 이름은 필수입니다.")
        String school,

        @NotBlank(message = "전화번호는 필수입니다.")
        String phone,

        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotNull(message = "우편번호는 필수입니다.")
        Integer postcode,

        @NotBlank(message = "주소는 필수입니다.")
        String defaultAddress,

        @NotBlank(message = "상세주소는 필수입니다.")
        String detailAddress
) {
    public SignUpMemberCommand toCommand() {
        return new SignUpMemberCommand(
                name,
                Nickname.from(nickname),
                loginId,
                password,
                school,
                Phone.from(phone),
                Email.from(email),
                Address.of(postcode, defaultAddress, detailAddress)
        );
    }
}
