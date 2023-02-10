package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.domain.Address;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.Password;
import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import lombok.*;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignUpRequest {
    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_NAME)
    private String name;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_NICKNAME)
    private String nickname;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_LOGIN_ID)
    private String loginId;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_PASSWORD)
    private String password;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_SCHOOL)
    private String school;

    @NotNull(message = MemberRequestValidationMessage.SignUp.MEMBER_POSTCODE)
    private Integer postcode;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_DEFAULT_ADDRESS)
    private String defaultAddress;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_DETAIL_ADDRESS)
    private String detailAddress;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_PHONE)
    private String phone;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_EMAIL)
    private String email;

    public Member toMemberEntity() {
        return Member.builder()
                .name(name)
                .nickname(nickname)
                .loginId(loginId)
                .password(Password.encrypt(password, PasswordEncoderFactories.createDelegatingPasswordEncoder()))
                .school(school)
                .address(Address.of(postcode, defaultAddress, detailAddress))
                .phone(phone)
                .email(Email.from(email))
                .build();
    }
}
