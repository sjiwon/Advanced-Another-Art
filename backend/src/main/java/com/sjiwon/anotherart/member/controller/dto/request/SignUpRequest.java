package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    public Member toEntity() {
        return Member.createMember(
                name,
                Nickname.from(nickname),
                loginId,
                Password.encrypt(password, new BCryptPasswordEncoder()),
                school,
                phone,
                Email.from(email),
                Address.of(postcode, defaultAddress, detailAddress)
        );
    }
}
