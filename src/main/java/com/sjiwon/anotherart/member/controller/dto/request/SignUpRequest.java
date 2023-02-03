package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.domain.Address;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.Password;
import com.sjiwon.anotherart.member.exception.MemberRequestValidationMessage;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "사용자 이름", example = "test", required = true)
    private String name;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_NICKNAME)
    @ApiModelProperty(value = "사용자 닉네임 (Unique)", example = "test", required = true)
    private String nickname;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_LOGIN_ID)
    @ApiModelProperty(value = "로그인 아이디 (Unique)", example = "test", required = true)
    private String loginId;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_PASSWORD)
    @ApiModelProperty(value = "로그인 비밀번호", example = "abcABC123!@#", required = true)
    private String password;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_SCHOOL)
    @ApiModelProperty(value = "사용자 재학중인 학교명", example = "경기대학교", required = true)
    private String school;

    @NotNull(message = MemberRequestValidationMessage.SignUp.MEMBER_POSTCODE)
    @ApiModelProperty(value = "사용자 주소 우편번호", example = "12345", required = true)
    private Integer postcode;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_DEFAULT_ADDRESS)
    @ApiModelProperty(value = "사용자 주소", example = "경기 성남시 분당구 정자일로 95", required = true)
    private String defaultAddress;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_DETAIL_ADDRESS)
    @ApiModelProperty(value = "사용자 상세 주소", example = "네이버", required = true)
    private String detailAddress;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_PHONE)
    @ApiModelProperty(value = "사용자 전화번호 (Unique)", example = "01012345678", required = true)
    private String phone;

    @NotBlank(message = MemberRequestValidationMessage.SignUp.MEMBER_EMAIL)
    @ApiModelProperty(value = "사용자 이메일 (Unique)", example = "test@gmail.com", required = true)
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
